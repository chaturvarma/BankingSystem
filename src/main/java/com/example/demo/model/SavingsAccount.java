package com.example.demo.model;
public class SavingsAccount extends Account {
    private static double interestRate = 5.0; // Default interest rate
    private double minBalance;
    private double transactionLimit;
    private int maxNoTransactions;
    private double charge;
    private boolean isViolating;
    private int currentTransactions;

    public SavingsAccount(Customer accountHolder, Branch branch, double minBalance, double transactionLimit, int maxNoTransactions) {
        super(accountHolder, branch);
        this.minBalance = minBalance;
        this.transactionLimit = transactionLimit;
        this.maxNoTransactions = maxNoTransactions;
        this.charge = 0.0;
        this.isViolating = false;
        this.currentTransactions = 0;
    }

    public double calculateInterest() {
        // Assuming interest is calculated on the current getBalance()
        return getBalance() * (interestRate / 100);
    }

    public boolean withdraw(double amount) {
        boolean success = false; // Track if withdrawal is successful
        System.out.println("You have requested to withdraw: " + amount);
        if (getBalance() - amount >= minBalance) {
            super.withdraw(amount);
            success = true;
            System.out.println("Withdrawal successful. New getBalance(): " + getBalance());
        } else {
            System.out.println("Insufficient getBalance() to meet minimum getBalance() requirements.");
        }
        return success;
    }

    public boolean deposit(double amount) {
        boolean success = false; // Track if deposit is successful
        System.out.println("You have deposited: " + amount);
        if (amount > 0) {
            super.deposit(amount);
            success = true;
            System.out.println("Deposit successful. New getBalance(): " + getBalance());
        } else {
            System.out.println("Deposit amount must be positive.");
        }
        return success;
    }

    public double getBalance() {
        return super.getBalance();
    }

    public void imposeFine() {
        charge = 50.0; // Example fine amount
        if (isViolating) {
            super.withdraw(charge);
            System.out.println("Fine imposed: " + charge);
        } else {
            System.out.println("No fine imposed as there was no violation.");
        }
    }
}
