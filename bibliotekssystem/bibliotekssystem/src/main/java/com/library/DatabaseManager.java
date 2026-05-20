package com.library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String URL = "jdbc:postgresql://localhost:5432/LibraryDB";

    private static final String USER = "postgres"; 
    private static final String PASSWORD = "postgres"; 

    public static Connection getConnection() {
        Connection connection = null;
        try {

            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {

            System.err.println("Kunde inte ansluta till databasen!");
            System.err.println("Felmeddelande: " + e.getMessage());
        }
        return connection;
    }
}