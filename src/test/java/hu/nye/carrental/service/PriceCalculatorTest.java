package hu.nye.carrental.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the price rule: number of days × daily price.
 */
class PriceCalculatorTest {

    private static final LocalDate NOV_2 = LocalDate.of(2026, 11, 2);

    @Test
    @DisplayName("A car returned on the same day counts as one day")
    void sameDayReturnCountsAsOneDay() {
        assertEquals(1, PriceCalculator.days(NOV_2, NOV_2));
    }

    @Test
    @DisplayName("Days are counted between the start and the return date")
    void countsDaysBetweenTheDates() {
        assertEquals(5, PriceCalculator.days(NOV_2, LocalDate.of(2026, 11, 7)));
    }

    @Test
    @DisplayName("Total price is days × daily price")
    void totalPriceIsDaysTimesDailyPrice() {
        assertEquals(170.0, PriceCalculator.totalPrice(NOV_2, LocalDate.of(2026, 11, 7), 34.0), 0.001);
    }

    @Test
    @DisplayName("A late return is charged for every day until the car came back")
    void lateReturnIsChargedForEveryDay() {
        LocalDate returned = LocalDate.of(2026, 11, 8); // planned end was Nov 5, came back 3 days late
        assertEquals(6 * 40.0, PriceCalculator.totalPrice(NOV_2, returned, 40.0), 0.001);
    }

    @Test
    @DisplayName("Counting days works across the end of the year")
    void worksAcrossTheEndOfTheYear() {
        assertEquals(3, PriceCalculator.days(LocalDate.of(2026, 12, 30), LocalDate.of(2027, 1, 2)));
    }
}
