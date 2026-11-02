package hu.nye.carrental.dao;

import hu.nye.carrental.db.Database;
import hu.nye.carrental.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * All SQL for the customer table (CRUD).
 */
public class CustomerDao {

    // READ
    public List<Customer> findAll() throws SQLException {
        String sql = "SELECT id, first_name, last_name, email, phone, license_number "
                + "FROM customer ORDER BY last_name, first_name";
        List<Customer> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("id"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setLicenseNumber(rs.getString("license_number"));
                result.add(customer);
            }
        }
        return result;
    }

    // CREATE
    public void insert(Customer customer) throws SQLException {
        String sql = "INSERT INTO customer (first_name, last_name, email, phone, license_number) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fillParameters(ps, customer);
            ps.executeUpdate();
        }
    }

    // UPDATE
    public void update(Customer customer) throws SQLException {
        String sql = "UPDATE customer SET first_name = ?, last_name = ?, email = ?, phone = ?, "
                + "license_number = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fillParameters(ps, customer);
            ps.setInt(6, customer.getId());
            ps.executeUpdate();
        }
    }

    // DELETE
    public void delete(Customer customer) throws SQLException {
        String sql = "DELETE FROM customer WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customer.getId());
            ps.executeUpdate();
        }
    }

    private void fillParameters(PreparedStatement ps, Customer customer) throws SQLException {
        ps.setString(1, customer.getFirstName());
        ps.setString(2, customer.getLastName());
        ps.setString(3, customer.getEmail());   // may be null (optional)
        ps.setString(4, customer.getPhone());   // may be null (optional)
        ps.setString(5, customer.getLicenseNumber());
    }
}
