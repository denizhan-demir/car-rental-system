package hu.nye.carrental.dao;

import hu.nye.carrental.DatabaseTestBase;
import hu.nye.carrental.model.Car;
import hu.nye.carrental.model.Rental;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the rental rules: creating, returning a car, and that a car cannot be rented twice.
 */
class RentalDaoTest extends DatabaseTestBase {

    private final RentalDao rentalDao = new RentalDao();
    private final CarDao carDao = new CarDao();
    private final CustomerDao customerDao = new CustomerDao();

    @Test
    @DisplayName("The sample data has active, overdue and closed rentals")
    void sampleDataHasEveryStatus() throws SQLException {
        LocalDate today = LocalDate.now();
        Map<String, Long> byStatus = rentalDao.findAll().stream()
                .collect(Collectors.groupingBy(r -> r.getStatus(today), Collectors.counting()));
        assertEquals(Map.of("Active", 3L, "Overdue", 1L, "Closed", 3L), byStatus);
    }

    @Test
    @DisplayName("Rentals that are still out are listed first")
    void activeRentalsAreListedFirst() throws SQLException {
        List<Rental> rentals = rentalDao.findAll();
        for (int i = 0; i < 4; i++) {
            assertTrue(rentals.get(i).isActive());
        }
        for (int i = 4; i < rentals.size(); i++) {
            assertFalse(rentals.get(i).isActive());
        }
    }

    @Test
    @DisplayName("A new rental makes the car rented")
    void newRentalMakesCarRented() throws SQLException {
        rentalDao.create(newRental("AA BT-102", LocalDate.now(), LocalDate.now().plusDays(3)));

        assertFalse(car("AA BT-102").isAvailable());
        assertEquals(8, rentalDao.findAll().size());
    }

    @Test
    @DisplayName("A car that is already rented cannot be rented again")
    void carCannotBeRentedTwice() throws SQLException {
        Rental second = newRental("AA BT-105", LocalDate.now(), LocalDate.now().plusDays(2)); // already out
        assertThrows(IllegalStateException.class, () -> rentalDao.create(second));
        assertEquals(7, rentalDao.findAll().size()); // nothing was saved
    }

    @Test
    @DisplayName("Returning the car closes the rental and the car is available again")
    void returningCarClosesRental() throws SQLException {
        Rental active = rentalDao.findAll().stream()
                .filter(r -> r.isActive() && r.getCarLabel().contains("AA BT-105"))
                .findFirst().orElseThrow();

        rentalDao.close(active.getId(), LocalDate.now(), 135.0);

        Rental closed = rentalDao.findAll().stream()
                .filter(r -> r.getId() == active.getId()).findFirst().orElseThrow();
        double totalPrice = closed.getTotalPrice();
        assertEquals("Closed", closed.getStatus(LocalDate.now()));
        assertEquals(135.0, totalPrice, 0.001);
        assertTrue(car("AA BT-105").isAvailable());
    }

    @Test
    @DisplayName("A rental cannot end before it starts")
    void endBeforeStartIsRejected() throws SQLException {
        Rental wrong = newRental("AA BT-102", LocalDate.now(), LocalDate.now().minusDays(1));
        assertThrows(SQLException.class, () -> rentalDao.create(wrong));
        assertTrue(car("AA BT-102").isAvailable());
    }

    private Car car(String plate) throws SQLException {
        Map<String, Car> byPlate = carDao.findAll().stream()
                .collect(Collectors.toMap(Car::getPlateNumber, Function.identity()));
        return byPlate.get(plate);
    }

    private Rental newRental(String plate, LocalDate start, LocalDate end) throws SQLException {
        Rental rental = new Rental();
        rental.setCarId(car(plate).getId());
        rental.setCustomerId(customerDao.findAll().get(0).getId());
        rental.setStartDate(start);
        rental.setEndDate(end);
        return rental;
    }
}
