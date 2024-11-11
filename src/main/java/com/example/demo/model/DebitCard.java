package com.example.demo.model;
import java.util.ArrayList;

class DebitCard extends Card {
    private Account account;
    private ArrayList<Transaction> transactions;

    public DebitCard(String name, Account account) {
        super(name);
        this.account = account;
        this.transactions = new ArrayList<>();
    }

    @Override
    public void withdraw(double amount) {
        if (getStatus().equals("Active")) {
            if (amount > 0) {
                account.withdraw(amount);

                transactions.add(new Transaction(account, null, "Withdrawal",amount));
                account.addTransaction(new Transaction(account, null, "Withdrawal",amount));

                System.out.println("Withdrawn: " + amount);
            } else {
                System.out.println("Insufficient balance for withdrawal.");
            }
        } else {
            System.out.println("Card is blocked. Cannot withdraw.");
        }
    }

    @Override
    public void deposit(double amount) {
        if (getStatus().equals("Active")) {
            if (amount > 0) {
                account.deposit(amount);
                transactions.add(new Transaction(null, account, "Deposit", amount));
                account.addTransaction(new Transaction(null, account, "Deposit", amount));
                System.out.println("Deposited: " + amount);
            } else {
                System.out.println("Deposit amount must be positive.");
            }
        } else {
            System.out.println("Card is blocked. Cannot deposit.");
        }
    }

    public ArrayList<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    @Override
    public String getCardDetails() {
        return super.getCardDetails() + 
               "\nLinked Account Balance: " + account.getBalance();
    }
}
