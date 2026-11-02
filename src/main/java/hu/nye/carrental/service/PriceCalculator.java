package hu.nye.carrental.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Rental price rules, kept in one place so they are easy to change and to test.
 */
public final class PriceCalculator {

    private PriceCalculator() {
    }

    /** Number of days to pay for. Returning the car on the same day still counts as 1 day. */
    public static long days(LocalDate start, LocalDate end) {
        long days = ChronoUnit.DAYS.between(start, end);
        return Math.max(1, days);
    }

    public static double totalPrice(LocalDate start, LocalDate end, double dailyPrice) {
        return days(start, end) * dailyPrice;
    }
}
