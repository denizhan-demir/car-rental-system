package hu.nye.carrental.ui;

import hu.nye.carrental.dao.BrandDao;
import hu.nye.carrental.dao.CategoryDao;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.GridLayout;

/**
 * "Brands & Categories" tab: two cards side by side.
 */
public class BrandCategoryPanel extends JPanel implements Refreshable {

    private final NamePanel<?> brands = new NamePanel<>("Brands", "Car manufacturers", "brand", new BrandDao());
    private final NamePanel<?> categories = new NamePanel<>("Categories", "Groups of cars, e.g. Economy or SUV", "category", new CategoryDao());

    public BrandCategoryPanel() {
        super(new GridLayout(1, 2, 20, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        add(brands);
        add(categories);
    }

    @Override
    public void refresh() {
        brands.reload();
        categories.reload();
    }
}
