package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.model.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class IndexController {
    private static Bank bank;

    @GetMapping("/")
    public String redirectToStaticIndex(Model model, HttpSession session) {
        if (bank == null) {
            bank = new Bank("National Bank", "B001");
            Branch branch = new Branch("BR001", "Main Branch", "123 Main St, City", "+91 9876543210");
            bank.addBranch(branch);

            Manager manager = new Manager("Vijay Mallya", bank, branch, 75000, 1234567890L, "john.doe@bank.com",
                    new Date());
            branch.addEmployee(manager);

            Employee employee1 = new Employee("Harshad Mehta", bank, branch, "Employee", "Branch", 45000, 9876543210L,
                    "jane.smith@bank.com", new Date());
            branch.addEmployee(employee1);

            Customer customer1 = new Customer("Mahesh Babu", "Film Nagar, Jubilee Hills", "+91 9876543210");
            Customer customer2 = new Customer("Chandrababu Naidu", "AP CM Office", "+91 1234567890");
            branch.addCustomer(customer1);
            branch.addCustomer(customer2);

            SavingsAccount savingsAccount1 = new SavingsAccount(customer1, branch, 1000.0, 5000.0, 5);
            SavingsAccount savingsAccount2 = new SavingsAccount(customer2, branch, 1500.0, 7000.0, 5);
            customer1.addAccount(savingsAccount1);
            customer2.addAccount(savingsAccount2);

            customer1.addDebitCard(savingsAccount1);
            customer2.addDebitCard(savingsAccount2);
            customer1.addCreditCard(5000.0, 12.5);
            customer2.addCreditCard(7000.0, 14.0);

        }

        if (bank != null && !bank.getBranches().isEmpty()) {
            Branch firstBranch = bank.getBranches().get(0);

            if (!firstBranch.getCustomers().isEmpty()) {
                Customer firstCustomer = firstBranch.getCustomers().get(0);
                Customer secondCustomer = firstBranch.getCustomers().get(1);

                session.setAttribute("cid_one", firstCustomer.getCIF());
                session.setAttribute("cid_two", secondCustomer.getCIF());

                if (!firstCustomer.getAccountDetails().isEmpty()) {
                    Account firstCustomerAccount = firstCustomer.getAccountDetails().get(0);
                    session.setAttribute("acc_one", firstCustomerAccount.getAccountNumber());
                } else {
                    session.setAttribute("acc_one", "No account found for first customer");
                }

                if (!secondCustomer.getAccountDetails().isEmpty()) {
                    Account secondCustomerAccount = secondCustomer.getAccountDetails().get(0);
                    session.setAttribute("acc_two", secondCustomerAccount.getAccountNumber());
                } else {
                    session.setAttribute("acc_two", "No account found for second customer");
                }
            }

            List<Manager> managers = new ArrayList<>();
            List<Employee> employees = new ArrayList<>();

            for (Employee emp : firstBranch.getEmployees()) {
                if (emp instanceof Manager) {
                    managers.add((Manager) emp);
                } else {
                    employees.add(emp);
                }
            }

            if (!managers.isEmpty()) {
                session.setAttribute("mid", managers.get(0).getEmployeeId());
            } else {
                session.setAttribute("mid", "No managers found in the first branch");
            }

            if (!employees.isEmpty()) {
                session.setAttribute("eid", employees.get(0).getEmployeeId());
            } else {
                session.setAttribute("eid", "No employees found in the first branch");
            }
        }

        return "forward:/index.html";
    }

    @GetMapping("/customer")
    public String redirectToCustomerIndex(Model model, HttpSession session) {
        model.addAttribute("cid_one", session.getAttribute("cid_one"));
        return "customer/index";
    }

    /*
     * Customer application and routing starts from here
     */

    @GetMapping("/customer/profile")
    public String redirectToCustomerProfile(Model model, HttpSession session) {
        String customerId = (String) session.getAttribute("cid_one");

        if (customerId != null && bank != null) {
            for (Branch branch : bank.getBranches()) {
                for (Customer customer : branch.getCustomers()) {
                    if (customer.getCIF().equals(customerId)) {
                        model.addAttribute("cname", customer.getName());
                        model.addAttribute("cid", customer.getCIF());
                        model.addAttribute("caddress", customer.getAddress());
                        model.addAttribute("cphone", customer.getPhoneNo());
                        break;
                    }
                }
            }
        } else {
            model.addAttribute("error", "Customer not found or session is invalid");
        }
        return "customer/profile";
    }

    @GetMapping("/customer/accounts_list")
    public String getCustomerAccounts(Model model, HttpSession session) {
        String customerId = (String) session.getAttribute("cid_one");
        List<Account> accounts = new ArrayList<>();
        List<String> accountTypes = new ArrayList<>();

        if (customerId != null && bank != null) {
            boolean customerFound = false;

            for (Branch branch : bank.getBranches()) {
                for (Customer customer : branch.getCustomers()) {
                    if (customer.getCIF().equals(customerId)) {
                        accounts = customer.getAccountDetails();
                        customerFound = true;

                        for (Account account : accounts) {
                            if (account instanceof SavingsAccount) {
                                accountTypes.add("Savings Account");
                            } else {
                                accountTypes.add("Current Account");
                            }
                        }
                        break;
                    }
                }
                if (customerFound)
                    break;
            }

            if (customerFound) {
                model.addAttribute("cid", customerId);
                model.addAttribute("accounts", accounts);
                model.addAttribute("accountTypes", accountTypes);
            } else {
                model.addAttribute("error", "Customer with CIF " + customerId + " not found");
            }
        } else {
            model.addAttribute("error", "Session is invalid or customer ID is not set");
        }

        return "customer/accounts_list";
    }

    @GetMapping("/customer/cards_list")
    public String getCustomerCards(Model model, HttpSession session) {
        String customerId = (String) session.getAttribute("cid_one");
        List<Card> cards = new ArrayList<>();
        List<String> cardTypes = new ArrayList<>();

        if (customerId != null && bank != null) {
            boolean customerFound = false;

            for (Branch branch : bank.getBranches()) {
                for (Customer customer : branch.getCustomers()) {
                    if (customer.getCIF().equals(customerId)) {
                        cards = customer.getCardDetails();

                        for (Card card : customer.getCardDetails()) {
                            if (card instanceof DebitCard) {
                                cardTypes.add("Debit Card");
                            } else if (card instanceof CreditCard) {
                                cardTypes.add("Credit Card");
                            }
                        }

                        customerFound = true;
                        break;
                    }
                }
                if (customerFound) {
                    break;
                }
            }

            if (customerFound) {
                model.addAttribute("cid", customerId);
                model.addAttribute("cards", cards);
                model.addAttribute("cardTypes", cardTypes);
            } else {
                model.addAttribute("error", "Customer with CIF " + customerId + " not found");
            }
        } else {
            model.addAttribute("error", "Session is invalid or customer ID is not set");
        }

        return "customer/cards_list";
    }
    /*
     * Employee application and routing starts from here
     */

    @GetMapping("/employee")
    public String redirectToEmployeeIndex(Model model, HttpSession session) {
        return "employee/index";
    }

    /*
     * Manager application and routing starts from here
     */

    @GetMapping("/manager")
    public String redirectToManagerIndex(Model model, HttpSession session) {
        return "manager/index";
    }
}