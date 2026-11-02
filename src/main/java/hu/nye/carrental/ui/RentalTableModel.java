package hu.nye.carrental.ui;

import hu.nye.carrental.model.Rental;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Tells the JTable what to show in each row and column.
 */
public class RentalTableModel extends AbstractTableModel {

    private static final String[] COLUMNS =
            {"ID", "Car", "Customer", "Start", "Planned end", "Returned", "Total price", "Status"};

    private List<Rental> rentals = new ArrayList<>();

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
        fireTableDataChanged();
    }

    public Rental getRentalAt(int row) {
        return rentals.get(row);
    }

    @Override
    public int getRowCount() {
        return rentals.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return switch (column) {
            case 0 -> Integer.class;
            case 6 -> Double.class;
            default -> String.class; // dates as yyyy-MM-dd text still sort correctly
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        Rental r = rentals.get(row);
        return switch (column) {
            case 0 -> r.getId();
            case 1 -> r.getCarLabel();
            case 2 -> r.getCustomerName();
            case 3 -> r.getStartDate().toString();
            case 4 -> r.getEndDate().toString();
            case 5 -> r.getReturnDate() == null ? "" : r.getReturnDate().toString();
            case 6 -> r.getTotalPrice(); // empty cell while active
            case 7 -> r.getStatus(LocalDate.now());
            default -> null;
        };
    }
}
