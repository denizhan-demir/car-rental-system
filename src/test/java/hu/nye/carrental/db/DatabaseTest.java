package hu.nye.carrental.db;

import hu.nye.carrental.DatabaseTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the database setup: tables, sample data and the rules in schema.sql.
 */
class DatabaseTest extends DatabaseTestBase {

    @Test
    @DisplayName("The first start loads the sample data")
    void firstStartLoadsSampleData() throws SQLException {
        assertEquals(8, count("brand"));
        assertEquals(6, count("category"));
        assertEquals(12, count("car"));
        assertEquals(6, count("customer"));
        assertEquals(7, count("rental"));
    }

    @Test
    @DisplayName("A second start does not add the sample data again")
    void secondStartDoesNotDuplicateSampleData() throws SQLException {
        Database.init(); // the database file exists now, like on every later start of the app
        assertEquals(12, count("car"));
        assertEquals(7, count("rental"));
    }

    @Test
    @DisplayName("Foreign keys stop deleting a brand that cars still use")
    void foreignKeysAreEnforced() {
        SQLException e = assertThrows(SQLException.class,
                () -> execute("DELETE FROM brand WHERE name = 'Toyota'"));
        assertTrue(e.getMessage().contains("FOREIGN KEY"));
    }

    @Test
    @DisplayName("Only Manual or Automatic is accepted as gearbox")
    void invalidGearboxIsRejected() {
        assertThrows(SQLException.class, () -> execute(
                "INSERT INTO car (brand_id, category_id, model, plate_number, year, daily_price, transmission) "
                        + "VALUES (1, 1, 'Test', 'TEST-001', 2024, 30, 'Robot')"));
    }

    private int count(String table) throws SQLException {
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private void execute(String sql) throws SQLException {
        try (Connection conn = Database.getConnection(); Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }
}
