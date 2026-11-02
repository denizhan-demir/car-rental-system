package hu.nye.carrental.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Opens connections to the SQLite database and creates the tables.
 * The whole database is one file (carrental.db) in the project folder.
 */
public final class Database {

    public static final String FILE_NAME = "carrental.db";
    private static final String URL = "jdbc:sqlite:" + FILE_NAME;

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        // SQLite ignores foreign keys unless this is switched on for every connection
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    /**
     * Creates the tables (safe on every start) and, on the very first start,
     * fills the database with sample data.
     */
    public static void init() {
        // must be checked before connecting, because connecting creates the file
        boolean firstStart = !Files.exists(Paths.get(FILE_NAME));
        try (Connection conn = getConnection()) {
            runScript(conn, "/db/schema.sql");
            if (firstStart) {
                runScript(conn, "/db/seed.sql");
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Could not initialize the database", e);
        }
    }

    /** Runs every statement of an .sql file from src/main/resources in one transaction. */
    private static void runScript(Connection conn, String resource) throws IOException, SQLException {
        String sql;
        try (InputStream in = Database.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IOException(resource + " not found in resources");
            }
            sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        // drop the comment lines, then split the script into statements at ';'
        String withoutComments = sql.lines()
                .filter(line -> !line.trim().startsWith("--"))
                .collect(Collectors.joining("\n"));

        conn.setAutoCommit(false);
        try (Statement st = conn.createStatement()) {
            for (String statement : withoutComments.split(";")) {
                if (!statement.isBlank()) {
                    st.execute(statement);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback(); // all or nothing
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /** Run this class on its own to check the database: prints how many rows each table has. */
    public static void main(String[] args) throws SQLException {
        init();
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            for (String table : new String[]{"brand", "category", "car", "customer", "rental"}) {
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
                    rs.next();
                    System.out.printf("%-9s %d rows%n", table, rs.getInt(1));
                }
            }
        }
    }
}
