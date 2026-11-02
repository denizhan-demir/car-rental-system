package hu.nye.carrental;

import hu.nye.carrental.db.Database;
import hu.nye.carrental.ui.BrandCategoryPanel;
import hu.nye.carrental.ui.CarPanel;
import hu.nye.carrental.ui.CustomerPanel;
import hu.nye.carrental.ui.MutedLabel;
import hu.nye.carrental.ui.Refreshable;
import hu.nye.carrental.ui.Styles;
import hu.nye.carrental.ui.Theme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Starts the application: prepares the database, then opens the main window with one tab per screen.
 */
public class Main {

    public static void main(String[] args) {
        Database.init();
        SwingUtilities.invokeLater(Main::createWindow);
    }

    private static void createWindow() {
        Theme.applySaved(); // must happen before any window is created

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Rentals", comingSoon("Rentals"));
        tabs.addTab("Cars", new CarPanel());
        tabs.addTab("Customers", new CustomerPanel());
        tabs.addTab("Brands & Categories", new BrandCategoryPanel());

        // FlatLaf feature: app name on the left of the tabs, theme switch on the right
        JLabel appName = Styles.font(new JLabel("Car Rental"), "h3");
        appName.setForeground(Styles.ACCENT);
        appName.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 28));
        tabs.putClientProperty("JTabbedPane.leadingComponent", appName);
        tabs.putClientProperty("JTabbedPane.trailingComponent", createThemeSwitch());

        // reload a tab from the database every time it is opened, so it always shows fresh data
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() instanceof Refreshable refreshable) {
                refreshable.refresh();
            }
        });

        JFrame frame = new JFrame("Car Rental System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(tabs);
        frame.setSize(1150, 720);
        frame.setMinimumSize(new Dimension(900, 560));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /** "Dark mode" / "Light mode" button, kept on the right edge of the tab bar. */
    private static JPanel createThemeSwitch() {
        JButton button = new JButton(Theme.isDark() ? "Light mode" : "Dark mode");
        button.addActionListener(e -> {
            Theme.setDark(!Theme.isDark()); // the choice is remembered for the next start
            button.setText(Theme.isDark() ? "Light mode" : "Dark mode");
        });
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(0, 0, 0, 16);
        panel.add(button, c);
        return panel;
    }

    /** Temporary tab content, replaced step by step with the real screens. */
    private static JPanel comingSoon(String name) {
        JPanel panel = new JPanel(new BorderLayout());
        MutedLabel label = new MutedLabel(name + " screen - coming soon");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
}
