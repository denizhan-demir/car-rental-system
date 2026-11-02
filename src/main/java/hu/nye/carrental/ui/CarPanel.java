package hu.nye.carrental.ui;

import hu.nye.carrental.dao.BrandDao;
import hu.nye.carrental.dao.CarDao;
import hu.nye.carrental.dao.CategoryDao;
import hu.nye.carrental.model.Brand;
import hu.nye.carrental.model.Car;
import hu.nye.carrental.model.Category;

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
import java.util.List;

/**
 * "Cars" tab: table of all cars with search, Add / Edit / Delete.
 */
public class CarPanel extends JPanel implements Refreshable {

    private final CarDao carDao = new CarDao();
    private final BrandDao brandDao = new BrandDao();
    private final CategoryDao categoryDao = new CategoryDao();

    private final CarTableModel tableModel = new CarTableModel();
    private final JTable table = new JTable(tableModel);
    private final TableRowSorter<CarTableModel> sorter = new TableRowSorter<>(tableModel);
    private final JTextField searchField = Styles.searchField("Search brand, model, plate, status...");

    public CarPanel() {
        super(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JButton addButton = Styles.primary(new JButton("+  Add car"));
        JButton editButton = new JButton("Edit");
        JButton deleteButton = Styles.danger(new JButton("Delete"));
        addButton.addActionListener(e -> addCar());
        editButton.addActionListener(e -> editCar());
        deleteButton.addActionListener(e -> deleteCar());
        add(Styles.toolbar(searchField, editButton, deleteButton, addButton), BorderLayout.NORTH);
        TableSearch.attach(searchField, sorter);

        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.getColumn("Status").setCellRenderer(new StatusRenderer());
        table.getColumn("Daily price").setCellRenderer(new MoneyRenderer());
        table.getColumn("ID").setMaxWidth(60);
        add(Styles.tableCard(table), BorderLayout.CENTER);

        // double click on a row = edit
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editCar();
                }
            }
        });

        refresh();
    }

    /** READ */
    @Override
    public final void refresh() {
        try {
            tableModel.setCars(carDao.findAll());
        } catch (SQLException e) {
            Dialogs.showDatabaseError(this, e);
        }
    }

    /** CREATE */
    private void addCar() {
        CarDialog dialog = createDialog("Add car", null);
        if (dialog == null) {
            return;
        }
        Car car = dialog.showDialog();
        if (car == null) {
            return; // cancelled
        }
        try {
            carDao.insert(car);
            refresh();
        } catch (SQLException e) {
            Dialogs.showDatabaseError(this, e);
        }
    }

    /** UPDATE */
    private void editCar() {
        Car selected = getSelectedCar();
        if (selected == null) {
            Dialogs.showWarning(this, "Please select a car first.");
            return;
        }
        CarDialog dialog = createDialog("Edit car", selected);
        if (dialog == null) {
            return;
        }
        Car car = dialog.showDialog();
        if (car == null) {
            return;
        }
        try {
            carDao.update(car);
            refresh();
        } catch (SQLException e) {
            Dialogs.showDatabaseError(this, e);
        }
    }

    /** DELETE */
    private void deleteCar() {
        Car selected = getSelectedCar();
        if (selected == null) {
            Dialogs.showWarning(this, "Please select a car first.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "Delete " + selected + "?", "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            carDao.delete(selected);
            refresh();
        } catch (SQLException e) {
            // e.g. the car has rentals -> foreign key stops the delete
            Dialogs.showDatabaseError(this, e);
        }
    }

    /** Loads the latest brands and categories, so new ones appear in the drop-downs. */
    private CarDialog createDialog(String title, Car existing) {
        try {
            List<Brand> brands = brandDao.findAll();
            List<Category> categories = categoryDao.findAll();
            if (brands.isEmpty() || categories.isEmpty()) {
                Dialogs.showWarning(this,
                        "Please add at least one brand and one category first (Brands & Categories tab).");
                return null;
            }
            return new CarDialog(SwingUtilities.getWindowAncestor(this), title, brands, categories, existing);
        } catch (SQLException e) {
            Dialogs.showDatabaseError(this, e);
            return null;
        }
    }

    private Car getSelectedCar() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }
        // the table can be sorted/filtered, so convert the visible row number to the real one
        return tableModel.getCarAt(table.convertRowIndexToModel(viewRow));
    }
}
