package hu.nye.carrental.ui;

import javax.swing.JLabel;
import javax.swing.UIManager;

/**
 * Grey secondary text. Picks the grey of the current theme again after a theme switch.
 */
public class MutedLabel extends JLabel {

    public MutedLabel(String text) {
        super(text);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setForeground(UIManager.getColor("Label.disabledForeground"));
    }
}
