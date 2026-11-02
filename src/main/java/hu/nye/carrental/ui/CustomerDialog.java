package hu.nye.carrental.ui;

import hu.nye.carrental.model.Customer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;

/**
 * Form for adding a new customer or editing an existing one.
 * showDialog() returns the filled Customer, or null if the user pressed Cancel.
 */
public class CustomerDialog extends JDialog {

    private final JTextField firstNameField = new JTextField(20);
    private final JTextField lastNameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final JTextField licenseField = new JTextField(20);

    private final Customer existing; // null when adding
    private Customer result;         // stays null if cancelled

    public CustomerDialog(Window owner, String title, Customer existing) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        this.existing = existing;

        if (existing != null) {
            firstNameField.setText(existing.getFirstName());
            lastNameField.setText(existing.getLastName());
            emailField.setText(existing.getEmail());
            phoneField.setText(existing.getPhone());
            licenseField.setText(existing.getLicenseNumber());
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        form.add(new JLabel("First name: *"));
        form.add(firstNameField);
        form.add(new JLabel("Last name: *"));
        form.add(lastNameField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Phone:"));
        form.add(phoneField);
        form.add(new JLabel("Driving license no.: *"));
        form.add(licenseField);

        JButton saveButton = Styles.primary(new JButton("Save"));
        JButton cancelButton = new JButton("Cancel");
        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(new JLabel("* required   "));
        buttons.add(cancelButton);
        buttons.add(saveButton);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(saveButton);

        pack();
        setLocationRelativeTo(owner);
    }

    public Customer showDialog() {
        setVisible(true);
        return result;
    }

    private void onSave() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String license = licenseField.getText().trim().toUpperCase();

        if (firstName.isEmpty() || lastName.isEmpty() || license.isEmpty()) {
            Dialogs.showWarning(this, "First name, last name and driving license number are required.");
            return;
        }
        // very simple check: something@something.something
        if (!email.isEmpty() && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            Dialogs.showWarning(this, "The email address does not look valid.");
            return;
        }

        Customer customer = new Customer();
        if (existing != null) {
            customer.setId(existing.getId());
        }
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email.isEmpty() ? null : email);  // empty optional field -> NULL in the database
        customer.setPhone(phone.isEmpty() ? null : phone);
        customer.setLicenseNumber(license);

        result = customer;
        dispose();
    }
}
