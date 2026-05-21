package com.library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public LibraryUser validateLogin(String loginIdentifier) {
        String sql = "SELECT * FROM LibraryUser WHERE email = ? OR name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, loginIdentifier);
            pstmt.setString(2, loginIdentifier);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new LibraryUser(
                        rs.getInt("userID"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("userCategory")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Fel vid inloggning: " + e.getMessage());
        }
        return null;
    }

    public List<String> getActiveLoans(int userId) {
        List<String> loans = new ArrayList<>();
        String sql = "SELECT i.title, l.duedate, l.barcode " +
                     "FROM checksession cs " +
                     "JOIN Loan l ON cs.receiptnumber = l.receiptnumber " +
                     "JOIN Copy c ON l.barcode = c.barcode " +
                     "JOIN Item i ON c.itemID = i.itemID " +
                     "WHERE cs.userid = ? AND l.returndate IS NULL";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String title = rs.getString("title");
                    java.sql.Date dueDate = rs.getDate("duedate");
                    String barcode = rs.getString("barcode");
                    
                    boolean isOverdue = dueDate.before(new java.util.Date());
                    String status = isOverdue ? "[FÖRSENAD]" : "[AKTIV]";
                    
                    loans.add(title + " | " + status + " t.o.m. " + dueDate + " #" + barcode);
                }
            }
        } catch (SQLException e) {
            System.err.println("Fel vid hämtning av aktiva lån: " + e.getMessage());
        }
        return loans;
    }

    public List<String> getPastLoans(int userId) {
        List<String> history = new ArrayList<>();
        String sql = "SELECT cs.receiptnumber, cs.loandate, i.title " +
                     "FROM checksession cs " +
                     "JOIN Loan l ON cs.receiptnumber = l.receiptnumber " +
                     "JOIN Copy c ON l.barcode = c.barcode " +
                     "JOIN Item i ON c.itemID = i.itemID " +
                     "WHERE cs.userid = ? " +
                     "ORDER BY cs.loandate DESC, cs.receiptnumber";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                String currentReceipt = "";
                StringBuilder receiptString = new StringBuilder();

                while (rs.next()) {
                    String receiptNum = rs.getString("receiptnumber");
                    java.sql.Date loanDate = rs.getDate("loandate");
                    String title = rs.getString("title");

                    if (!receiptNum.equals(currentReceipt)) {
                        if (!currentReceipt.isEmpty()) {
                            history.add(receiptString.toString());
                        }
                        currentReceipt = receiptNum;
                        receiptString = new StringBuilder("Kvitto: " + receiptNum + " (" + loanDate + ")\n   - " + title);
                    } else {
                        receiptString.append("\n   - ").append(title);
                    }
                }
                if (!receiptString.toString().isEmpty()) {
                    history.add(receiptString.toString());
                }
            }
        } catch (SQLException e) {
            System.err.println("Fel vid hämtning av historik: " + e.getMessage());
        }
        return history;
    }

    public boolean returnLoan(String barcode) {
        String updateCopy = "UPDATE Copy SET status = 'AVAILABLE' WHERE barcode = ?";
        String updateLoan = "UPDATE Loan SET returndate = CURRENT_DATE WHERE barcode = ? AND returndate IS NULL";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement pCopy = conn.prepareStatement(updateCopy);
                 PreparedStatement pLoan = conn.prepareStatement(updateLoan)) {
                
                pCopy.setString(1, barcode);
                pCopy.executeUpdate();
                
                pLoan.setString(1, barcode);
                pLoan.executeUpdate();
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Fel vid uppdatering av databas: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Databasanslutningsfel: " + e.getMessage());
            return false;
        }
    }

    public boolean createReservation(int userId, int itemId) {
        String sql = "INSERT INTO Reservation (reservationID, userID, itemID, reservationDate) " +
                     "VALUES ((SELECT COALESCE(MAX(reservationID), 0) + 1 FROM Reservation), ?, ?, CURRENT_DATE)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, itemId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Fel vid skapande av reservation: " + e.getMessage());
            return false;
        }
    }

    public List<String> getReservations(int userId) {
        List<String> reservations = new ArrayList<>();
        String sql = "SELECT i.title, r.reservationDate, r.itemID " +
                     "FROM Reservation r " +
                     "JOIN Item i ON r.itemID = i.itemID " +
                     "WHERE r.userID = ? " +
                     "ORDER BY r.reservationDate ASC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String title = rs.getString("title");
                    java.sql.Date resDate = rs.getDate("reservationDate");
                    int itemId = rs.getInt("itemID");
                    
                    reservations.add(title + " | [RESERVERAD] Köad sedan " + resDate + " #" + itemId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Fel vid hämtning av reservationer: " + e.getMessage());
        }
        return reservations;
    }

    public List<String> getNotifications(int userId) {
        List<String> notifications = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection()) {
            
            String sqlTomorrow = "SELECT i.title, l.duedate FROM checksession cs " +
                                 "JOIN Loan l ON cs.receiptnumber = l.receiptnumber " +
                                 "JOIN Copy c ON l.barcode = c.barcode " +
                                 "JOIN Item i ON c.itemID = i.itemID " +
                                 "WHERE cs.userid = ? AND c.status = 'LOANED' AND l.duedate = CURRENT_DATE + INTERVAL '1 day'";
            try (PreparedStatement p1 = conn.prepareStatement(sqlTomorrow)) {
                p1.setInt(1, userId);
                ResultSet rs = p1.executeQuery();
                while(rs.next()) {
                    notifications.add("⚠️ PÅMINNELSE: '" + rs.getString("title") + "' ska lämnas tillbaka imorgon (" + rs.getDate("duedate") + ").");
                }
            }

            String sqlLate = "SELECT i.title, l.duedate FROM checksession cs " +
                             "JOIN Loan l ON cs.receiptnumber = l.receiptnumber " +
                             "JOIN Copy c ON l.barcode = c.barcode " +
                             "JOIN Item i ON c.itemID = i.itemID " +
                             "WHERE cs.userid = ? AND c.status = 'LOANED' AND l.duedate < CURRENT_DATE";
            try (PreparedStatement p2 = conn.prepareStatement(sqlLate)) {
                p2.setInt(1, userId);
                ResultSet rs = p2.executeQuery();
                while(rs.next()) {
                    notifications.add("🚨 FÖRSENAD: '" + rs.getString("title") + "' skulle ha lämnats tillbaka " + rs.getDate("duedate") + "!");
                }
            }

            String sqlRes = "SELECT i.title FROM Reservation r " +
                            "JOIN Item i ON r.itemID = i.itemID " +
                            "WHERE r.userID = ? AND EXISTS (" +
                            "    SELECT 1 FROM Copy c WHERE c.itemID = r.itemID AND c.status = 'AVAILABLE'" +
                            ")";
            try (PreparedStatement p3 = conn.prepareStatement(sqlRes)) {
                p3.setInt(1, userId);
                ResultSet rs = p3.executeQuery();
                while(rs.next()) {
                    notifications.add("✅ TILLGÄNGLIG: Din reserverade bok '" + rs.getString("title") + "' finns nu inne att låna!");
                }
            }

        } catch (SQLException e) {
            System.err.println("Fel vid hämtning av notiser: " + e.getMessage());
        }
        
        return notifications;
    }

}