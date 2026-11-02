package hu.nye.carrental.ui;

/**
 * Tabs that implement this are reloaded from the database every time they are opened,
 * so they always show fresh data (for example after a rental changed a car's status).
 */
public interface Refreshable {
    void refresh();
}
