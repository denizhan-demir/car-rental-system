package hu.nye.carrental.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests how the status of a rental (Active / Overdue / Closed) follows from its dates.
 */
class RentalTest {

    private static final LocalDate START = LocalDate.of(2026, 11, 2);
    private static final LocalDate PLANNED_END = LocalDate.of(2026, 11, 5);

    private Rental rental;

    @BeforeEach
    void createRental() {
        rental = new Rental();
        rental.setStartDate(START);
        rental.setEndDate(PLANNED_END);
    }

    @Test
    @DisplayName("Before the planned end date the rental is Active")
    void activeBeforePlannedEnd() {
        assertEquals("Active", rental.getStatus(LocalDate.of(2026, 11, 3)));
    }

    @Test
    @DisplayName("On the planned end date the rental is still Active")
    void stillActiveOnPlannedEndDay() {
        assertEquals("Active", rental.getStatus(PLANNED_END));
    }

    @Test
    @DisplayName("After the planned end date a rental that is not returned is Overdue")
    void overdueAfterPlannedEnd() {
        assertEquals("Overdue", rental.getStatus(LocalDate.of(2026, 11, 6)));
    }

    @Test
    @DisplayName("A returned rental is Closed, even if it came back late")
    void closedOnceReturned() {
        rental.setReturnDate(LocalDate.of(2026, 11, 8));
        assertEquals("Closed", rental.getStatus(LocalDate.of(2026, 11, 20)));
    }

    @Test
    @DisplayName("A rental is active until the car is returned")
    void activeUntilReturned() {
        assertTrue(rental.isActive());
        rental.setReturnDate(PLANNED_END);
        assertFalse(rental.isActive());
    }
}
