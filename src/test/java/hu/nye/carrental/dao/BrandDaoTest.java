package hu.nye.carrental.dao;

import hu.nye.carrental.DatabaseTestBase;
import hu.nye.carrental.model.Brand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests CRUD for brands. CategoryDao works the same way (both implement NameDao).
 */
class BrandDaoTest extends DatabaseTestBase {

    private final BrandDao dao = new BrandDao();

    @Test
    @DisplayName("A new brand appears in the list")
    void addedBrandAppearsInList() throws SQLException {
        dao.insert("Dacia");
        assertEquals(9, dao.findAll().size());
        assertTrue(names().contains("Dacia"));
    }

    @Test
    @DisplayName("A brand can be renamed")
    void brandCanBeRenamed() throws SQLException {
        dao.rename(find("Suzuki"), "Suzuki Motor");
        assertTrue(names().contains("Suzuki Motor"));
        assertFalse(names().contains("Suzuki"));
    }

    @Test
    @DisplayName("The same brand name cannot be added twice")
    void duplicateNameIsRejected() {
        assertThrows(SQLException.class, () -> dao.insert("Toyota"));
    }

    @Test
    @DisplayName("A brand without cars can be deleted")
    void unusedBrandCanBeDeleted() throws SQLException {
        dao.insert("Dacia");
        dao.delete(find("Dacia"));
        assertFalse(names().contains("Dacia"));
    }

    @Test
    @DisplayName("A brand that cars use cannot be deleted")
    void brandUsedByCarsCannotBeDeleted() throws SQLException {
        Brand toyota = find("Toyota");
        assertThrows(SQLException.class, () -> dao.delete(toyota));
        assertTrue(names().contains("Toyota"));
    }

    private List<String> names() throws SQLException {
        return dao.findAll().stream().map(Brand::getName).toList();
    }

    private Brand find(String name) throws SQLException {
        return dao.findAll().stream().filter(b -> b.getName().equals(name)).findFirst().orElseThrow();
    }
}
