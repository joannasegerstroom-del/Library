package com.library;

import java.util.ArrayList;
import java.util.List;

public class UserSession {
    private static LibraryUser loggedInUser = null;
    
    private static List<Item> cart = new ArrayList<>();

    public static void logIn(LibraryUser user) {
        loggedInUser = user;
    }

    public static void logOut() {
        loggedInUser = null;
        cart.clear();
    }

    public static LibraryUser getLoggedInUser() {
        return loggedInUser;
    }

    public static boolean isLoggedIn() {
        return loggedInUser != null;
    }

    public static boolean isAdmin() {
        return loggedInUser != null && "ADMIN".equalsIgnoreCase(loggedInUser.getUserCategory());
    }

    public static void addToCart(Item item) {
        cart.add(item);
    }

    public static List<Item> getCart() {
        return cart;
    }

    public static void clearCart() {
        cart.clear();
    }
}