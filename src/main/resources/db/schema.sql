-- Car Rental System - database tables (SQLite)
-- Dates are stored as TEXT in yyyy-MM-dd format, because SQLite has no DATE type.
-- This script runs on every start. IF NOT EXISTS makes that safe.

CREATE TABLE IF NOT EXISTS brand (
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT    NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS category (
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT    NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS car (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    brand_id     INTEGER NOT NULL,
    category_id  INTEGER NOT NULL,
    model        TEXT    NOT NULL,
    plate_number TEXT    NOT NULL UNIQUE,
    year         INTEGER NOT NULL,
    daily_price  REAL    NOT NULL CHECK (daily_price > 0),
    seats        INTEGER NOT NULL DEFAULT 5,
    transmission TEXT    NOT NULL DEFAULT 'Manual' CHECK (transmission IN ('Manual', 'Automatic')),
    fuel_type    TEXT    NOT NULL DEFAULT 'Petrol' CHECK (fuel_type IN ('Petrol', 'Diesel', 'Hybrid', 'Electric')),
    FOREIGN KEY (brand_id)    REFERENCES brand (id),
    FOREIGN KEY (category_id) REFERENCES category (id)
);

CREATE TABLE IF NOT EXISTS customer (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name     TEXT NOT NULL,
    last_name      TEXT NOT NULL,
    email          TEXT,
    phone          TEXT,
    license_number TEXT NOT NULL UNIQUE
);

-- return_date and total_price stay NULL while the car is out (active rental)
CREATE TABLE IF NOT EXISTS rental (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    car_id      INTEGER NOT NULL,
    customer_id INTEGER NOT NULL,
    start_date  TEXT    NOT NULL,
    end_date    TEXT    NOT NULL CHECK (end_date >= start_date),
    return_date TEXT,
    total_price REAL,
    FOREIGN KEY (car_id)      REFERENCES car (id),
    FOREIGN KEY (customer_id) REFERENCES customer (id)
);

-- Makes the check "is this car rented right now?" fast
CREATE INDEX IF NOT EXISTS idx_rental_car_return ON rental (car_id, return_date);
