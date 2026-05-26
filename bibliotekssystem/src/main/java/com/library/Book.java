package com.library;

public class Book extends Item {
    private String author;
    private String isbn;
    private String bookClassification;

    public Book(int itemID, String title, String itemCategory, String author, String isbn, String bookClassification) {

        super(itemID, title, "Book", itemCategory);
        this.author = author;
        this.isbn = isbn;
        this.bookClassification = bookClassification;
    }

    @Override
    public int calculateLoanPeriod() {

        if ("COURSE_LITERATURE".equals(getItemCategory())) {
            return 14;
        }
        return 30;
    }

    public String getAuthor() {return author;}
    public String getIsbn() {return isbn;}
    public String getBookClassification() {return bookClassification;}
}