package com.example.demo.model;
import java.util.ArrayList;
import java.util.List;

public class Branch{
	private String branchID;
	private String branchName;
	private String address;
	private String contactNumber;
	//private Employee branchManager;
	private List<Employee> employees;
	private List<Customer> customers;
	private List<Account> accounts;
	private List<Transaction> transactions;
	private List<LoanApplication> loanApplications;
	private List<LoanAccount> loanAccounts;
	private List<FixedDeposit> fixedDeposits;
	private List<RecurringDeposit> recurringDeposits;
	
	public Branch(String branchID, String branchName, String address, String contactNumber){
		this.branchID = branchID;
		this.branchName = branchName;
		this.address = address;
		this.contactNumber = contactNumber;
		//this.branchManager = branchManager;
		employees = new ArrayList<>();
		customers = new ArrayList<>();
		accounts = new ArrayList<>();
		transactions = new ArrayList<>();
		loanApplications = new ArrayList<>();
		loanAccounts = new ArrayList<>();
		fixedDeposits = new ArrayList<>();
		recurringDeposits = new ArrayList<>();
	}
	String getBranchID(){
		return branchID;
	}
	String getBranchName(){
		return branchName;
	}
	String getAddress(){
		return address;
	}
	String getContactNumber(){
		return contactNumber;
    }
	List<Employee> getEmployees(){
		return employees;
	}
	List<Customer> getCustomers(){
		return customers;
	}
	List<Account> getAccounts(){
		return accounts;
	}
	List<Transaction> getTransactions(){
		return transactions;
	}
	List<LoanApplication> getLoanApplications(){
		return loanApplications;
	}
	List<LoanAccount> getLoanAccounts(){
		return loanAccounts;
	}
	List<FixedDeposit> getFixedDeposits(){
		return fixedDeposits;
	}
	List<RecurringDeposit> getRecurringDeposits(){
		return recurringDeposits;
	}
	void addAccount(Account account){
		accounts.add(account);
	}
	void removeAccount(Account account){
		if(accounts.contains(account)){
			accounts.remove(account);
		}
		else{
			System.out.println("Account does not exist.");
		}
	}
	void addEmployee(Employee employee){
		employees.add(employee);
	}
	void removeEmployee(Employee employee){
		if(employees.contains(employee)){
			employees.remove(employee);
		}
		else{
			System.out.println("Employee does not exist.");
		}
	}
	void addCustomer(Customer customer){
		customers.add(customer);
	}
	void addTransaction(Transaction transaction){
		transactions.add(transaction);
	}
	void addLoanApplication(LoanApplication loanApplication){
		loanApplications.add(loanApplication);
	}
	void removeLoanApplication(LoanApplication loanApplication){
		if(loanApplications.contains(loanApplication)){
			loanApplications.remove(loanApplication);
		}
		else{
			System.out.println("Loan Application does not exist.");
		}
	}
	void addLoanAccount(LoanAccount loanAccount){
		loanAccounts.add(loanAccount);
	}
	void removeLoanAccount(LoanAccount loanAccount){
		if(loanAccounts.contains(loanAccount)){
			loanAccounts.remove(loanAccount);
		}
		else{
			System.out.println("Loan Account does not exist.");
		}
	}
	void addFixedDeposit(FixedDeposit fixedDeposit){
		fixedDeposits.add(fixedDeposit);
	}
	void removeFixedDeposit(FixedDeposit fixedDeposit){
		if(fixedDeposits.contains(fixedDeposit)){
			fixedDeposits.remove(fixedDeposit);
		}
		else{
			System.out.println("Fixed Deposit does not exist.");
		}
	}
	void addRecurringDeposit(RecurringDeposit recurringDeposit){
		recurringDeposits.add(recurringDeposit);
	}
	void removeRecurringDeposit(RecurringDeposit recurringDeposit){
		if(recurringDeposits.contains(recurringDeposit)){
			recurringDeposits.remove(recurringDeposit);
		}
		else{
			System.out.println("Recurring Deposit does not exist.");
		}
	}
	void generateBranchReport(){
		System.out.println("Branch ID: " + branchID);
		System.out.println("Branch Name: " + branchName);
		System.out.println("Branch address: " + address);
		System.out.println("Branch Contact: " + contactNumber);
		System.out.println("Branch Manager:");
		System.out.println("Number of Employees: " + employees.size());
		System.out.println("Number of Customers: " + customers.size());
		System.out.println("Number of Accounts: " + accounts.size());
		System.out.println("Number of Transactions: " + transactions.size());
		System.out.println("Number of Loan Applications: " + loanApplications.size());
		System.out.println("Number of Loan Accounts: " + loanAccounts.size());
		System.out.println("Number of Fixed Deposits: " + fixedDeposits.size());
		System.out.println("Number of Recurring Deposits: " + recurringDeposits.size());
	}
}
