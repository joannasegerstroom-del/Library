package com.library;

public class LibraryUser {
    
    private int userID;
    private String name;
    private String email;
    private String userCategory;

    public LibraryUser(int userID, String name, String email, String userCategory) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.userCategory = userCategory;
    }

    public int getUserID() {
        return userID;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getUserCategory() {
        return userCategory;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setUserCategory(String userCategory) {
        this.userCategory = userCategory;
    }
}