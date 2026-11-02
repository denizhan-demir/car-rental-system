package hu.nye.carrental.ui;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;

/**
 * Shared colors and small helpers, so every screen looks the same.
 */
public final class Styles {

    public static final Color ACCENT = new Color(0xFF5F00);   // rental-company orange
    public static final Color SUCCESS = new Color(0x1F9D55);
    public static final Color DANGER = new Color(0xE5484D);
    public static final Color MUTED = new Color(0x8A8F98);

    private Styles() {
    }

    /** Theme color by UIManager key, with a fallback. Read at paint time so theme switches work. */
    public static Color ui(String key, Color fallback) {
        Color color = UIManager.getColor(key);
        return color != null ? color : fallback;
    }

    public static String hex(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    /** Filled orange button for the main action of a screen. */
    public static <B extends AbstractButton> B primary(B button) {
        button.setBackground(ACCENT);
        button.setForeground(Color.WHITE);
        button.setFont(button.getFont().deriveFont(Font.BOLD));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    /** Button with red text for delete actions. */
    public static JButton danger(JButton button) {
        button.setForeground(DANGER);
        return button;
    }

    /** FlatLaf typography: "h1", "h2", "h3", "large", "small"... */
    public static <C extends JComponent> C font(C component, String styleClass) {
        component.putClientProperty("FlatLaf.styleClass", styleClass);
        return component;
    }

    /** Search field with placeholder text and a clear (x) button. */
    public static JTextField searchField(String placeholder) {
        JTextField field = new JTextField(26);
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.putClientProperty("JTextField.showClearButton", true);
        return field;
    }

    /** Toolbar above a table: search on the left, buttons on the right. */
    public static JPanel toolbar(JComponent left, JButton... buttons) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.add(left, BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        for (JButton button : buttons) {
            right.add(button);
        }
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    /** Puts a table into a rounded card without the default scroll pane border. */
    public static JComponent tableCard(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        RoundedPanel card = new RoundedPanel(new BorderLayout(), "Table.background", 14);
        card.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    /** A title with a smaller grey line below it. */
    public static JPanel heading(String title, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout(0, 2));
        panel.setOpaque(false);
        panel.add(font(new JLabel(title), "h3"), BorderLayout.NORTH);
        panel.add(new MutedLabel(subtitle), BorderLayout.SOUTH);
        return panel;
    }
}
