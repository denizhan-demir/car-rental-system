package hu.nye.carrental.ui;

import hu.nye.carrental.dao.CarDao;
import hu.nye.carrental.dao.CustomerDao;
import hu.nye.carrental.dao.RentalDao;
import hu.nye.carrental.model.Car;
import hu.nye.carrental.model.Customer;
import hu.nye.carrental.model.Rental;
import hu.nye.carrental.service.PriceCalculator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * "Rentals" tab: create a rental, return a car (close the rental), delete a rental.
 */
public class RentalPanel extends JPanel implements Refreshable {

    private final RentalDao rentalDao = new RentalDao();
    private final CarDao carDao = new CarDao();
    private final CustomerDao customerDao = new CustomerDao();

    private final RentalTableModel tableModel = new RentalTableModel();
    private final JTable table = new JTable(tableModel);
    private final TableRowSorter<RentalTableModel> sorter = new TableRowSorter<>(tableModel);
    private final JTextField searchField = Styles.searchField("Search car, customer, date, status...");

    public RentalPanel() {
        super(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JButton newButton = Styles.primary(new JButton("+  New rental"));
        JButton returnButton = new JButton("Return car");
        JButton deleteButton = Styles.danger(new JButton("Delete"));
        newButton.addActionListener(e -> newRental());
        returnButton.addActionListener(e -> returnCar());
        deleteButton.addActionListener(e -> deleteRental());
        add(Styles.toolbar(searchField, deleteButton, returnButton, newButton), BorderLayout.NORTH);
        TableSearch.attach(searchField, sorter);

        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.getColumn("Status").setCellRenderer(new StatusRenderer());
        table.getColumn("Total price").setCellRenderer(new MoneyRenderer());
        table.getColumn("ID").setMaxWidth(60);
        add(Styles.tableCard(table), BorderLayout.CENTER);

        refresh();
    }

    /** READ */
    @Override
    public final void refresh() {
        try {
            tableModel.setRentals(rentalDao.findAll());
        } catch (SQLException e) {
            Dialogs.showDatabaseError(this, e);
        }
    }

    /** CREATE */
    private void newRental() {
        try {
            List<Car> availableCars = carDao.findAll().stream()
                    .filter(Car::isAvailable)
                    .toList();
            List<Customer> customers = customerDao.findAll();

            if (availableCars.isEmpty()) {
                Dialogs.showWarning(this, "There are no available cars at the moment.");
                return;
            }
            if (customers.isEmpty()) {
                Dialogs.showWarning(this, "Please add a customer first (Customers tab).");
                return;
            }

            Rental rental = new RentalDialog(SwingUtilities.getWindowAncestor(this), availableCars, customers)
                    .showDialog();
            if (rental == null) {
                return;
            }
            rentalDao.create(rental);
            refresh();
        } catch (SQLException e) {
            Dialogs.showDatabaseError(this, e);
        } catch (IllegalStateException e) {
            Dialogs.showWarning(this, e.getMessage()); // "This car is already rented."
        }
    }

    /** UPDATE: close the rental and calculate the final price */
    private void returnCar() {
        Rental selected = getSelectedRental();
        if (selected == null) {
            Dialogs.showWarning(this, "Please select a rental first.");
            return;
        }
        if (!selected.isActive()) {
            Dialogs.showWarning(this, "This rental is already closed.");
            return;
        }

        // 1) ask for the return date
        JSpinner dateSpinner = DateSpinners.create(LocalDate.now());
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.add(new JLabel("Return date:"));
        datePanel.add(dateSpinner);
        int answer = JOptionPane.showConfirmDialog(this, datePanel,
                "Return " + selected.getCarLabel(), JOptionPane.OK_CANCEL_OPTION);
        if (answer != JOptionPane.OK_OPTION) {
            return;
        }
        LocalDate returnDate = DateSpinners.getDate(dateSpinner);
        if (returnDate == null) {
            Dialogs.showWarning(this, "Please enter a valid date (yyyy-MM-dd).");
            return;
        }
        if (returnDate.isBefore(selected.getStartDate())) {
            Dialogs.showWarning(this, "The return date cannot be before the start date.");
            return;
        }

        // 2) show the price and ask for confirmation
        long days = PriceCalculator.days(selected.getStartDate(), returnDate);
        double total = PriceCalculator.totalPrice(selected.getStartDate(), returnDate, selected.getDailyPrice());
        String message = String.format("Customer: %s%nDays: %d%nDaily price: %s%nTotal price: %s%n%nClose this rental?",
                selected.getCustomerName(), days, Money.format(selected.getDailyPrice()), Money.format(total));
        answer = JOptionPane.showConfirmDialog(this, message, "Confirm return", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            rentalDao.close(selected.getId(), returnDate, total);
            refresh();
        } catch (SQLException e) {
            Dialogs.showDatabaseError(this, e);
        }
    }

    /** DELETE */
    private void deleteRental() {
        Rental selected = getSelectedRental();
        if (selected == null) {
            Dialogs.showWarning(this, "Please select a rental first.");
            return;
        }
        String message = "Delete this rental?";
        if (selected.isActive()) {
            message += "\nThe car is still out - after deleting, it will show as available again.";
        }
        int answer = JOptionPane.showConfirmDialog(this, message, "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            rentalDao.delete(selected);
            refresh();
        } catch (SQLException e) {
            Dialogs.showDatabaseError(this, e);
        }
    }

    private Rental getSelectedRental() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }
        return tableModel.getRentalAt(table.convertRowIndexToModel(viewRow));
    }
}
