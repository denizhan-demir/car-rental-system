package hu.nye.carrental.ui;

import hu.nye.carrental.model.Brand;
import hu.nye.carrental.model.Car;
import hu.nye.carrental.model.Category;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.text.ParseException;
import java.time.Year;
import java.util.List;

/**
 * Form for adding a new car or editing an existing one.
 * showDialog() returns the filled Car, or null if the user pressed Cancel.
 */
public class CarDialog extends JDialog {

    private final JComboBox<Brand> brandBox = new JComboBox<>();
    private final JComboBox<Category> categoryBox = new JComboBox<>();
    private final JTextField modelField = new JTextField(20);
    private final JTextField plateField = new JTextField(20);
    private final JSpinner yearSpinner;
    private final JSpinner priceSpinner;
    private final JSpinner seatsSpinner = new JSpinner(new SpinnerNumberModel(5, 2, 9, 1));
    private final JComboBox<String> transmissionBox = new JComboBox<>(new String[]{"Manual", "Automatic"});
    private final JComboBox<String> fuelBox = new JComboBox<>(new String[]{"Petrol", "Diesel", "Hybrid", "Electric"});

    private final Car existing; // null when adding a new car
    private Car result;         // stays null if cancelled

    public CarDialog(Window owner, String title, List<Brand> brands, List<Category> categories, Car existing) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        this.existing = existing;

        int currentYear = Year.now().getValue();
        yearSpinner = new JSpinner(new SpinnerNumberModel(currentYear, 1990, currentYear + 1, 1));
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#")); // no "2,026"
        priceSpinner = new JSpinner(new SpinnerNumberModel(30.0, 1.0, 10_000.0, 1.0));
        priceSpinner.setEditor(new JSpinner.NumberEditor(priceSpinner, "0.00"));

        for (Brand brand : brands) {
            brandBox.addItem(brand);
        }
        for (Category category : categories) {
            categoryBox.addItem(category);
        }
        modelField.putClientProperty("JTextField.placeholderText", "e.g. Corolla");
        plateField.putClientProperty("JTextField.placeholderText", "e.g. AA BC-123");

        if (existing != null) {
            fillFrom(existing);
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        form.add(new JLabel("Brand:"));
        form.add(brandBox);
        form.add(new JLabel("Category:"));
        form.add(categoryBox);
        form.add(new JLabel("Model:"));
        form.add(modelField);
        form.add(new JLabel("Plate number:"));
        form.add(plateField);
        form.add(new JLabel("Year:"));
        form.add(yearSpinner);
        form.add(new JLabel("Daily price (€):"));
        form.add(priceSpinner);
        form.add(new JLabel("Seats:"));
        form.add(seatsSpinner);
        form.add(new JLabel("Gearbox:"));
        form.add(transmissionBox);
        form.add(new JLabel("Fuel:"));
        form.add(fuelBox);

        JButton saveButton = Styles.primary(new JButton("Save"));
        JButton cancelButton = new JButton("Cancel");
        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        buttons.add(cancelButton);
        buttons.add(saveButton);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(saveButton); // Enter = Save

        pack();
        setLocationRelativeTo(owner);
    }

    /** Opens the window and waits until it is closed. */
    public Car showDialog() {
        setVisible(true);
        return result;
    }

    private void fillFrom(Car car) {
        for (int i = 0; i < brandBox.getItemCount(); i++) {
            if (brandBox.getItemAt(i).getId() == car.getBrandId()) {
                brandBox.setSelectedIndex(i);
            }
        }
        for (int i = 0; i < categoryBox.getItemCount(); i++) {
            if (categoryBox.getItemAt(i).getId() == car.getCategoryId()) {
                categoryBox.setSelectedIndex(i);
            }
        }
        modelField.setText(car.getModel());
        plateField.setText(car.getPlateNumber());
        yearSpinner.setValue(car.getYear());
        priceSpinner.setValue(car.getDailyPrice());
        seatsSpinner.setValue(car.getSeats());
        transmissionBox.setSelectedItem(car.getTransmission());
        fuelBox.setSelectedItem(car.getFuelType());
    }

    private void onSave() {
        String model = modelField.getText().trim();
        String plate = plateField.getText().trim().toUpperCase();

        if (model.isEmpty() || plate.isEmpty()) {
            Dialogs.showWarning(this, "Model and plate number are required.");
            return;
        }
        try {
            // takes the typed text even if the user did not press Enter in the spinner
            yearSpinner.commitEdit();
            priceSpinner.commitEdit();
            seatsSpinner.commitEdit();
        } catch (ParseException e) {
            Dialogs.showWarning(this, "Please enter a valid year, price and number of seats.");
            return;
        }

        Car car = new Car();
        if (existing != null) {
            car.setId(existing.getId());
        }
        car.setBrandId(((Brand) brandBox.getSelectedItem()).getId());
        car.setCategoryId(((Category) categoryBox.getSelectedItem()).getId());
        car.setModel(model);
        car.setPlateNumber(plate);
        car.setYear(((Number) yearSpinner.getValue()).intValue());
        car.setDailyPrice(((Number) priceSpinner.getValue()).doubleValue());
        car.setSeats(((Number) seatsSpinner.getValue()).intValue());
        car.setTransmission((String) transmissionBox.getSelectedItem());
        car.setFuelType((String) fuelBox.getSelectedItem());

        result = car;
        dispose();
    }
}
