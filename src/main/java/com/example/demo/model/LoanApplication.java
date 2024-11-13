package com.example.demo.model;
import java.time.LocalDate;

public class LoanApplication {
    private static int applicationCounter = 1;
    private String applicationId;
    private Customer customer;
    private double loanAmount;
    private String loanType;
    private String loanStatus;
    private boolean verified;
    private Employee verifiedBy;
    private LocalDate applicationDate;
    private LocalDate approvalDate;

    public LoanApplication(Customer customer, double loanAmount, String loanType, LocalDate applicationDate) {
        this.applicationId = generateApplicationId();
        this.customer = customer;
        this.loanAmount = loanAmount;
        this.loanType = loanType;
        this.applicationDate = applicationDate;
        this.loanStatus = "Pending";
        this.verified = false;
    }

    private String generateApplicationId() {
        String id = String.format("%09d", applicationCounter);
        applicationCounter++;
        return "LAP" + id;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public void getLoanDetails() {
        System.out.println("Loan Application ID: " + applicationId);
        System.out.println("Customer: " + customer.getName());
        System.out.println("Loan Amount: " + loanAmount);
        System.out.println("Loan Type: " + loanType);
        System.out.println("Application Date: " + applicationDate);
        System.out.println("Approval Date: " + (approvalDate != null ? approvalDate : "Not Approved"));
        System.out.println("Status: " + loanStatus);
        System.out.println("Verified: " + (verified ? "Yes" : "No"));
        System.out.println("Verified By: " + (verifiedBy != null ? verifiedBy.getEmployeeName() : "Not Verified"));
    }
    public String getCustomerName(){
            return customer.getName();
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getCustomerID() {
        return customer.getCIF();
    }

    public void updateLoanStatus(String status) {
        this.loanStatus = status;
        if ("Approved".equalsIgnoreCase(status)) {
            this.approvalDate = LocalDate.now();
        } else {
            this.approvalDate = null;
        }
    }

    public void updateVerification(boolean bool, Employee employee) {
        this.verified = bool;
        verifiedBy = employee;
    }

    
    public boolean isVerified() {
        return verified;
    }
    public String getApplicationId() {
      return applicationId;
    }

    public double getAmount() {
        return loanAmount;
    }

    public String getLoanType() {
        return loanType;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public Employee getVerifiedBy() {
        return verifiedBy;
    }

}
