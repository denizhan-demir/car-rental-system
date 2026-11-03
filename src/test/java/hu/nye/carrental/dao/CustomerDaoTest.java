package hu.nye.carrental.dao;

import hu.nye.carrental.DatabaseTestBase;
import hu.nye.carrental.model.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests CRUD for customers.
 */
class CustomerDaoTest extends DatabaseTestBase {

    private final CustomerDao dao = new CustomerDao();

    @Test
    @DisplayName("A customer can be added without email and phone")
    void customerWithoutEmailAndPhoneCanBeAdded() throws SQLException {
        dao.insert(newCustomer("XY999999"));
        Customer saved = byLicense("XY999999");
        assertEquals("Test", saved.getFirstName());
        assertNull(saved.getEmail());
        assertNull(saved.getPhone());
    }

    @Test
    @DisplayName("The same driving license number cannot be used twice")
    void duplicateLicenseIsRejected() {
        assertThrows(SQLException.class, () -> dao.insert(newCustomer("AB123456")));
    }

    @Test
    @DisplayName("Editing a customer changes the phone number")
    void updateChangesPhone() throws SQLException {
        Customer anna = byLicense("AB123456");
        anna.setPhone("+36 30 999 0000");
        dao.update(anna);
        assertEquals("+36 30 999 0000", byLicense("AB123456").getPhone());
    }

    @Test
    @DisplayName("A customer with rentals cannot be deleted")
    void customerWithRentalsCannotBeDeleted() throws SQLException {
        Customer anna = byLicense("AB123456");
        assertThrows(SQLException.class, () -> dao.delete(anna));
    }

    @Test
    @DisplayName("A customer without rentals can be deleted")
    void customerWithoutRentalsCanBeDeleted() throws SQLException {
        dao.insert(newCustomer("XY999999"));
        dao.delete(byLicense("XY999999"));
        assertEquals(6, dao.findAll().size());
    }

    private Customer byLicense(String license) throws SQLException {
        return dao.findAll().stream().filter(c -> c.getLicenseNumber().equals(license)).findFirst().orElseThrow();
    }

    private Customer newCustomer(String license) {
        Customer customer = new Customer();
        customer.setFirstName("Test");
        customer.setLastName("Customer");
        customer.setLicenseNumber(license); // email and phone stay null (optional)
        return customer;
    }
}
