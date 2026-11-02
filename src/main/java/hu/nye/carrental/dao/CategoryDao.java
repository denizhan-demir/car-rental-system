package hu.nye.carrental.dao;

import hu.nye.carrental.db.Database;
import hu.nye.carrental.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * All SQL for the category table (CRUD).
 */
public class CategoryDao implements NameDao<Category> {

    // READ
    @Override
    public List<Category> findAll() throws SQLException {
        String sql = "SELECT id, name FROM category ORDER BY name";
        List<Category> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        }
        return result;
    }

    // CREATE
    @Override
    public void insert(String name) throws SQLException {
        String sql = "INSERT INTO category (name) VALUES (?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
        }
    }

    // UPDATE
    @Override
    public void rename(Category item, String newName) throws SQLException {
        String sql = "UPDATE category SET name = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setInt(2, item.getId());
            ps.executeUpdate();
        }
    }

    // DELETE
    @Override
    public void delete(Category item) throws SQLException {
        String sql = "DELETE FROM category WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, item.getId());
            ps.executeUpdate();
        }
    }
}
