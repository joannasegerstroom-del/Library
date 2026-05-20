package com.library;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CheckSession {
    private String receiptNumber;
    private LocalDate loanDate;
    private LibraryUser user;
    private List<Loan> loans;

    public CheckSession(String receiptNumber, LibraryUser user) {
        this.receiptNumber = receiptNumber;
        this.loanDate = LocalDate.now();
        this.user = user;
        this.loans = new ArrayList<>();
    }

    public void addLoan(Loan loan) {
        loans.add(loan);
        loan.getCopy().updateStatus("LOANED");
    }

    public void printReceipt() {
        System.out.println("=== KVITTO: " + receiptNumber + " ===");
        System.out.println("Datum: " + loanDate);
        System.out.println("Låntagare: " + user.getName() + " (" + user.getUserCategory() + ")");
        System.out.println("Lånade objekt:");
        for (Loan l : loans) {
            System.out.println("- " + l.getCopy().getItem().getTitle() + " (Återlämnas senast: " + l.getDueDate() + ")");
        }
        System.out.println("=================================");
    }

    public String getReceiptNumber() {return receiptNumber;}
    public LocalDate getLoanDate() {return loanDate;}
    public LibraryUser getUser() {return user;}
    public List<Loan> getLoans() {return loans;}
}