package hu.nye.carrental.model;

public class Car {
    private int id;
    private int brandId;
    private int categoryId;
    private String model;
    private String plateNumber;
    private int year;
    private double dailyPrice;
    private int seats = 5;
    private String transmission = "Manual"; // Manual / Automatic
    private String fuelType = "Petrol";     // Petrol / Diesel / Hybrid / Electric

    // filled from JOIN queries, only for displaying
    private String brandName;
    private String categoryName;
    // true if the car has no active rental (calculated in the SQL query)
    private boolean available = true;

    public Car() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBrandId() { return brandId; }
    public void setBrandId(int brandId) { this.brandId = brandId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public double getDailyPrice() { return dailyPrice; }
    public void setDailyPrice(double dailyPrice) { this.dailyPrice = dailyPrice; }

    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return brandName + " " + model + " (" + plateNumber + ")"; // shown in drop-down lists
    }
}
