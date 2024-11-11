package com.example.demo.model;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Card {
    private String cardNumber;
    private String name;
    private int cvv;
    private Date expiryDate;
    private String status;

    public Card(String name) {
        this.cardNumber = generateCardNumber();
        this.name = name;
        this.cvv = generateCVV();
        this.expiryDate = generateExpiryDate();
        this.status = "Inactive";
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumBuilder = new StringBuilder();

        // Generate a 16-digit card number
        for (int i = 0; i < 16; i++) {
            cardNumBuilder.append(random.nextInt(10)); // Random digit from 0 to 9
        }
        return cardNumBuilder.toString();
    }

    private int generateCVV() {
        Random random = new Random();
        return 100 + random.nextInt(900); // Random 3-digit number (100 to 999)
    }

    private Date generateExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 4); // Set expiry date to 4 years from now
        return calendar.getTime();
    }

    public void blockCard() {
        status = "Blocked";
        System.out.println("Card has been blocked.");
    }

    public void unblockCard() {
        status = "Active";
        System.out.println("Card has been unblocked.");
    }

    public void activateCard() {
        status = "Active";
        System.out.println("Card has been activated.");
    }

    public String getCardDetails() {
        return "Card Number: " + cardNumber +
               "\nName: " + name +
               "\nCVV: " + cvv +
               "\nExpiry Date: " + expiryDate +
               "\nStatus: " + status;
    }

    public void withdraw(double amount) {
        if (status.equals("Active")) {
            System.out.println("Amount " + amount + " withdrawn.");
        } else {
            System.out.println("Card is blocked. Cannot withdraw.");
        }
    }

    public void deposit(double amount) {
        if (status.equals("Active")) {
            System.out.println("Amount " + amount + " deposited.");
        } else {
            System.out.println("Card is blocked. Cannot deposit.");
        }
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getName() {
        return name;
    }

    public int getCvv() {
        return cvv;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public String getStatus() {
        return status;
    }
}
