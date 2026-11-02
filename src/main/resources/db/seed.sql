-- Sample data, loaded only on the very first start (when carrental.db is created).
-- Cars and rentals find their brand, category, car and customer by name, plate or license number,
-- so the script does not depend on the generated ids.
-- Rental dates are relative to today, so the demo always has active, overdue and closed rentals.

INSERT INTO brand (name) VALUES
    ('Toyota'), ('Volkswagen'), ('Skoda'), ('Suzuki'), ('Ford'), ('BMW'), ('Mercedes-Benz'), ('Tesla');

INSERT INTO category (name) VALUES
    ('Economy'), ('Compact'), ('SUV'), ('Premium'), ('Van'), ('Electric');

INSERT INTO car (brand_id, category_id, model, plate_number, year, daily_price, seats, transmission, fuel_type) VALUES
    ((SELECT id FROM brand WHERE name = 'Toyota'),        (SELECT id FROM category WHERE name = 'Economy'),  'Yaris Hybrid',   'AA BT-101', 2024, 34,  5, 'Automatic', 'Hybrid'),
    ((SELECT id FROM brand WHERE name = 'Volkswagen'),    (SELECT id FROM category WHERE name = 'Economy'),  'Polo',           'AA BT-102', 2023, 31,  5, 'Manual',    'Petrol'),
    ((SELECT id FROM brand WHERE name = 'Suzuki'),        (SELECT id FROM category WHERE name = 'Economy'),  'Swift',          'AA BT-103', 2024, 29,  5, 'Manual',    'Petrol'),
    ((SELECT id FROM brand WHERE name = 'Volkswagen'),    (SELECT id FROM category WHERE name = 'Compact'),  'Golf',           'AA BT-104', 2024, 42,  5, 'Automatic', 'Petrol'),
    ((SELECT id FROM brand WHERE name = 'Skoda'),         (SELECT id FROM category WHERE name = 'Compact'),  'Octavia Combi',  'AA BT-105', 2023, 45,  5, 'Manual',    'Diesel'),
    ((SELECT id FROM brand WHERE name = 'Toyota'),        (SELECT id FROM category WHERE name = 'Compact'),  'Corolla',        'AA BT-106', 2024, 44,  5, 'Automatic', 'Hybrid'),
    ((SELECT id FROM brand WHERE name = 'Volkswagen'),    (SELECT id FROM category WHERE name = 'SUV'),      'Tiguan',         'AA BT-107', 2024, 64,  5, 'Automatic', 'Diesel'),
    ((SELECT id FROM brand WHERE name = 'Suzuki'),        (SELECT id FROM category WHERE name = 'SUV'),      'Vitara',         'AA BT-108', 2023, 49,  5, 'Manual',    'Petrol'),
    ((SELECT id FROM brand WHERE name = 'BMW'),           (SELECT id FROM category WHERE name = 'Premium'),  '5 Series',       'AA BT-109', 2024, 99,  5, 'Automatic', 'Diesel'),
    ((SELECT id FROM brand WHERE name = 'Mercedes-Benz'), (SELECT id FROM category WHERE name = 'Premium'),  'E-Class',        'AA BT-110', 2024, 105, 5, 'Automatic', 'Hybrid'),
    ((SELECT id FROM brand WHERE name = 'Ford'),          (SELECT id FROM category WHERE name = 'Van'),      'Tourneo Custom', 'AA BT-111', 2024, 89,  8, 'Automatic', 'Diesel'),
    ((SELECT id FROM brand WHERE name = 'Tesla'),         (SELECT id FROM category WHERE name = 'Electric'), 'Model 3',        'AA BT-112', 2024, 79,  5, 'Automatic', 'Electric');

INSERT INTO customer (first_name, last_name, email, phone, license_number) VALUES
    ('Anna',   'Kovács',  'anna.kovacs@example.com',   '+36 30 111 2233', 'AB123456'),
    ('Bence',  'Nagy',    'bence.nagy@example.com',    '+36 20 222 3344', 'CD234567'),
    ('Lena',   'Schmidt', 'lena.schmidt@example.com',  NULL,              'EF345678'),
    ('Márk',   'Tóth',    NULL,                        '+36 70 333 4455', 'GH456789'),
    ('Zeynep', 'Arslan',  'zeynep.arslan@example.com', '+36 30 444 5566', 'JK567890'),
    ('Péter',  'Szabó',   'peter.szabo@example.com',   '+36 20 555 6677', 'LM678901');

-- Closed rentals: the car came back, total price = days x daily price
INSERT INTO rental (car_id, customer_id, start_date, end_date, return_date, total_price)
SELECT car.id, customer.id, date('now', '-30 days'), date('now', '-25 days'), date('now', '-25 days'), 5 * car.daily_price
FROM car, customer WHERE car.plate_number = 'AA BT-101' AND customer.license_number = 'AB123456';

INSERT INTO rental (car_id, customer_id, start_date, end_date, return_date, total_price)
SELECT car.id, customer.id, date('now', '-21 days'), date('now', '-18 days'), date('now', '-17 days'), 4 * car.daily_price
FROM car, customer WHERE car.plate_number = 'AA BT-104' AND customer.license_number = 'CD234567';

INSERT INTO rental (car_id, customer_id, start_date, end_date, return_date, total_price)
SELECT car.id, customer.id, date('now', '-14 days'), date('now', '-7 days'), date('now', '-7 days'), 7 * car.daily_price
FROM car, customer WHERE car.plate_number = 'AA BT-107' AND customer.license_number = 'JK567890';

-- Active rentals: the car is out and the planned end date is today or later
INSERT INTO rental (car_id, customer_id, start_date, end_date)
SELECT car.id, customer.id, date('now', '-2 days'), date('now', '+3 days')
FROM car, customer WHERE car.plate_number = 'AA BT-105' AND customer.license_number = 'EF345678';

INSERT INTO rental (car_id, customer_id, start_date, end_date)
SELECT car.id, customer.id, date('now', '-1 days'), date('now', '+4 days')
FROM car, customer WHERE car.plate_number = 'AA BT-109' AND customer.license_number = 'LM678901';

INSERT INTO rental (car_id, customer_id, start_date, end_date)
SELECT car.id, customer.id, date('now'), date('now', '+2 days')
FROM car, customer WHERE car.plate_number = 'AA BT-112' AND customer.license_number = 'AB123456';

-- Overdue rental: still out, but the planned end date has passed
INSERT INTO rental (car_id, customer_id, start_date, end_date)
SELECT car.id, customer.id, date('now', '-9 days'), date('now', '-2 days')
FROM car, customer WHERE car.plate_number = 'AA BT-108' AND customer.license_number = 'GH456789';
