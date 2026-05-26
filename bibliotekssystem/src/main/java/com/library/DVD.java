package com.library;

public class DVD extends Item {
    private String director;
    private String genre;
    private String actor;

    public DVD(int itemID, String title, String itemCategory, String director, String genre, String actor) {
        super(itemID, title, "DVD", itemCategory);
        this.director = director;
        this.genre = genre;
        this.actor = actor;
    }

    @Override
    public int calculateLoanPeriod() {
        return 7;
    }

    public String getDirector() {return director;}
    public String getGenre() {return genre;}
    public String getActor() {return actor;}
}