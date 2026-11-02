package hu.nye.carrental.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Common operations for simple tables that only have an id and a name (brand, category).
 * The Brands & Categories screen talks to this interface, so the same panel
 * can be reused for both tables.
 */
public interface NameDao<T> {

    List<T> findAll() throws SQLException;

    void insert(String name) throws SQLException;

    void rename(T item, String newName) throws SQLException;

    void delete(T item) throws SQLException;
}
