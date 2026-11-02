package hu.nye.carrental;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Starts the application. For now it only opens an empty window,
 * to check that the project, Maven and FlatLaf work on every computer.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();

            JLabel label = new JLabel("Car Rental System - work in progress", SwingConstants.CENTER);
            label.putClientProperty("FlatLaf.styleClass", "h2");

            JFrame frame = new JFrame("Car Rental System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(label);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
