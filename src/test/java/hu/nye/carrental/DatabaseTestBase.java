package hu.nye.carrental;

import hu.nye.carrental.db.Database;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

/**
 * Base class for tests that need the database.
 * Before every test it creates a brand-new database in a temporary folder and fills it with
 * the sample data (seed.sql), so every test starts from the same state.
 * The real carrental.db in the project folder is never touched.
 */
public abstract class DatabaseTestBase {

    @TempDir
    protected Path tempDir; // JUnit creates this folder before each test and deletes it afterwards

    @BeforeEach
    protected void createTestDatabase() {
        System.setProperty(Database.FILE_PROPERTY, tempDir.resolve("test.db").toString());
        Database.init();
    }

    @AfterEach
    protected void useRealDatabaseAgain() {
        System.clearProperty(Database.FILE_PROPERTY);
    }
}
