package com.library;

public class Magazine extends Item {

    public Magazine(int itemID, String title, String itemCategory, String itemClassification) {
        super(itemID, title, itemCategory, itemClassification);
    }

    @Override
    public int calculateLoanPeriod() {
        return 0; 
    }
}