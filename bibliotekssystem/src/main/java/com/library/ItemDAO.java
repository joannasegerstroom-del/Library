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
                    sql += " AND i.itemClassification = 'Book' AND i.itemCategory = 'Bok'";
                    break;
                case "Filmer":
                    sql += " AND i.itemClassification = 'DVD'";
                    break;
                case "Kurslitteratur":
                    sql += " AND i.itemCategory = 'Kurslitteratur'";
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

    public boolean addBook(String title, String category, String author, String isbn, String bookClass) {
        String insertItem = "INSERT INTO Item (itemID, title, itemClassification, itemCategory) VALUES ((SELECT COALESCE(MAX(itemID), 0) + 1 FROM Item), ?, 'Book', ?) RETURNING itemID";
        String insertBook = "INSERT INTO Book (itemID, author, isbn, bookClassification) VALUES (?, ?, ?, ?)";

        try (java.sql.Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try (java.sql.PreparedStatement pstmtItem = conn.prepareStatement(insertItem);
                 java.sql.PreparedStatement pstmtBook = conn.prepareStatement(insertBook)) {

                pstmtItem.setString(1, title);
                pstmtItem.setString(2, category);
                java.sql.ResultSet rs = pstmtItem.executeQuery();

                if (rs.next()) {
                    int itemId = rs.getInt(1);
                    pstmtBook.setInt(1, itemId);
                    pstmtBook.setString(2, author);
                    pstmtBook.setString(3, isbn);
                    pstmtBook.setString(4, bookClass);
                    pstmtBook.executeUpdate();
                    conn.commit();
                    return true;
                }
            } catch (java.sql.SQLException e) {
                conn.rollback();
                System.err.println("Fel vid tillägg av bok: " + e.getMessage());
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Databasanslutningsfel: " + e.getMessage());
        }
        return false;
    }

    public boolean addDVD(String title, String category, String director, String genre, String actor) {
        String insertItem = "INSERT INTO Item (itemID, title, itemClassification, itemCategory) VALUES ((SELECT COALESCE(MAX(itemID), 0) + 1 FROM Item), ?, 'DVD', ?) RETURNING itemID";
        String insertDVD = "INSERT INTO DVD (itemID, director, genre, actor) VALUES (?, ?, ?, ?)";

        try (java.sql.Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try (java.sql.PreparedStatement pstmtItem = conn.prepareStatement(insertItem);
                 java.sql.PreparedStatement pstmtDVD = conn.prepareStatement(insertDVD)) {

                pstmtItem.setString(1, title);
                pstmtItem.setString(2, category);
                java.sql.ResultSet rs = pstmtItem.executeQuery();

                if (rs.next()) {
                    int itemId = rs.getInt(1);
                    pstmtDVD.setInt(1, itemId);
                    pstmtDVD.setString(2, director);
                    pstmtDVD.setString(3, genre);
                    pstmtDVD.setString(4, actor);
                    pstmtDVD.executeUpdate();
                    conn.commit();
                    return true;
                }
            } catch (java.sql.SQLException e) {
                conn.rollback();
                System.err.println("Fel vid tillägg av DVD: " + e.getMessage());
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Databasanslutningsfel: " + e.getMessage());
        }
        return false;
    }

    public boolean addMagazine(String title, String category) {
        String insertItem = "INSERT INTO Item (itemID, title, itemClassification, itemCategory) VALUES ((SELECT COALESCE(MAX(itemID), 0) + 1 FROM Item), ?, 'Magazine', ?)";

        try (java.sql.Connection conn = DatabaseManager.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(insertItem)) {

            pstmt.setString(1, title);
            pstmt.setString(2, category);
            pstmt.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            System.err.println("Fel vid tillägg av tidskrift: " + e.getMessage());
            return false;
        }
    }

    public boolean updateItemBasic(int itemId, String newTitle, String newCategory) {
        String updateSql = "UPDATE Item SET title = ?, itemCategory = ? WHERE itemID = ?";
        try (java.sql.Connection conn = DatabaseManager.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            
            pstmt.setString(1, newTitle);
            pstmt.setString(2, newCategory);
            pstmt.setInt(3, itemId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (java.sql.SQLException e) {
            System.err.println("Fel vid uppdatering: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteItem(int itemId) {
        String deleteBook = "DELETE FROM Book WHERE itemID = ?";
        String deleteDVD = "DELETE FROM DVD WHERE itemID = ?";
        String deleteItem = "DELETE FROM Item WHERE itemID = ?";
        
        try (java.sql.Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try (java.sql.PreparedStatement pb = conn.prepareStatement(deleteBook);
                 java.sql.PreparedStatement pd = conn.prepareStatement(deleteDVD);
                 java.sql.PreparedStatement pi = conn.prepareStatement(deleteItem)) {
                
                pb.setInt(1, itemId);
                pb.executeUpdate();
                
                pd.setInt(1, itemId);
                pd.executeUpdate();
                
                pi.setInt(1, itemId);
                pi.executeUpdate();
                
                conn.commit();
                return true;
            } catch (java.sql.SQLException e) {
                conn.rollback();
                System.err.println("Fel vid borttagning: " + e.getMessage());
                return false;
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Databasanslutningsfel: " + e.getMessage());
            return false;
        }
    }

    public String getAvailableBarcode(int itemId) {
        String sql = "SELECT barcode FROM Copy WHERE itemID = ? AND status = 'AVAILABLE' LIMIT 1";
        try (java.sql.Connection conn = DatabaseManager.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, itemId);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("barcode");
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Kunde inte hämta ledig kopia: " + e.getMessage());
        }
        return null;
    }

    public boolean createLoan(String receiptNumber, int userId, String barcode) {
        String insertSession = "INSERT INTO checksession (receiptnumber, loandate, userid) VALUES (?, CURRENT_DATE, ?)";
        String insertLoan = "INSERT INTO Loan (loanid, receiptnumber, barcode, duedate) VALUES ((SELECT COALESCE(MAX(loanid), 0) + 1 FROM Loan), ?, ?, CURRENT_DATE + INTERVAL '30 days')";
        String updateCopy = "UPDATE Copy SET status = 'LOANED' WHERE barcode = ?";

        try (java.sql.Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            
            try (java.sql.PreparedStatement pSession = conn.prepareStatement(insertSession);
                 java.sql.PreparedStatement pLoan = conn.prepareStatement(insertLoan);
                 java.sql.PreparedStatement pCopy = conn.prepareStatement(updateCopy)) {

                pSession.setString(1, receiptNumber);
                pSession.setInt(2, userId);
                pSession.executeUpdate();
                pLoan.setString(1, receiptNumber);
                pLoan.setString(2, barcode); 
                pLoan.executeUpdate();
                pCopy.setString(1, barcode); 
                pCopy.executeUpdate();

                conn.commit();
                return true;
            } catch (java.sql.SQLException e) {
                conn.rollback();
                System.err.println("Fel vid skapande av lån: " + e.getMessage());
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Databasanslutningsfel: " + e.getMessage());
        }
        return false;
    }

    private int getActiveLoanCount(int userId, java.sql.Connection conn) throws java.sql.SQLException {
        String sql = "SELECT COUNT(*) FROM Loan l JOIN checksession cs ON l.receiptnumber = cs.receiptnumber WHERE cs.userid = ? AND l.returndate IS NULL";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public java.util.Map<Item, String> checkoutCart(LibraryUser user, java.util.List<Item> cartItems) {
        java.util.Map<Item, String> checkoutResults = new java.util.LinkedHashMap<>();
        String receiptNumber = "REC-" + System.currentTimeMillis();
        
        String insertSession = "INSERT INTO checksession (receiptnumber, loandate, userid) VALUES (?, CURRENT_DATE, ?)";
        String insertLoan = "INSERT INTO Loan (loanid, receiptnumber, barcode, duedate) VALUES ((SELECT COALESCE(MAX(loanid), 0) + 1 FROM Loan), ?, ?, CURRENT_DATE + CAST(? AS INTERVAL))";
        String updateCopy = "UPDATE Copy SET status = 'LOANED' WHERE barcode = ?";
        String deleteReservation = "DELETE FROM Reservation WHERE userID = ? AND itemID = ?";

        int maxLoans = 5;
        String cat = user.getUserCategory() != null ? user.getUserCategory() : "";
        if ("Forskare".equalsIgnoreCase(cat)) {
            maxLoans = 20;
        } else if ("Student".equalsIgnoreCase(cat) || "Övriga universitetsanställda".equalsIgnoreCase(cat) || "Övriga anställda".equalsIgnoreCase(cat)) {
            maxLoans = 10;
        }

        try (java.sql.Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false); 
            
            try (java.sql.PreparedStatement pSession = conn.prepareStatement(insertSession);
                 java.sql.PreparedStatement pLoan = conn.prepareStatement(insertLoan);
                 java.sql.PreparedStatement pCopy = conn.prepareStatement(updateCopy);
                 java.sql.PreparedStatement pDelRes = conn.prepareStatement(deleteReservation)) {

                int currentLoans = getActiveLoanCount(user.getUserID(), conn);

                pSession.setString(1, receiptNumber);
                pSession.setInt(2, user.getUserID());
                pSession.executeUpdate();

                int successfulLoans = 0;

                for (Item item : cartItems) {
                    if (currentLoans >= maxLoans) {
                        checkoutResults.put(item, "LIMIT_REACHED");
                        continue;
                    }

                    String category = item.getItemCategory() != null ? item.getItemCategory().trim() : "";
                    if (item instanceof Magazine || "Referenslitteratur".equalsIgnoreCase(category)) {
                        checkoutResults.put(item, "ERROR");
                        continue;
                    }

                    String barcode = getAvailableBarcode(item.getItemID());
                    if (barcode == null) {
                        checkoutResults.put(item, "UNAVAILABLE");
                        continue; 
                    }

                    String interval = "1 month";
                    java.time.LocalDate dueDate = java.time.LocalDate.now().plusMonths(1);

                    if ("Kurslitteratur".equalsIgnoreCase(category)) {
                        interval = "14 days";
                        dueDate = java.time.LocalDate.now().plusDays(14);
                    } else if (item instanceof DVD || "Film".equalsIgnoreCase(category)) {
                        interval = "7 days";
                        dueDate = java.time.LocalDate.now().plusDays(7);
                    }

                    try {
                        pLoan.setString(1, receiptNumber);
                        pLoan.setString(2, barcode); 
                        pLoan.setString(3, interval); 
                        pLoan.executeUpdate();
                        
                        pCopy.setString(1, barcode); 
                        pCopy.executeUpdate();
                        
                        pDelRes.setInt(1, user.getUserID());
                        pDelRes.setInt(2, item.getItemID());
                        pDelRes.executeUpdate();

                        checkoutResults.put(item, "SUCCESS|" + receiptNumber + "|" + barcode + "|" + dueDate.toString());
                        successfulLoans++;
                        currentLoans++; 

                    } catch (java.sql.SQLException e) {
                        checkoutResults.put(item, "ERROR");
                    }
                }

                if (successfulLoans > 0) {
                    conn.commit();
                } else {
                    conn.rollback(); 
                }
                
                return checkoutResults;

            } catch (java.sql.SQLException e) {
                conn.rollback();
                System.err.println("Fel vid kassa-utcheckning: " + e.getMessage());
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Databasanslutningsfel: " + e.getMessage());
        }
        
        for (Item item : cartItems) {
            checkoutResults.putIfAbsent(item, "ERROR");
        }
        return checkoutResults;
    }
}