package com.example.demo.model;
import java.util.ArrayList;
## Account
public abstract class Account {
    private static int accountCounter = 100000;
    private Customer accountHolder;
    private String accountNumber;
    private double balance;
    private Branch branch;
    private ArrayList<FixedDeposit> fixedDeposits;
    private ArrayList<RecurringDeposit> recurringDeposits;
    private ArrayList<LoanAccount> loanAccounts;
    private ArrayList<Transaction> transactions;


    public Account(Customer accountHolder, Branch branch) {
        this.accountHolder = accountHolder;
        this.accountNumber = generateAccountNumber();
        this.balance = 0.0;
        this.branch = branch;
        this.fixedDeposits = new ArrayList<>();
        this.recurringDeposits = new ArrayList<>();
        this.loanAccounts = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }


    private synchronized String generateAccountNumber() {
        return String.valueOf(accountCounter++);
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountDetails() {
        return "Account Number: " + accountNumber +
               "\nAccount Holder: " + accountHolder.getName() +
               "\nBalance: " + balance +
               "\nBranch: " + branch.getBranchName();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Branch getBranch() {
        return branch;
    }

   abstract public boolean withdraw(double amount) {
        if (amount > 0) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public boolean deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            return true;
        }
        return false;
    }
    
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void closeFD(FixedDeposit FD) {
        if (fixedDeposits.remove(FD)) {
            System.out.println("Fixed Deposit closed.");
        }
    }

    public void closeRD(RecurringDeposit RD) {
        if (recurringDeposits.remove(RD)) {
            System.out.println("Recurring Deposit closed.");
        }
    }

    public void addFD(FixedDeposit FD) {
        fixedDeposits.add(FD);
        System.out.println("Fixed Deposit added.");
    }

    public void addRD(RecurringDeposit RD) {
        recurringDeposits.add(RD);
        System.out.println("Recurring Deposit added.");
    }

    public void addLoanAccount(LoanAccount LA) {
        loanAccounts.add(LA);
        System.out.println("Loan Account added.");
    }

    public void removeLoanAccount(LoanAccount LA) {
        loanAccounts.remove(LA);
        System.out.println("Loan Account removed.");
    }

    public ArrayList<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
}
