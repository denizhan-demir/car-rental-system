package hu.nye.carrental.ui;

import hu.nye.carrental.model.Car;
import hu.nye.carrental.model.Customer;
import hu.nye.carrental.model.Rental;
import hu.nye.carrental.service.PriceCalculator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.time.LocalDate;
import java.util.List;

/**
 * Form for creating a new rental. Shows the estimated price while the user picks dates.
 * showDialog() returns the new Rental, or null if the user pressed Cancel.
 */
public class RentalDialog extends JDialog {

    private final JComboBox<Car> carBox = new JComboBox<>();
    private final JComboBox<Customer> customerBox = new JComboBox<>();
    private final JSpinner startSpinner = DateSpinners.create(LocalDate.now());
    private final JSpinner endSpinner = DateSpinners.create(LocalDate.now().plusDays(3));
    private final JLabel estimateLabel = new JLabel();

    private Rental result;

    public RentalDialog(Window owner, List<Car> availableCars, List<Customer> customers) {
        super(owner, "New rental", ModalityType.APPLICATION_MODAL);

        for (Car car : availableCars) {
            carBox.addItem(car);
        }
        for (Customer customer : customers) {
            customerBox.addItem(customer);
        }

        // recalculate the price whenever the car or a date changes
        carBox.addActionListener(e -> updateEstimate());
        startSpinner.addChangeListener(e -> updateEstimate());
        endSpinner.addChangeListener(e -> updateEstimate());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        form.add(new JLabel("Car:"));
        form.add(carBox);
        form.add(new JLabel("Customer:"));
        form.add(customerBox);
        form.add(new JLabel("Start date:"));
        form.add(startSpinner);
        form.add(new JLabel("Planned end date:"));
        form.add(endSpinner);
        form.add(new JLabel("Estimated price:"));
        form.add(estimateLabel);

        JButton saveButton = Styles.primary(new JButton("Create rental"));
        JButton cancelButton = new JButton("Cancel");
        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelButton);
        buttons.add(saveButton);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(saveButton);

        updateEstimate();
        pack();
        setLocationRelativeTo(owner);
    }

    public Rental showDialog() {
        setVisible(true);
        return result;
    }

    private void updateEstimate() {
        Car car = (Car) carBox.getSelectedItem();
        LocalDate start = DateSpinners.getDate(startSpinner);
        LocalDate end = DateSpinners.getDate(endSpinner);
        if (car == null || start == null || end == null || end.isBefore(start)) {
            estimateLabel.setText("-");
            return;
        }
        long days = PriceCalculator.days(start, end);
        double price = PriceCalculator.totalPrice(start, end, car.getDailyPrice());
        estimateLabel.setText(String.format("%s  (%d days × %s)", Money.format(price), days,
                Money.format(car.getDailyPrice())));
    }

    private void onSave() {
        LocalDate start = DateSpinners.getDate(startSpinner);
        LocalDate end = DateSpinners.getDate(endSpinner);
        if (start == null || end == null) {
            Dialogs.showWarning(this, "Please enter valid dates (yyyy-MM-dd).");
            return;
        }
        if (end.isBefore(start)) {
            Dialogs.showWarning(this, "The end date cannot be before the start date.");
            return;
        }

        Rental rental = new Rental();
        rental.setCarId(((Car) carBox.getSelectedItem()).getId());
        rental.setCustomerId(((Customer) customerBox.getSelectedItem()).getId());
        rental.setStartDate(start);
        rental.setEndDate(end);

        result = rental;
        dispose();
    }
}
