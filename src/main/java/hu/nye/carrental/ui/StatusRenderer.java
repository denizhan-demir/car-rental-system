package hu.nye.carrental.ui;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

/**
 * Shows status texts in color in the tables: ● Available, ● Rented, ● Overdue...
 */
public class StatusRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value == null ? "" : "●  " + value, isSelected, hasFocus, row, column);
        setFont(getFont().deriveFont(Font.BOLD));
        if (!isSelected) {
            setForeground(colorFor(String.valueOf(value)));
        }
        return this;
    }

    public static Color colorFor(String status) {
        return switch (status) {
            case "Available", "Active" -> Styles.SUCCESS;
            case "Rented" -> Styles.ACCENT;
            case "Overdue" -> Styles.DANGER;
            default -> Styles.MUTED; // Closed
        };
    }
}
