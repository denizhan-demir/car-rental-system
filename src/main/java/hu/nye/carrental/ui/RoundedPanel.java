package hu.nye.carrental.ui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

/**
 * A panel with rounded corners and a thin border, like the cards on modern websites.
 * Colors come from the current theme (by UIManager key) every time it is painted,
 * so it follows light/dark mode automatically.
 */
public class RoundedPanel extends JPanel {

    private final String backgroundKey;
    private final int arc;

    public RoundedPanel(LayoutManager layout, String backgroundKey, int arc) {
        super(layout);
        this.backgroundKey = backgroundKey;
        this.arc = arc;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Styles.ui(backgroundKey, Color.WHITE));
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        g2.setColor(Styles.ui("Component.borderColor", Color.LIGHT_GRAY));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        g2.dispose();
        super.paintComponent(g);
    }
}
