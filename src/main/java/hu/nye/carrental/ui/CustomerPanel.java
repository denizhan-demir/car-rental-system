package hu.nye.carrental.ui;

import hu.nye.carrental.dao.CustomerDao;
import hu.nye.carrental.model.Customer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

/**
 * "Customers" tab: table of all customers with search, Add / Edit / Delete.
 */
public class CustomerPanel extends JPanel implements Refreshable {

    private final CustomerDao customerDao = new CustomerDao();

    private final CustomerTableModel tableModel = new CustomerTableModel();
    private final JTable table = new JTable(tableModel);
    private final TableRowSorter<CustomerTableModel> sorter = new TableRowSorter<>(tableModel);
    private final JTextField searchField = Styles.searchField("Search name, email, phone, license...");

    public CustomerPanel() {
        super(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JButton addButton = Styles.primary(new JButton("+  Add customer"));
        JButton editButton = new JButton("Edit");
        JButton deleteButton = Styles.danger(new JButton("Delete"));
        addButton.addActionListener(e -> addCustomer());
        editButton.addActionListener(e -> editCustomer());
        deleteButton.addActionListener(e -> deleteCustomer());
        add(Styles.toolbar(searchField, editButton, deleteButton, addButton), BorderLayout.NORTH);
        TableSearch.attach(searchField, sorter);

        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.getColumn("ID").setMaxWidth(60);
        add(Styles.tableCard(table), BorderLayout.CENTER);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editCustomer();
                }
            }
        });

        refresh();
    }

    /** READ */
    @Override
    public final void refresh() {
        try {
            tableModel.setCustomers(customerDao.findAll());
        } catch (SQLException e) {
            Dialogs.showDatabaseError(this, e);
        }
    }

    /** CREATE */
    private void addCustomer() {
        Customer customer = new CustomerDialog(SwingUtilities.getWindowAncestor(this), "Add customer", null)
                .showDialog();
        if (customer == null) {
            return;
        }
        try {
            customerDao.insert(customer);
            refresh();
        } catch (SQLException e) {
            Dialogs.showDatabaseError(this, e);
        }
    }

    /** UPDATE */
    private void editCustomer() {
        Customer selected = getSelectedCustomer();
        if (selected == null) {
            Dialogs.showWarning(this, "Please select a customer first.");
            return;
        }
        Customer customer = new CustomerDialog(SwingUtilities.getWindowAncestor(this), "Edit customer", selected)
                .showDialog();
        if (customer == null) {
            return;
        }
        try {
            customerDao.update(customer);
            refresh();
        } catch (SQLException e) {
            Dialogs.showDatabaseError(this, e);
        }
    }

    /** DELETE */
    private void deleteCustomer() {
        Customer selected = getSelectedCustomer();
        if (selected == null) {
            Dialogs.showWarning(this, "Please select a customer first.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "Delete " + selected.getFullName() + "?", "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            customerDao.delete(selected);
            refresh();
        } catch (SQLException e) {
            // e.g. the customer has rentals -> foreign key stops the delete
            Dialogs.showDatabaseError(this, e);
        }
    }

    private Customer getSelectedCustomer() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }
        return tableModel.getCustomerAt(table.convertRowIndexToModel(viewRow));
    }
}
