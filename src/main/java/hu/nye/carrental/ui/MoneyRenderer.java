package hu.nye.carrental.ui;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Shows numbers as prices (€35.00) in the tables, aligned to the right.
 * The table still sorts by the real number, not by the text.
 */
public class MoneyRenderer extends DefaultTableCellRenderer {

    public MoneyRenderer() {
        setHorizontalAlignment(SwingConstants.RIGHT);
    }

    @Override
    protected void setValue(Object value) {
        setText(value instanceof Number number ? Money.format(number.doubleValue()) : "");
    }
}
