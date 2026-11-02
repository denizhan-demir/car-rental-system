package hu.nye.carrental.ui;

import hu.nye.carrental.dao.NameDao;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

/**
 * A card with a list and Add / Edit / Delete buttons for a table that only has a name.
 * Used twice on the Brands & Categories tab.
 */
public class NamePanel<T> extends RoundedPanel {

    private final String itemLabel;          // e.g. "brand", used in messages
    private final NameDao<T> dao;
    private final DefaultListModel<T> listModel = new DefaultListModel<>();
    private final JList<T> list = new JList<>(listModel);

    public NamePanel(String title, String subtitle, String itemLabel, NameDao<T> dao) {
        super(new BorderLayout(0, 12), "List.background", 14);
        this.itemLabel = itemLabel;
        this.dao = dao;
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JButton addButton = Styles.primary(new JButton("+  Add"));
        JButton editButton = new JButton("Edit");
        JButton deleteButton = Styles.danger(new JButton("Delete"));
        addButton.addActionListener(e -> addItem());
        editButton.addActionListener(e -> editItem());
        deleteButton.addActionListener(e -> deleteItem());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(Styles.heading(title, subtitle), BorderLayout.WEST);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        buttons.setOpaque(false);
        buttons.add(deleteButton);
        buttons.add(editButton);
        buttons.add(addButton);
        top.add(buttons, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellHeight(34);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        // double click on an item = edit
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editItem();
                }
            }
        });

        reload();
    }

    /** READ: loads everything from the database into the list again. */
    public final void reload() {
        try {
            listModel.clear();
            for (T item : dao.findAll()) {
                listModel.addElement(item);
            }
        } catch (SQLException e) {
            Dialogs.showDatabaseError(this, e);
        }
    }

    /** CREATE */
    private void addItem() {
        String name = askName("New " + itemLabel + " name:", "");
        if (name == null) {
            return;
        }
        try {
            dao.insert(name);
            reload();
        } catch (SQLException e) {
            Dialogs.showDatabaseError(this, e);
        }
    }

    /** UPDATE */
    private void editItem() {
        T selected = list.getSelectedValue();
        if (selected == null) {
            Dialogs.showWarning(this, "Please select a " + itemLabel + " first.");
            return;
        }
        // toString() of Brand / Category returns the name
        String name = askName("New name:", selected.toString());
        if (name == null) {
            return;
        }
        try {
            dao.rename(selected, name);
            reload();
        } catch (SQLException e) {
            Dialogs.showDatabaseError(this, e);
        }
    }

    /** DELETE */
    private void deleteItem() {
        T selected = list.getSelectedValue();
        if (selected == null) {
            Dialogs.showWarning(this, "Please select a " + itemLabel + " first.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "Delete \"" + selected + "\"?", "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.delete(selected);
            reload();
        } catch (SQLException e) {
            Dialogs.showDatabaseError(this, e);
        }
    }

    /** Returns the trimmed name, or null if the user cancelled or left it empty. */
    private String askName(String message, String initialValue) {
        String input = JOptionPane.showInputDialog(this, message, initialValue);
        if (input == null) {
            return null; // Cancel was pressed
        }
        input = input.trim();
        if (input.isEmpty()) {
            Dialogs.showWarning(this, "The name cannot be empty.");
            return null;
        }
        return input;
    }
}
