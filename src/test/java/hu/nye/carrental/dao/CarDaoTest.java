package hu.nye.carrental.dao;

import hu.nye.carrental.DatabaseTestBase;
import hu.nye.carrental.model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests CRUD for cars and the availability status that comes from the rentals.
 */
class CarDaoTest extends DatabaseTestBase {

    private final CarDao dao = new CarDao();

    @Test
    @DisplayName("Cars are listed with their brand and category names")
    void listsCarsWithBrandAndCategoryNames() throws SQLException {
        assertEquals(12, dao.findAll().size());
        Car yaris = byPlate("AA BT-101");
        assertEquals("Toyota", yaris.getBrandName());
        assertEquals("Economy", yaris.getCategoryName());
        assertEquals("Yaris Hybrid", yaris.getModel());
    }

    @Test
    @DisplayName("Cars with an active rental are shown as rented")
    void availabilityComesFromRentals() throws SQLException {
        Set<String> rented = dao.findAll().stream()
                .filter(car -> !car.isAvailable())
                .map(Car::getPlateNumber)
                .collect(Collectors.toSet());
        // the sample data has 4 rentals without a return date (3 active, 1 overdue)
        assertEquals(Set.of("AA BT-105", "AA BT-108", "AA BT-109", "AA BT-112"), rented);
    }

    @Test
    @DisplayName("A new car is saved with all its data and is available")
    void newCarIsSavedAndAvailable() throws SQLException {
        Car car = newCar("TEST-001");
        dao.insert(car);

        Car saved = byPlate("TEST-001");
        assertEquals("Test model", saved.getModel());
        assertEquals(7, saved.getSeats());
        assertEquals("Automatic", saved.getTransmission());
        assertEquals("Electric", saved.getFuelType());
        assertTrue(saved.isAvailable());
    }

    @Test
    @DisplayName("Editing a car changes its daily price")
    void updateChangesDailyPrice() throws SQLException {
        Car yaris = byPlate("AA BT-101");
        yaris.setDailyPrice(40);
        dao.update(yaris);
        assertEquals(40, byPlate("AA BT-101").getDailyPrice(), 0.001);
    }

    @Test
    @DisplayName("The same plate number cannot be used twice")
    void duplicatePlateIsRejected() {
        assertThrows(SQLException.class, () -> dao.insert(newCar("AA BT-101")));
    }

    @Test
    @DisplayName("A daily price of 0 is rejected")
    void zeroPriceIsRejected() {
        Car car = newCar("TEST-002");
        car.setDailyPrice(0);
        assertThrows(SQLException.class, () -> dao.insert(car));
    }

    @Test
    @DisplayName("A car with rental history cannot be deleted")
    void carWithRentalsCannotBeDeleted() throws SQLException {
        Car yaris = byPlate("AA BT-101"); // has a closed rental in the sample data
        assertThrows(SQLException.class, () -> dao.delete(yaris));
    }

    @Test
    @DisplayName("A car without rentals can be deleted")
    void carWithoutRentalsCanBeDeleted() throws SQLException {
        dao.delete(byPlate("AA BT-102")); // the Polo was never rented
        assertEquals(11, dao.findAll().size());
    }

    private Car byPlate(String plate) throws SQLException {
        return dao.findAll().stream().filter(c -> c.getPlateNumber().equals(plate)).findFirst().orElseThrow();
    }

    /** A test car with the same brand and category as the Yaris. */
    private Car newCar(String plate) {
        try {
            Car yaris = byPlate("AA BT-101");
            Car car = new Car();
            car.setBrandId(yaris.getBrandId());
            car.setCategoryId(yaris.getCategoryId());
            car.setModel("Test model");
            car.setPlateNumber(plate);
            car.setYear(2025);
            car.setDailyPrice(55);
            car.setSeats(7);
            car.setTransmission("Automatic");
            car.setFuelType("Electric");
            return car;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
