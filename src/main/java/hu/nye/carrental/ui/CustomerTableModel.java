package hu.nye.carrental.ui;

import hu.nye.carrental.model.Customer;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Tells the JTable what to show in each row and column.
 */
public class CustomerTableModel extends AbstractTableModel {

    private static final String[] COLUMNS =
            {"ID", "First name", "Last name", "Email", "Phone", "License number"};

    private List<Customer> customers = new ArrayList<>();

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
        fireTableDataChanged();
    }

    public Customer getCustomerAt(int row) {
        return customers.get(row);
    }

    @Override
    public int getRowCount() {
        return customers.size();
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
        return column == 0 ? Integer.class : String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        Customer c = customers.get(row);
        return switch (column) {
            case 0 -> c.getId();
            case 1 -> c.getFirstName();
            case 2 -> c.getLastName();
            case 3 -> c.getEmail() == null ? "" : c.getEmail();
            case 4 -> c.getPhone() == null ? "" : c.getPhone();
            case 5 -> c.getLicenseNumber();
            default -> null;
        };
    }
}
