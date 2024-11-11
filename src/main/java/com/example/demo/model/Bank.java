package com.example.demo.model;
import java.util.ArrayList;

public class Bank {
    private String name;
    private final String bank_id;
    private ArrayList<Branch> branches;
     // Taking Branch object as an Array List

    public Bank(String name, String bank_id) {
        this.name = name;
        this.bank_id = bank_id;
        this.branches = new ArrayList<>(); // Initialise an Empty List
    }

    public void addBranch(Branch branch) {
        branches.add(branch);
    }

    public void removeBranch(Branch branch) {
        branches.remove(branch);
    }

    public void getBranchDetails() {
        for (Branch branch : branches) {
            System.out.println("Branch Name: " + branch.getBranchName()); // Branch class has getBranchName method
        }
    }

    public void getBankDetails() {
        System.out.println("Bank Name: " + name);
        System.out.println("Bank ID: " + bank_id);
        System.out.println("Branches linked to this bank:");
        for (Branch branch : branches) {
            System.out.println();
            branch.generateBranchReport();
            System.out.println();
        }
    }
}
