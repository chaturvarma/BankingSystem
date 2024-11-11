package com.example.demo.model;
import java.time.LocalDate;

public class LoanAccount {

    private String loanId;
    private Customer customer;
    // The total loan amount that the loan has been alloted for
    private double loanAmount;
    private double interestRate;
    private int loanTerm;
    // The remaining amount that the customer has to pay to finish the loan
    private double balance;
    // The installment that is required to be paid every month for the loan
    // If this installment is not paid regularly, then lateFee is acquired
    private double installment;
    private LocalDate loanStart;
    private String status;
    private double lateFee;

    public LoanAccount(String loanId, Customer customer, double loanAmount, double interestRate, int loanTerm, LocalDate loanStart) {
        this.loanId = loanId;
        this.customer = customer;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.loanTerm = loanTerm;
        this.loanStart = loanStart;
        this.balance = loanAmount;
        this.status = "Active";
        this.lateFee = 500;
        this.installment = calculateInstallment();
    }

    public void getLoanDetails() {
        System.out.println("Loan ID: " + loanId);
        System.out.println("Customer: " + customer);
        System.out.println("Loan Amount: " + loanAmount);
        System.out.println("Interest Rate: " + interestRate);
        System.out.println("Loan Term (months): " + loanTerm);
        System.out.println("Balance: " + balance);
        System.out.println("Installment: " + installment);
        System.out.println("Loan Start Date: " + loanStart);
        System.out.println("Status: " + status);
        System.out.println("Late Fee: " + lateFee);
    }

    public String getLoanId() {
        return this.loanId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public int getLoanTerm() {
        return loanTerm;
    }

    public double getBalance() {
        return balance;
    }

    public double getInstallment() {
        return installment;
    }

    public LocalDate getLoanStart() {
        return loanStart;
    }

    public String getStatus() {
        return status;
    }

    public double getLateFee() {
        return lateFee;
    }

    public LocalDate getLoanEnd() {
        return loanStart.plusMonths(loanTerm);
    }

    private double calculateInstallment() {
        double monthlyInterestRate = interestRate / 12 / 100;
        double installmentAmount = this.balance * monthlyInterestRate / (1 - Math.pow(1 + monthlyInterestRate, -loanTerm));
        return installmentAmount;
    }

    public void closeLoan() {
        if (balance == 0) {
            status = "Closed";
            System.out.println("Loan has been closed");
        } else {
            System.out.println("Remaining amount " + balance + " still needs to be paid");
        }
    }

    public void updateLateFee(double newLateFee) {
        this.lateFee = newLateFee;
        System.out.println("Late fee updated to: " + lateFee);
    }

    public void updateInstallment() {
        this.installment = calculateInstallment();
        System.out.println("Installment recalculated: " + installment);
    }

    public void updateTerm(int newLoanTerm) {
        this.loanTerm = newLoanTerm;
        this.installment = calculateInstallment();
        System.out.println("Loan term updated to " + loanTerm + " months. New installment: " + installment);
    }

    public void updateInterest(double newInterestRate) {
        this.interestRate = newInterestRate;
        this.installment = calculateInstallment();
        System.out.println("Interest rate updated to " + interestRate + "%. New installment: " + installment);
    }

    public void makePayment(Double amount) {
        if (amount == null || amount < 0) {
            System.out.println("Enter a valid amount");
        } 
        else if (amount == 0) {
            System.out.println("Payment amount cannot be zero");
        }
        else if (amount > balance) {
            System.out.println("Invalid amount. You are paying more than required amount");
        }
        else {
            balance = balance - amount;
            System.out.println("Payment of " + amount + " made. Remaining balance to be paid: " + balance);
        }

        if(balance == 0) {
            System.out.println("Entire loanAmount has been paid");
            closeLoan();
        }
    }

    public void applyDelay() {
        balance += lateFee;
        System.out.println("Delay fee of " + lateFee + " applied. New amount to be paid: " + balance);
    }

    public void increaseLoan(double additionalAmount) {
        if ("Active".equals(status)) {
            this.loanAmount += additionalAmount;
            this.balance += additionalAmount;
            this.installment = calculateInstallment();
            System.out.println("Loan increased by " + additionalAmount + ". New balance: " + balance);
        } else {
            System.out.println("Cannot increase loan on closed or inactive accounts");
        }
    }
}
