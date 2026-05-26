package com.library;

public abstract class Item {
    private int itemID;
    private String title;
    private String itemClassification;
    private String itemCategory;

    public Item(int itemID, String title, String itemClassification, String itemCategory) {
        this.itemID = itemID;
        this.title = title;
        this.itemClassification = itemClassification;
        this.itemCategory = itemCategory;
    }

    public abstract int calculateLoanPeriod();

    public int getItemID() {return itemID;}
    public String getTitle() {return title;}
    public String getItemClassification() {return itemClassification;}
    public String getItemCategory() {return itemCategory;}

    public void setItemCategory(String itemCategory) {this.itemCategory = itemCategory;}

    @Override
    public String toString() {
    return this.title;
    }
}