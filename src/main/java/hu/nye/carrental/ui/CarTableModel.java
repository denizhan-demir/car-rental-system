package hu.nye.carrental.ui;

import hu.nye.carrental.model.Car;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Tells the JTable what to show in each row and column.
 */
public class CarTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"ID", "Brand", "Model", "Category", "Plate number", "Year",
            "Seats", "Gearbox", "Fuel", "Daily price", "Status"};

    private List<Car> cars = new ArrayList<>();

    public void setCars(List<Car> cars) {
        this.cars = cars;
        fireTableDataChanged(); // tells the table to redraw itself
    }

    public Car getCarAt(int row) {
        return cars.get(row);
    }

    @Override
    public int getRowCount() {
        return cars.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    // correct types make sorting work properly (numbers are not sorted as text)
    @Override
    public Class<?> getColumnClass(int column) {
        return switch (column) {
            case 0, 5, 6 -> Integer.class;
            case 9 -> Double.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        Car car = cars.get(row);
        return switch (column) {
            case 0 -> car.getId();
            case 1 -> car.getBrandName();
            case 2 -> car.getModel();
            case 3 -> car.getCategoryName();
            case 4 -> car.getPlateNumber();
            case 5 -> car.getYear();
            case 6 -> car.getSeats();
            case 7 -> car.getTransmission();
            case 8 -> car.getFuelType();
            case 9 -> car.getDailyPrice();
            case 10 -> car.isAvailable() ? "Available" : "Rented";
            default -> null;
        };
    }
}
