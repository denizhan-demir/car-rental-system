package hu.nye.carrental.model;

import java.time.LocalDate;

public class Rental {
    private int id;
    private int carId;
    private int customerId;
    private LocalDate startDate;
    private LocalDate endDate;     // planned return date
    private LocalDate returnDate;  // null while the rental is active
    private Double totalPrice;     // null until the rental is closed

    // filled from JOIN queries, only for displaying
    private String carLabel;
    private String customerName;
    private double dailyPrice;     // the car's current daily price

    public Rental() {
    }

    public boolean isActive() {
        return returnDate == null;
    }

    /** "Closed" if the car was returned, "Overdue" if it is late, otherwise "Active". */
    public String getStatus(LocalDate today) {
        if (!isActive()) {
            return "Closed";
        }
        if (endDate.isBefore(today)) {
            return "Overdue";
        }
        return "Active";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCarId() { return carId; }
    public void setCarId(int carId) { this.carId = carId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public String getCarLabel() { return carLabel; }
    public void setCarLabel(String carLabel) { this.carLabel = carLabel; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public double getDailyPrice() { return dailyPrice; }
    public void setDailyPrice(double dailyPrice) { this.dailyPrice = dailyPrice; }
}
