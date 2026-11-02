package hu.nye.carrental.dao;

import hu.nye.carrental.db.Database;
import hu.nye.carrental.model.Brand;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * All SQL for the brand table (CRUD).
 */
public class BrandDao implements NameDao<Brand> {

    // READ
    @Override
    public List<Brand> findAll() throws SQLException {
        String sql = "SELECT id, name FROM brand ORDER BY name";
        List<Brand> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Brand(rs.getInt("id"), rs.getString("name")));
            }
        }
        return result;
    }

    // CREATE
    @Override
    public void insert(String name) throws SQLException {
        String sql = "INSERT INTO brand (name) VALUES (?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
        }
    }

    // UPDATE
    @Override
    public void rename(Brand item, String newName) throws SQLException {
        String sql = "UPDATE brand SET name = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setInt(2, item.getId());
            ps.executeUpdate();
        }
    }

    // DELETE
    @Override
    public void delete(Brand item) throws SQLException {
        String sql = "DELETE FROM brand WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, item.getId());
            ps.executeUpdate();
        }
    }
}
