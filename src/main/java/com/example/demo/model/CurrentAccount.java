package com.example.demo.model;
import java.time.LocalDate;
public class CurrentAccount extends Account{
	private double overdraftLimit;
	private double minBalance;
	private double minBalanceFine;
	private double overdraftFine;
	private double balance = super.getBalance();
	private LocalDate lastMinBalanceDate;
    	private LocalDate lastOverdraftDate;
	CurrentAccount(Customer accountHolder, Branch branch, double salary){
		super(accountHolder, branch);
		overdraftLimit = 2 * salary;
		minBalance = 50000;
		overdraftFine = 1000;
		minBalanceFine = 500;
		lastMinBalanceDate = null;
		lastOverdraftDate = null;
	}
	public double getBalance(){
		return super.getBalance();
	}
	public boolean withdraw(double amount) {
        	if (amount > 0 && (balance - amount) >= -overdraftLimit) {
			super.withdraw(amount);
        		System.out.println("Withdrawn: " + amount);
			if (balance < minBalance && lastMinBalanceDate == null) {
                		lastMinBalanceDate = LocalDate.now();
            		}
        		if (balance < 0 && lastOverdraftDate == null) {
        			lastOverdraftDate = LocalDate.now();
        		}
			return true;
        	} 
		else {
         		System.out.println("Withdrawal denied. Exceeds overdraft limit or invalid amount.");
			return false;
      		}
	}
	public boolean deposit(double amount) {
        	if (amount > 0) {
			super.deposit(amount);
                	System.out.println("Deposited: " + amount);
        	}
		else {
                	System.out.println("Invalid deposit amount.");
			return false;
        	}
        	if (balance >= 0) {
                	lastOverdraftDate = null;
      		}
		if (balance >= minBalance) {
            		lastMinBalanceDate = null;
        	}
		return true;
      	}
	void imposeFine() {
        	LocalDate currentDate = LocalDate.now();
        	if (lastMinBalanceDate != null) {
            		while (lastMinBalanceDate.plusDays(30).isBefore(currentDate) || lastMinBalanceDate.plusDays(30).isEqual(currentDate)) {
                		if (balance < minBalance) {
				super.withdraw(minBalanceFine);
                    		System.out.println("Applied minimum balance fine of " + minBalanceFine + " for one cycle.");
                		}
                		lastMinBalanceDate = lastMinBalanceDate.plusDays(30);
            		}
        	}
        	if (lastOverdraftDate != null) {
            		while (lastOverdraftDate.plusDays(30).isBefore(currentDate) || lastOverdraftDate.plusDays(30).isEqual(currentDate)) {
				super.withdraw(overdraftFine);
                		System.out.println("Applied overdraft fine of " + overdraftFine + " for one cycle.");
                		lastOverdraftDate = lastOverdraftDate.plusDays(30);
            		}
        	}
    	}
	void AccountDetails(){
		super.getAccountDetails();
		System.out.println("Overdraft Limit: " + overdraftLimit);
		System.out.println("Minimum Balance: " + minBalance);
		System.out.println("Minimum Balance Fine: " + minBalanceFine);
		System.out.println("Overdraft Fine: " + overdraftFine);
	}
}

