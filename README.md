# Car Rental System

Semester project for **BAI0168 – The Technology and Methodology of System Development**
(University of Nyíregyháza, 2026/27 autumn semester).

A desktop application for a small car rental office. It keeps track of cars, customers and rentals,
shows which cars are available, and calculates the price when a car is returned.

**Documentation:** [User guide](docs/USER_GUIDE.md) · [Database design](docs/database.md)

## Features

- Brands and categories: create, list, rename, delete
- Cars: create, list, edit, delete, search; availability status calculated from the rentals
- Customers: create, list, edit, delete, search
- Rentals: create, return a car with automatic price calculation, delete; active / overdue / closed status
- Sample data on the first start, light and dark theme

## Team

| Member | Responsibility |
|---|---|
| Denizhan Demir | Frontend (user interface) and project setup |
| Erman Yazgan | Backend (data model, CRUD operations, business rules) |
| Emek Mert Öçal | Database (tables, sample data) and documentation |

## Technologies

- Java 17+, Swing with FlatLaf (user interface)
- SQLite database via JDBC
- Maven, Git, GitHub

## Running

Open the folder in IntelliJ IDEA as a Maven project and run `hu.nye.carrental.Main`,
or run `mvn compile exec:java` in the project folder.

The database file `carrental.db` is created automatically in the project folder on the first start.

## Project structure

    src/main/java/hu/nye/carrental/
    ├── Main.java   – starts the application
    ├── db/         – database connection and setup             (Database)
    ├── model/      – data classes                              (Backend)
    ├── dao/        – SQL queries for each table (CRUD)          (Backend)
    ├── service/    – business rules, e.g. price calculation     (Backend)
    └── ui/         – screens, tables and forms                  (Frontend)
    src/main/resources/db/
    ├── schema.sql  – database tables                            (Database)
    └── seed.sql    – sample data for the first start            (Database)
