package com.example.demo.model;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class RecurringDeposit {

    private String recurringId;
    // The amount that is available in the account
    private double balance;
    private double interestRate;
    // The frequency of deposits in days (Ex - 10, one deposit every 10 days)
    private int frequency;
    // The amount that being regularly deposited
    private double depositAmount;
    // The date after which the amount can be withdrawn
    private LocalDate maturity;
    private String status;
    // The amount after which the balance can be withdrawn
    private double maturityAmount;

    public RecurringDeposit(String recurringId, double interestRate, int frequency, double depositAmount, LocalDate maturity) {
        this.recurringId = recurringId;
        this.interestRate = interestRate;
        this.frequency = frequency;
        this.depositAmount = depositAmount;
        this.maturity = maturity;
        this.balance = 0.0;
        this.status = "Active";
        this.maturityAmount = calculateMaturityAmount(depositAmount, interestRate, frequency, maturity);
    }

    public void getRdDetails() {
        System.out.println("Recurring Deposit ID: " + recurringId);
        System.out.println("Balance: " + balance);
        System.out.println("Interest Rate: " + interestRate + "%");
        System.out.println("Frequency: " + frequency + " days");
        System.out.println("Deposit Amount: " + depositAmount);
        System.out.println("Maturity Date: " + maturity);
        System.out.println("Status: " + status);
        System.out.println("Maturity Amount: " + maturityAmount);
    }

    public String getRecurringId() {
        return recurringId;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public int getFrequency() {
        return frequency;
    }

    public double getDepositAmount() {
        return depositAmount;
    }

    public LocalDate getMaturity() {
        return maturity;
    }

    public String getStatus() {
        return status;
    }

    public double getMaturityAmount() {
        return maturityAmount;
    }

    public void updateInterest(double newInterestRate) {
        if(newInterestRate < 0 || newInterestRate > 100) {
            System.out.println("Enter a valid interest rate");
        } else if (newInterestRate == this.interestRate) {
            System.err.println("The new interest rate is same as the old interest rate");
        } else {
            this.interestRate = newInterestRate;
            System.out.println("Interest rate updated to: " + interestRate + "%");
        }
    }

    public void updateFrequency(int newFrequency) {
        if(newFrequency < 1 || newFrequency > 365) {
            System.out.println("Enter a valid frequency between 1 and 365 days");
        } else if (newFrequency == this.frequency) {
            System.out.println("Enter a new frequency. The new frequency is same as the old one");
        } else {
            this.frequency = newFrequency;
            System.out.println("Deposit frequency updated to every " + frequency + " days");
        }
    }

    public void updateDeposit(double newDepositAmount) {
        if(newDepositAmount < 1) {
            System.out.println("Enter a valid deposit amount");
        } else if (newDepositAmount == this.depositAmount) {
            System.out.println("The new deposit amount is same as the old one");
        } else {
            this.depositAmount = newDepositAmount;
            System.out.println("Deposit amount updated to: " + depositAmount);
        }
    }

    public void updateMaturityAmount(double maturityAmount) {
        if(maturityAmount < 1) {
            System.out.println("Enter a valid maturity amount");
        } else {
            this.maturityAmount = maturityAmount;
            System.out.println("Maturity amount has been updated to " + this.maturityAmount);
        }
    }

    public void updateMaturity(LocalDate newMaturity) {
        if (newMaturity.isBefore(LocalDate.now().plusDays(frequency))) {
            System.out.println("Maturity date cannot be earlier than " + LocalDate.now().plusDays(frequency));
            return;
        }
        
        if (!newMaturity.equals(this.maturity)) {
            this.maturity = newMaturity;
            System.out.println("Maturity date has been updated to " + this.maturity);
        } else {
            System.out.println("New maturity date is the same as the current date. No update needed.");
        }
    }

    public void makeDeposit() {
        if (status.equals("Active")) {
            balance += depositAmount;
            System.out.println("Deposit of " + depositAmount + " made. New balance: " + balance);
        } else {
            System.out.println("Cannot make deposit to an inactive account");
        }
    }

    public void closeAccount() {
        if (status.equals("Active")) {
            this.status = "Closed";
            System.out.println("Recurring Deposit account closed. Final balance: " + balance);
        } else {
            System.out.println("Account is already closed");
        }
    }

    public boolean canWithdraw() {
        LocalDate today = LocalDate.now();

        if (today.isAfter(this.maturity) || today.isEqual(this.maturity)) {
            if (balance >= maturityAmount) {
                return true;
            }
        }
        
        return false;
    }

    private double calculateMaturityAmount(double depositAmount, double interestRate, int frequency, LocalDate maturityDate) {
        LocalDate today = LocalDate.now();
        long totalDays = ChronoUnit.DAYS.between(today, maturityDate);
        int depositCycles = (int) (totalDays / frequency);

        double totalDeposit = 0.0;
        double totalInterest = 0.0;

        for (int i = 0; i < depositCycles; i++) {
            totalDeposit += depositAmount;
            double cycleInterest = depositAmount * Math.pow(1 + (interestRate / 100 / 12), (depositCycles - i)) - depositAmount;
            totalInterest += cycleInterest;
        }

        return totalDeposit + totalInterest;
    }
}
