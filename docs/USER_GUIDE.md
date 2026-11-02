# Car Rental System – User Guide

This guide explains how to use the Car Rental System, from the first start to closing a rental.
No programming knowledge is needed to follow it.

---

## 1. Purpose of the application

The Car Rental System helps the employees of a small car rental office with their daily work.
Instead of paper or Excel sheets, all information is kept in one place:

- which **cars** the office has, what they cost per day, and whether they are available or rented,
- who the **customers** are,
- which car was **rented** by whom, when it has to come back, and how much the customer has to pay.

The application is used by office employees, so there is no separate login for customers.

---

## 2. Starting the application

**Requirements:** Java 17 or newer.

1. Open the project folder in IntelliJ IDEA as a Maven project.
2. Run the class `hu.nye.carrental.Main`
   (or run `mvn compile exec:java` in a terminal inside the project folder).
3. The main window opens on the **Rentals** tab.

All data is saved in the file `carrental.db` in the project folder. It is created automatically,
and changes are saved immediately – there is no "Save" button.

**Sample data:** on the very first start the database is filled with 12 cars, 6 customers and
7 rentals (closed, active and overdue), so every screen can be tried out right away.
To start again with fresh sample data, close the application and delete `carrental.db`.

---

## 3. The main window

The window has four tabs. The orange line shows which one is open.

| Tab | What you can do there |
|---|---|
| **Rentals** | see all rentals, create a rental, take a car back (close the rental), delete a rental |
| **Cars** | list, add, edit, delete and search cars; see if a car is available or rented |
| **Customers** | list, add, edit, delete and search customers |
| **Brands & Categories** | manage the lists of car brands and car categories |

Every tab is reloaded when you open it, so it always shows the current data.

**Light / dark theme:** the **Dark mode** button in the top right corner switches the theme
(it then shows **Light mode** to switch back). The application remembers the choice.

All tabs work the same way: a **search field** on the left, buttons on the right
(the orange one is the main action), and a **table** below.

---

## 4. Getting started – step by step

1. **Customers** – add the customer with **+ Add customer** (a rental always needs a customer).
2. **Rentals** – click **+ New rental**, choose a car and the customer, set the dates and click **Create rental**.
3. When the car comes back, select the rental on the **Rentals** tab and click **Return car**.
   The final price is calculated automatically.

To add your own cars, use the **Cars** tab. Every car needs a brand and a category;
if the one you need is missing, add it on the **Brands & Categories** tab first.

---

## 5. Main functions

### 5.1 Rentals

The table shows all rentals, active ones first: car, customer, start date, planned end date,
return date, total price and **status**:

- **● Active** (green) – the car is out and the planned end date has not passed yet.
- **● Overdue** (red) – the car is still out, but the planned end date has already passed.
- **● Closed** (grey) – the car was returned; the return date and the total price are filled in.

**Create a rental**

1. Click **+ New rental**.
2. Choose the car (only available cars are listed) and the customer.
3. Set the start date and the planned end date. Dates use the format `yyyy-MM-dd`;
   you can type them, or click into the day, month or year and use the arrow buttons.
4. The **Estimated price** below updates while you change the car or the dates,
   for example `€105.00 (3 days × €35.00)`.
5. Click **Create rental**. The car's status changes to *Rented*.

**Return a car (close a rental)**

1. Select an active or overdue rental and click **Return car**.
2. Set the return date (today by default) and click **OK**.
3. A summary shows the customer, the number of days, the daily price and the **total price**. Click **Yes**.
4. The rental becomes *Closed* and the car is *Available* again.

**How the price is calculated:** number of days × daily price of the car.
The number of days is the difference between the start date and the return date.
A car returned on the same day counts as one day, and late days are charged too.

**Delete a rental** – select a rental, click **Delete** and confirm.
If the rental was still active, the car becomes available again.

### 5.2 Cars

The table shows all cars: brand, model, category, plate number, year, seats, gearbox, fuel,
daily price and status (**● Available** in green, **● Rented** in orange).

**Add a car**

1. Click **+ Add car**.
2. Choose the brand and the category from the lists.
3. Fill in the model and the plate number (the plate number is converted to capital letters).
4. Set the year, the daily price, the number of seats, the gearbox and the fuel type.
5. Click **Save** (or press Enter).

**Edit a car** – select the car and click **Edit** (or double-click the row), change the data, click **Save**.

**Delete a car** – select the car, click **Delete** and confirm.
A car that has rentals (active or closed) cannot be deleted, because the rental history would be lost.

### 5.3 Customers

The table shows all customers with their name, email, phone and driving license number.

**Add a customer**

1. Click **+ Add customer**.
2. Fill in the first name, last name and driving license number. These are required (marked with *).
3. Email and phone are optional.
4. Click **Save**.

**Edit a customer** – select the customer and click **Edit** (or double-click the row).

**Delete a customer** – select the customer, click **Delete** and confirm.
A customer who has rentals cannot be deleted.

### 5.4 Brands & Categories

The tab shows two cards side by side, **Brands** and **Categories**. Both work the same way:

- **+ Add** – type the name and click OK.
- **Edit** – select an item and click Edit (or double-click it), type the new name and click OK.
- **Delete** – select an item, click Delete and confirm.

A brand or category that is used by at least one car cannot be deleted.
Change or delete those cars first.

---

## 6. CRUD operations overview

CRUD means the four basic data operations: **C**reate, **R**ead, **U**pdate, **D**elete.

| Data | Create | Read | Update | Delete |
|---|---|---|---|---|
| Brand | Brands & Categories → + Add | Brands list | Edit / double-click | Delete |
| Category | Brands & Categories → + Add | Categories list | Edit / double-click | Delete |
| Car | Cars → + Add car | Cars table, search, sorting | Edit / double-click | Delete |
| Customer | Customers → + Add customer | Customers table, search, sorting | Edit / double-click | Delete |
| Rental | Rentals → + New rental | Rentals table, search, sorting | Return car (closes the rental) | Delete |

---

## 7. Messages and what they mean

| Message | Reason | What to do |
|---|---|---|
| *This value already exists.* | A brand or category with this name already exists. | Use a different name. |
| *A car with this plate number already exists.* | Plate numbers must be unique. | Check the plate number. |
| *A customer with this license number already exists.* | The customer is probably already registered. | Search for the customer in the table. |
| *This item is used by other records…* | You tried to delete something that a car or a rental still uses. | Delete or change the related records first. |
| *There are no available cars at the moment.* | Every car is rented. | Close a rental or add a new car. |
| *Please add a customer first.* | A rental needs a customer. | Add one on the Customers tab. |
| *Please select … first.* | You clicked a button without selecting a row. | Click a row, then the button. |
| *The end date cannot be before the start date.* | The dates are in the wrong order. | Correct the dates. |

---

## 8. Tips

- **Search:** type into the search field above a table. Only rows that contain the text in any
  column stay visible; upper and lower case do not matter. Examples: `overdue` on Rentals,
  `automatic` or `rented` on Cars. The **×** in the field clears the search.
- **Sorting:** click a column header to sort by that column; click again to reverse the order.
- **Keyboard:** in the add and edit windows, **Enter** saves.
