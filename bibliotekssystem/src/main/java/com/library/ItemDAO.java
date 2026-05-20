package com.library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    public List<Item> searchByKeyword(String keyword, String filter) {
    List<Item> resultList = new ArrayList<>();

    String sql = "SELECT i.itemID, i.title, i.itemClassification, i.itemCategory, " +
                 "b.author, b.isbn, b.bookClassification, " +
                 "d.director, d.genre, d.actor " +
                 "FROM Item i " +
                 "LEFT JOIN Book b ON i.itemID = b.itemID " +
                 "LEFT JOIN DVD d ON i.itemID = d.itemID " +
                 "WHERE (i.title ILIKE ? " +
                 "OR b.author ILIKE ? " +
                 "OR b.isbn ILIKE ? " +
                 "OR d.director ILIKE ? " +
                 "OR d.genre ILIKE ? " +
                 "OR d.actor ILIKE ? " +
                 "OR b.bookClassification ILIKE ? " +
                 "OR i.itemCategory ILIKE ?) "; 

    if (filter != null && !filter.isEmpty()) {
        switch (filter) {
            case "Böcker":
                sql += " AND i.itemClassification = 'Book' AND i.itemCategory = 'OTHER_BOOK'";
                break;
            case "Filmer":
                sql += " AND i.itemClassification = 'DVD'";
                break;
            case "Kurslitteratur":
                sql += " AND i.itemCategory = 'COURSE_LITERATURE'";
                break;
            case "Tidskrifter":
                sql += " AND i.itemClassification = 'Magazine'";
                break;
        }
    }

    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        String searchPattern = "%" + keyword + "%";
        
        for (int i = 1; i <= 8; i++) {
            pstmt.setString(i, searchPattern);
        }
        
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("itemID");
            String title = rs.getString("title");
            String classification = rs.getString("itemClassification");
            String category = rs.getString("itemCategory");

            if ("Book".equalsIgnoreCase(classification)) {
                String author = rs.getString("author");
                String isbn = rs.getString("isbn");
                String bookClass = rs.getString("bookClassification");
                resultList.add(new Book(id, title, category, author, isbn, bookClass));
            } else if ("DVD".equalsIgnoreCase(classification)) {
                String director = rs.getString("director");
                String genre = rs.getString("genre");
                String actor = rs.getString("actor");
                resultList.add(new DVD(id, title, category, director, genre, actor));
            } else {
                resultList.add(new Magazine(id, title, category, classification));
            }
        }
    } catch (SQLException e) {
        System.err.println("Ett fel uppstod vid sökning i databasen: " + e.getMessage());
    }

    return resultList; 
    }
}