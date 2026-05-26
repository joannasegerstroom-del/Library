package com.library;

import java.time.LocalDate;

public class Loan {
    private int loanID;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Copy copy;

    public Loan(int loanID, LocalDate dueDate, Copy copy) {
        this.loanID = loanID;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.copy = copy;
    }

    public void registerReturn() {
        this.returnDate = LocalDate.now();
        this.copy.updateStatus("AVAILABLE");
    }

    public boolean isOverdue() {
        if (returnDate == null && LocalDate.now().isAfter(dueDate)) {
            return true;
        }
        return false;
    }

    public int getLoanID() {return loanID;}
    public LocalDate getDueDate() {return dueDate;}
    public LocalDate getReturnDate() {return returnDate;}
    public Copy getCopy() {return copy;}
}