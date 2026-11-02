package hu.nye.carrental.ui;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.sql.SQLException;

/**
 * Small helper for showing messages. Turns SQLite error messages into
 * something a normal user can understand.
 */
public final class Dialogs {

    private Dialogs() {
    }

    public static void showDatabaseError(Component parent, SQLException e) {
        String message = e.getMessage() == null ? "" : e.getMessage();
        String text;
        if (message.contains("plate_number")) {
            text = "A car with this plate number already exists.";
        } else if (message.contains("license_number")) {
            text = "A customer with this license number already exists.";
        } else if (message.contains("UNIQUE")) {
            text = "This value already exists.";
        } else if (message.contains("CHECK")) {
            text = "Some of the values are not valid.";
        } else if (message.contains("FOREIGN KEY")) {
            text = "This item is used by other records (for example a car or a rental), so it cannot be deleted.";
        } else {
            text = "Database error: " + message;
        }
        JOptionPane.showMessageDialog(parent, text, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showWarning(Component parent, String text) {
        JOptionPane.showMessageDialog(parent, text, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}
