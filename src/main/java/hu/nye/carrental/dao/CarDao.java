package hu.nye.carrental.dao;

import hu.nye.carrental.db.Database;
import hu.nye.carrental.model.Car;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * All SQL for the car table (CRUD).
 */
public class CarDao {

    // READ – joins brand and category so the table can show names instead of ids.
    // "available" is 1 if the car has no rental without a return date.
    private static final String SELECT_ALL = """
            SELECT c.id, c.brand_id, c.category_id, c.model, c.plate_number, c.year, c.daily_price,
                   c.seats, c.transmission, c.fuel_type,
                   b.name   AS brand_name,
                   cat.name AS category_name,
                   NOT EXISTS (SELECT 1 FROM rental r
                               WHERE r.car_id = c.id AND r.return_date IS NULL) AS available
            FROM car c
            JOIN brand b      ON b.id = c.brand_id
            JOIN category cat ON cat.id = c.category_id
            ORDER BY b.name, c.model
            """;

    public List<Car> findAll() throws SQLException {
        List<Car> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        }
        return result;
    }

    // CREATE
    public void insert(Car car) throws SQLException {
        String sql = "INSERT INTO car (brand_id, category_id, model, plate_number, year, daily_price, "
                + "seats, transmission, fuel_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fillParameters(ps, car);
            ps.executeUpdate();
        }
    }

    // UPDATE
    public void update(Car car) throws SQLException {
        String sql = "UPDATE car SET brand_id = ?, category_id = ?, model = ?, plate_number = ?, year = ?, "
                + "daily_price = ?, seats = ?, transmission = ?, fuel_type = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fillParameters(ps, car);
            ps.setInt(10, car.getId());
            ps.executeUpdate();
        }
    }

    // DELETE
    public void delete(Car car) throws SQLException {
        String sql = "DELETE FROM car WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, car.getId());
            ps.executeUpdate();
        }
    }

    /** The first 9 parameters are the same for INSERT and UPDATE. */
    private void fillParameters(PreparedStatement ps, Car car) throws SQLException {
        ps.setInt(1, car.getBrandId());
        ps.setInt(2, car.getCategoryId());
        ps.setString(3, car.getModel());
        ps.setString(4, car.getPlateNumber());
        ps.setInt(5, car.getYear());
        ps.setDouble(6, car.getDailyPrice());
        ps.setInt(7, car.getSeats());
        ps.setString(8, car.getTransmission());
        ps.setString(9, car.getFuelType());
    }

    private Car mapRow(ResultSet rs) throws SQLException {
        Car car = new Car();
        car.setId(rs.getInt("id"));
        car.setBrandId(rs.getInt("brand_id"));
        car.setCategoryId(rs.getInt("category_id"));
        car.setModel(rs.getString("model"));
        car.setPlateNumber(rs.getString("plate_number"));
        car.setYear(rs.getInt("year"));
        car.setDailyPrice(rs.getDouble("daily_price"));
        car.setSeats(rs.getInt("seats"));
        car.setTransmission(rs.getString("transmission"));
        car.setFuelType(rs.getString("fuel_type"));
        car.setBrandName(rs.getString("brand_name"));
        car.setCategoryName(rs.getString("category_name"));
        car.setAvailable(rs.getBoolean("available"));
        return car;
    }
}
