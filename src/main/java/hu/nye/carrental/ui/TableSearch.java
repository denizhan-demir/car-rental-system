package hu.nye.carrental.ui;

import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.regex.Pattern;

/**
 * Connects a search field to a table: every time the text changes,
 * only the rows that contain the text (in any column) stay visible.
 */
public final class TableSearch {

    private TableSearch() {
    }

    public static <M extends TableModel> void attach(JTextField field, TableRowSorter<M> sorter) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                apply(field, sorter);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                apply(field, sorter);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                apply(field, sorter);
            }
        });
    }

    private static <M extends TableModel> void apply(JTextField field, TableRowSorter<M> sorter) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            // (?i) = ignore upper/lower case, Pattern.quote = treat the text literally
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
        }
    }
}
