package hu.nye.carrental.ui;

import java.util.Locale;

/**
 * Formats prices in one place. Change CURRENCY here (for example to " Ft")
 * and every screen follows.
 */
public final class Money {

    private static final String CURRENCY = "€";

    private Money() {
    }

    /** Always "€105.00" (dot as decimal separator), whatever the computer's language is. */
    public static String format(double amount) {
        return String.format(Locale.US, "%s%.2f", CURRENCY, amount);
    }
}
