package com.library;

public class Copy {
    private String barcode;
    private String shelfLocation;
    private String status;
    private Item item;

    public Copy(String barcode, String shelfLocation, String status, Item item) {
        this.barcode = barcode;
        this.shelfLocation = shelfLocation;
        this.status = status;
        this.item = item;
    }

    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }

    public String getBarcode() {return barcode;}
    public String getShelfLocation() {return shelfLocation;}
    public String getStatus() {return status;}
    public Item getItem() {return item;}

    public void setShelfLocation(String shelfLocation) { this.shelfLocation = shelfLocation; }
}