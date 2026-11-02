package hu.nye.carrental.dao;

import hu.nye.carrental.db.Database;
import hu.nye.carrental.model.Rental;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * All SQL for the rental table: create, list, close (return) and delete.
 */
public class RentalDao {

    // READ – active rentals first, then the newest ones
    private static final String SELECT_ALL = """
            SELECT r.id, r.car_id, r.customer_id, r.start_date, r.end_date, r.return_date, r.total_price,
                   b.name || ' ' || c.model || ' (' || c.plate_number || ')' AS car_label,
                   cu.first_name || ' ' || cu.last_name AS customer_name,
                   c.daily_price
            FROM rental r
            JOIN car c       ON c.id = r.car_id
            JOIN brand b     ON b.id = c.brand_id
            JOIN customer cu ON cu.id = r.customer_id
            ORDER BY (r.return_date IS NULL) DESC, r.start_date DESC, r.id DESC
            """;

    public List<Rental> findAll() throws SQLException {
        List<Rental> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        }
        return result;
    }

    /**
     * CREATE – checks that the car is free and inserts the rental in one transaction,
     * so the same car can never have two active rentals.
     */
    public void create(Rental rental) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM rental WHERE car_id = ? AND return_date IS NULL";
        String insertSql = "INSERT INTO rental (car_id, customer_id, start_date, end_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                    check.setInt(1, rental.getCarId());
                    try (ResultSet rs = check.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            throw new IllegalStateException("This car is already rented.");
                        }
                    }
                }
                try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                    insert.setInt(1, rental.getCarId());
                    insert.setInt(2, rental.getCustomerId());
                    insert.setString(3, rental.getStartDate().toString()); // yyyy-MM-dd
                    insert.setString(4, rental.getEndDate().toString());
                    insert.executeUpdate();
                }
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback(); // undo everything if something went wrong
                throw e;
            }
        }
    }

    /** UPDATE – closes an active rental when the car comes back. */
    public void close(int rentalId, LocalDate returnDate, double totalPrice) throws SQLException {
        String sql = "UPDATE rental SET return_date = ?, total_price = ? WHERE id = ? AND return_date IS NULL";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, returnDate.toString());
            ps.setDouble(2, totalPrice);
            ps.setInt(3, rentalId);
            ps.executeUpdate();
        }
    }

    // DELETE
    public void delete(Rental rental) throws SQLException {
        String sql = "DELETE FROM rental WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rental.getId());
            ps.executeUpdate();
        }
    }

    private Rental mapRow(ResultSet rs) throws SQLException {
        Rental rental = new Rental();
        rental.setId(rs.getInt("id"));
        rental.setCarId(rs.getInt("car_id"));
        rental.setCustomerId(rs.getInt("customer_id"));
        rental.setStartDate(LocalDate.parse(rs.getString("start_date")));
        rental.setEndDate(LocalDate.parse(rs.getString("end_date")));

        String returnDate = rs.getString("return_date");
        rental.setReturnDate(returnDate == null ? null : LocalDate.parse(returnDate));

        double totalPrice = rs.getDouble("total_price");
        rental.setTotalPrice(rs.wasNull() ? null : totalPrice); // NULL while the rental is active

        rental.setCarLabel(rs.getString("car_label"));
        rental.setCustomerName(rs.getString("customer_name"));
        rental.setDailyPrice(rs.getDouble("daily_price"));
        return rental;
    }
}
