package hu.nye.carrental.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * Light / dark theme with an orange accent color (inspired by car rental websites).
 * The choice is saved with Java Preferences, so the app opens with the same theme next time.
 */
public final class Theme {

    private static final Preferences PREFS = Preferences.userNodeForPackage(Theme.class);
    private static final String DARK_KEY = "darkMode";

    private Theme() {
    }

    public static boolean isDark() {
        return PREFS.getBoolean(DARK_KEY, false);
    }

    /** Call once at startup, before any window is created. */
    public static void applySaved() {
        apply(isDark());
    }

    /** Switches the theme while the app is running and repaints every open window. */
    public static void setDark(boolean dark) {
        PREFS.putBoolean(DARK_KEY, dark);
        apply(dark);
        FlatLaf.updateUI();
    }

    private static void apply(boolean dark) {
        // accent color used by FlatLaf for default buttons, focus rings, check boxes, selections...
        FlatLaf.setGlobalExtraDefaults(Map.of("@accentColor", Styles.hex(Styles.ACCENT)));
        if (dark) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }

        // rounded, roomy controls
        UIManager.put("Component.arc", 10);
        UIManager.put("Button.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("CheckBox.arc", 6);
        UIManager.put("Button.margin", new Insets(6, 14, 6, 14));

        // tabs: taller, with a thicker orange line under the selected tab
        UIManager.put("TabbedPane.tabHeight", 42);
        UIManager.put("TabbedPane.tabSelectionHeight", 3);

        // tables: taller rows, only horizontal lines, zebra stripes
        UIManager.put("Table.rowHeight", 34);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
        UIManager.put("TableHeader.height", 36);
        UIManager.put("Table.alternateRowColor", dark ? new Color(0x4B4F51) : new Color(0xF8F9FB));

        // thin rounded scroll bars
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
    }
}
