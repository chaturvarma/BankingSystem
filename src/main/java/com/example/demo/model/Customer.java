package com.example.demo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String CIF;
    private static long nextID = 1;
    private String name;
    private String address;
    private String phone_no;
    private List<Account> accounts;
    private List<Card> cards;
    private List<LoanApplication> loanApplications;
    private List<LoanAccount> loanAccounts;

    public Customer(String name, String address, String phone_no) {
        CIF = "CIF" + String.format("%09d", nextID++);
        this.name = name;
        this.address = address;
        this.phone_no = phone_no;
        this.accounts = new ArrayList<>();
        this.cards = new ArrayList<>();
        this.loanApplications = new ArrayList<>();
        this.loanAccounts = new ArrayList<>();
    }

    public void addAccount(Account account) {
        if (account != null) {
            accounts.add(account);
            System.out.println("Account added: " + account.getAccountNumber());
        } else {
            System.out.println("Invalid account!");
        }
    }

    public void closeAccount(Account account) {
        if (accounts.remove(account)) {
            System.out.println("Account closed: " + account.getAccountNumber());
        } else {
            System.out.println("Account not found!");
        }
    }

    public List<Account> getAccountDetails() {
        return accounts;
    }

    public void addDebitCard(Account account) {
        if (account != null) {
            cards.add(new DebitCard(name, account));
            System.out.println("Debit Card added for account: " + account.getAccountNumber());
        } else {
            System.out.println("Invalid debit card account!");
        }
    }

    public void addCreditCard(double limit, double interest) {
        cards.add(new CreditCard(name, limit, interest));
        System.out.println("Credit Card added.");
    }

    public void removeCard(Card card) {
        if (cards.remove(card)) {
            System.out.println("Card removed: " + card.getCardNumber());
        } else {
            System.out.println("Card not found!");
        }
    }

    public List<Card> getCardDetails() {
        return cards;
    }

    public void applyLoan(double amount, String type, LocalDate appDate) {
        LoanApplication loanApp = new LoanApplication(this, amount, type, appDate);
        loanApplications.add(loanApp);
        System.out.println("Loan application submitted: " + loanApp.getApplicationId());
    }

    public void applyLoan(LoanApplication loanApp) {
        loanApplications.add(loanApp);
        System.out.println("Loan application submitted: " + loanApp.getApplicationId());
    }

    public String getLoanApplicationStatus(LoanApplication loanApplication) {
        if (loanApplications.contains(loanApplication)) {
            return loanApplication.getLoanStatus();
        } else {
            return "Loan application not found!";
        }
    }

    public List<LoanAccount> getLoanAccounts() {
        return loanAccounts;
    }

    public String getCIF() {
        return CIF;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNo() {
        return phone_no;
    }
}
