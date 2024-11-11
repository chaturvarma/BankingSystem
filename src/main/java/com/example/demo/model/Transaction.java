package com.example.demo.model;
import java.time.LocalTime;
import java.time.LocalDate;

public class Transaction {
    private static int transactionCounter = 0;
    
    private String transactionId;
    private Account sourceAccount;
    private Account destinationAccount;
    private String transactionType;
    private double amount;
    private LocalDate transactionDate;
    private String transactionStatus;
    private double transactionFee;
    private String failureReason;
    private LocalTime transactionTime;


    public Transaction(Account sourceAccount, Account destinationAccount, String transactionType, double amount) {
        this.transactionId = generateTransactionId();
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = LocalDate.now();
        this.transactionTime = LocalTime.now();
        this.transactionStatus = "Pending";
    }

    private String generateTransactionId() {
        transactionCounter++;
        return String.format("%09d", transactionCounter);
    }

    public void getTransactionDetails() {
        System.out.println("Transaction ID: " + transactionId);
        System.out.println("Source Account: " + (sourceAccount != null ? sourceAccount.getAccountNumber() : "N/A"));
        System.out.println("Destination Account: " + (destinationAccount != null ? destinationAccount.getAccountNumber() : "N/A"));
        System.out.println("Type: " + transactionType);
        System.out.println("Amount: " + amount);
        System.out.println("Date: " + transactionDate);
        System.out.println("Status: " + transactionStatus);
        System.out.println("Fee: " + transactionFee);
        System.out.println("Failure Reason: " + (failureReason != null ? failureReason : "None"));
    }

    public double calculateTransactionFees() {
        switch (transactionType) {
            case "Transfer":
                transactionFee = amount * 0.005;
                break;
            case "Deposit":
                transactionFee = 0.0;
                break;
            case "Withdraw":
                transactionFee = amount * 0.01;
                break;
            default:
                transactionFee = 0.0;
                break;
        }
        return transactionFee;
    }

    public boolean processTransaction() {
        transactionFee = calculateTransactionFees();

        switch (transactionType) {
            case "Deposit":
                if (destinationAccount != null) {
                    destinationAccount.deposit(amount);
                    transactionStatus = "Completed";
                    destinationAccount.addTransaction(this);
                    
                } else {
                    transactionStatus = "Failed";
                    failureReason = "Destination Account Not Specified";
                }
                break;

            case "Withdraw":
                if (sourceAccount != null) {
                    sourceAccount.withdraw(amount + transactionFee);
                    transactionStatus = "Completed";
                    sourceAccount.addTransaction(this);
                } else {
                    transactionStatus = "Failed";
                    failureReason = "Source Account Not Specified";
                }
                break;

            case "Transfer":
                if (sourceAccount != null && destinationAccount != null) {
                    boolean withdrawSuccess = sourceAccount.withdraw(amount + transactionFee);
                    if (withdrawSuccess) {
                        destinationAccount.deposit(amount);
                        transactionStatus = "Completed";
                        sourceAccount.addTransaction(this);
                        destinationAccount.addTransaction(this);
                    } else {
                        transactionStatus = "Failed";
                        failureReason = "Insufficient Funds in Source Account";
                    }
                } else {
                    transactionStatus = "Failed";
                    failureReason = sourceAccount == null ? "Source Account Not Specified" :
                                   destinationAccount == null ? "Destination Account Not Specified" : "Unknown Error";
                }
                break;

            default:
                transactionStatus = "Failed";
                failureReason = "Invalid Transaction Type";
                break;
        }

        return "Completed".equals(transactionStatus);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public Account getSourceAccount() {
        return sourceAccount;
    }

    public Account getDestinationAccount() {
        return destinationAccount;
    }
}
