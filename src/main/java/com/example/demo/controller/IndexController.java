package com.example.demo.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Account;
import com.example.demo.model.Bank;
import com.example.demo.model.Branch;
import com.example.demo.model.Card;
import com.example.demo.model.CreditCard;
import com.example.demo.model.Customer;
import com.example.demo.model.DebitCard;
import com.example.demo.model.Employee;
import com.example.demo.model.LoanAccount;
import com.example.demo.model.LoanApplication;
import com.example.demo.model.Manager;
import com.example.demo.model.SavingsAccount;
import com.example.demo.model.Transaction;

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
            Employee employee2 = new Employee("Satyam Raju", bank, branch, "Employee", "Branch", 25000, 9876543210L,
                    "matyas@bank.com", new Date());
            branch.addEmployee(employee2);

            Customer customer1 = new Customer("Mahesh Babu", "Film Nagar, Jubilee Hills", "+91 9876543210");
            Customer customer2 = new Customer("Chandrababu Naidu", "AP CM Office", "+91 1234567890");
            branch.addCustomer(customer1);
            branch.addCustomer(customer2);

            SavingsAccount savingsAccount1 = new SavingsAccount(customer1, branch, 1000.0, 5000.0, 5);
            SavingsAccount savingsAccount2 = new SavingsAccount(customer2, branch, 1500.0, 7000.0, 5);
            SavingsAccount savingsAccount3 = new SavingsAccount(customer1, branch, 1500.0, 7000.0, 5);
            customer1.addAccount(savingsAccount1);
            customer2.addAccount(savingsAccount2);
            customer1.addAccount(savingsAccount3);

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

    /*
     * Customer application and routing starts from here
     * This uses the route /customer fto perform all its functions
     */

    @GetMapping("/customer")
    public String redirectToCustomerIndex(Model model, HttpSession session) {
        return "customer/index";
    }

    @GetMapping("/customer/confirmation")
    public String customerConfirmation() {
        return "confirmation";
    }

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

    /*
     * Customers accounts section
     */

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

    @GetMapping("/customer/accounts_info")
    public String getAccountDetails(
            @RequestParam("accountId") String accountId,
            Model model,
            HttpSession session) {
        String customerId = (String) session.getAttribute("cid_one");

        if (customerId != null && bank != null) {
            outerLoop: for (Branch branch : bank.getBranches()) {
                for (Customer customer : branch.getCustomers()) {
                    if (customer.getCIF().equals(customerId)) {
                        for (Account account : customer.getAccountDetails()) {
                            if (account.getAccountNumber().equals(accountId)) {
                                model.addAttribute("accountId", account.getAccountNumber());
                                model.addAttribute("balance", account.getBalance());
                                model.addAttribute("branchName", branch.getBranchName());
                                model.addAttribute("cname", customer.getName());

                                if (account instanceof SavingsAccount) {
                                    model.addAttribute("accountType", "Savings Account");
                                } else {
                                    model.addAttribute("accountType", "Current Account");
                                }

                                break outerLoop;
                            }
                        }
                    }
                }
            }
        } else {
            model.addAttribute("error", "Customer not found or session is invalid");
        }
        return "customer/accounts_info";
    }

    @GetMapping("/customer/accounts_deposit")
    public String showDepositForm(@RequestParam("accountId") String accountId, Model model) {
        model.addAttribute("accountId", accountId);
        return "customer/accounts_deposit";
    }

    @PostMapping("/customer/accounts_deposit")
    public String depositToAccount(
            @RequestParam("accountId") String accountId,
            @RequestParam("amount") double amount,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        String customerId = (String) session.getAttribute("cid_one");

        if (customerId != null && bank != null) {
            outerLoop: for (Branch branch : bank.getBranches()) {
                for (Customer customer : branch.getCustomers()) {
                    if (customer.getCIF().equals(customerId)) {
                        Account account = null;

                        for (Account acc : customer.getAccountDetails()) {
                            if (acc.getAccountNumber().equals(accountId)) {
                                account = acc;
                                break;
                            }
                        }

                        if (account != null) {
                            double initialBalance = account.getBalance();
                            Transaction transaction = new Transaction(account, account, "Deposit", amount);

                            if (transaction.processTransaction()) {
                                double finalBalance = account.getBalance();

                                if (finalBalance == initialBalance + amount) {
                                    redirectAttributes.addFlashAttribute("title", "Amount Successfully Deposited");
                                    redirectAttributes.addFlashAttribute("message", "₹" + amount +
                                            " has been successfully deposited to account " + account.getAccountNumber()
                                            + ". Updated balance: ₹" + finalBalance
                                            + ". Transaction ID: " + transaction.getTransactionId());
                                    return "redirect:/customer/confirmation";
                                } else {
                                    model.addAttribute("error", "Balance verification failed after deposit.");
                                }
                            } else {
                                model.addAttribute("error", "Transaction failed.");
                            }
                        } else {
                            model.addAttribute("error", "Account not found or does not belong to the customer.");
                        }
                        break outerLoop;
                    }
                }
            }
        } else {
            model.addAttribute("error", "Customer not found or session is invalid.");
        }
        return "customer/accounts_deposit";
    }

    @GetMapping("/customer/accounts_withdraw")
    public String showWithdrawForm(@RequestParam("accountId") String accountId, Model model) {
        model.addAttribute("accountId", accountId);
        return "customer/accounts_withdraw";
    }

    @PostMapping("/customer/accounts_withdraw")
    public String withdrawFromAccount(
            @RequestParam("accountId") String accountId,
            @RequestParam("amount") double amount,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        String customerId = (String) session.getAttribute("cid_one");

        if (customerId != null && bank != null) {
            outerLoop: for (Branch branch : bank.getBranches()) {
                for (Customer customer : branch.getCustomers()) {
                    if (customer.getCIF().equals(customerId)) {
                        Account account = null;

                        for (Account acc : customer.getAccountDetails()) {
                            if (acc.getAccountNumber().equals(accountId)) {
                                account = acc;
                                break;
                            }
                        }

                        if (account != null) {
                            double initialBalance = account.getBalance();
                            Transaction transaction = new Transaction(account, account, "Withdraw", amount);

                            if (transaction.processTransaction()) {
                                redirectAttributes.addFlashAttribute("title", "Amount Successfully Withdrawn");
                                redirectAttributes.addFlashAttribute("message", "₹" + amount +
                                        " has been successfully withdrawn from account "
                                        + account.getAccountNumber() +
                                        ". Updated balance: ₹" + initialBalance + amount + ". Transaction ID: "
                                        + transaction.getTransactionId());
                                return "redirect:/customer/confirmation";
                            } else {
                                model.addAttribute("error", "Transaction failed.");
                            }
                        } else {
                            model.addAttribute("error", "Account not found or does not belong to the customer.");
                        }
                        break outerLoop;
                    }
                }
            }
        } else {
            model.addAttribute("error", "Customer not found or session is invalid.");
        }
        return "customer/accounts_withdraw";
    }

    @GetMapping("/customer/accounts_payment")
    public String showPaymentForm(@RequestParam("accountId") String accountId, Model model) {
        model.addAttribute("accountId", accountId);
        return "customer/accounts_payment";
    }

    @PostMapping("/customer/accounts_payment")
    public String transferAmount(
            @RequestParam("sourceAccountId") String sourceAccountId,
            @RequestParam("destinationAccountId") String destinationAccountId,
            @RequestParam("amount") double amount,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        String customerId = (String) session.getAttribute("cid_one");

        if (customerId != null && bank != null) {
            Account sourceAccount = null;
            Account destinationAccount = null;

            outerLoop1: for (Branch branch : bank.getBranches()) {
                for (Customer customer : branch.getCustomers()) {
                    for (Account account : customer.getAccountDetails()) {
                        if (account.getAccountNumber().equals(sourceAccountId)
                                && customer.getCIF().equals(customerId)) {
                            sourceAccount = account;
                            break outerLoop1;
                        }
                    }
                }
            }

            outerLoop2: for (Branch branch : bank.getBranches()) {
                for (Customer customer : branch.getCustomers()) {
                    for (Account account : customer.getAccountDetails()) {
                        if (account.getAccountNumber().equals(destinationAccountId)) {
                            destinationAccount = account;
                            break outerLoop2;
                        }
                    }
                }
            }

            if (sourceAccount != null && destinationAccount != null) {
                Transaction transaction = new Transaction(sourceAccount, destinationAccount, "Transfer", amount);

                if (transaction.processTransaction()) {
                    double sourceFinalBalance = sourceAccount.getBalance();

                    redirectAttributes.addFlashAttribute("title", "Amount Successfully Transferred");
                    redirectAttributes.addFlashAttribute("message", "₹" + amount +
                            " has been successfully transferred from account " + sourceAccount.getAccountNumber() +
                            " to account " + destinationAccount.getAccountNumber() +
                            ". Updated balance: ₹" + sourceFinalBalance + ". Transaction ID: "
                            + transaction.getTransactionId());
                    return "redirect:/customer/confirmation";
                } else {
                    model.addAttribute("error", "Transaction failed.");
                }
            } else {
                model.addAttribute("error",
                        "Source or destination account not found, or source account does not belong to the customer.");
            }
        } else {
            model.addAttribute("error", "Customer not found or session is invalid.");
        }

        return "customer/accounts_payment";
    }

    @GetMapping("/customer/accounts_transaction")
    public String getAccountTransactions(
            @RequestParam("accountId") String accountId,
            Model model,
            HttpSession session) {

        String customerId = (String) session.getAttribute("cid_one");

        if (customerId != null && bank != null) {
            Account targetAccount = null;

            outerLoop: for (Branch branch : bank.getBranches()) {
                for (Customer customer : branch.getCustomers()) {
                    if (customer.getCIF().equals(customerId)) {
                        for (Account account : customer.getAccountDetails()) {
                            if (account.getAccountNumber().equals(accountId)) {
                                targetAccount = account;
                                break outerLoop;
                            }
                        }
                    }
                }
            }

            if (targetAccount != null) {
                List<Map<String, Object>> transactionsList = new ArrayList<>();

                for (Transaction transaction : targetAccount.getTransactions()) {
                    Map<String, Object> transactionMap = new HashMap<>();
                    transactionMap.put("transactionId", transaction.getTransactionId());
                    transactionMap.put("amount", transaction.getAmount());
                    transactionMap.put("transactionType", transaction.getTransactionType());

                    transactionsList.add(transactionMap);
                }

                model.addAttribute("transactions", transactionsList);
                model.addAttribute("account", targetAccount.getAccountNumber());
                model.addAttribute("balance", targetAccount.getBalance());
                model.addAttribute("count", transactionsList.size());
            } else {
                model.addAttribute("error", "Account not found or does not belong to the logged-in customer");
            }
        } else {
            model.addAttribute("error", "Customer not found or session is invalid");
        }

        return "customer/accounts_transaction";
    }

    /*
     * Customer cards section
     */

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

    @GetMapping("/customer/cards_info")
    public String getCardInfo(@RequestParam("cardNumber") String cardNumber, Model model, HttpSession session) {
        String customerId = (String) session.getAttribute("cid_one");

        if (customerId != null && bank != null) {
            boolean cardFound = false;
            outerloop: for (Branch branch : bank.getBranches()) {
                for (Customer customer : branch.getCustomers()) {
                    if (customer.getCIF().equals(customerId)) {
                        for (Card card : customer.getCardDetails()) {
                            if (card.getCardNumber().equals(cardNumber)) {
                                model.addAttribute("card", card.getCardNumber());
                                model.addAttribute("name", card.getName());
                                model.addAttribute("expiry", card.getExpiryDate());
                                model.addAttribute("status", card.getStatus());
                                model.addAttribute("cvv", card.getCvv());
                                model.addAttribute("cardType",
                                        (card instanceof DebitCard) ? "Debit Card" : "Credit Card");
                                cardFound = true;
                                break outerloop;
                            }
                        }
                    }
                }
            }

            if (!cardFound) {
                model.addAttribute("error", "Card with number " + cardNumber + " not found for customer " + customerId);
            }
        } else {
            model.addAttribute("error", "Session is invalid or customer ID is not set");
        }
        return "customer/cards_info";
    }

    /*
     * Customer loan applications section
     */

    @GetMapping("/customer/loanapp_list")
    public String getCustomerLoanApplications(Model model, HttpSession session) {
        String customerId = (String) session.getAttribute("cid_one");

        if (customerId != null && bank != null) {
            List<Map<String, Object>> customerLoanApplications = new ArrayList<>();

            outerLoop: for (Branch branch : bank.getBranches()) {
                for (Customer customer : branch.getCustomers()) {
                    if (customer.getCIF().equals(customerId)) {
                        for (LoanApplication loanApp : branch.getLoanApplications()) {
                            if (loanApp.getCustomerID().equals(customer.getCIF())) {
                                Map<String, Object> loanAppDetails = new HashMap<>();
                                loanAppDetails.put("applicationId", loanApp.getApplicationId());
                                loanAppDetails.put("amount", loanApp.getAmount());
                                loanAppDetails.put("loanType", loanApp.getLoanType());

                                customerLoanApplications.add(loanAppDetails);
                            }
                        }
                        break outerLoop;
                    }
                }
            }

            if (!customerLoanApplications.isEmpty()) {
                model.addAttribute("loanApplications", customerLoanApplications);
            } else {
                model.addAttribute("error", "No loan applications found for customer ID " + customerId);
            }
        } else {
            model.addAttribute("error", "Customer ID not found or session is invalid");
        }

        return "customer/loanapp_list";
    }

    @GetMapping("/customer/loanapp_info")
    public String getLoanApplicationInfo(@RequestParam("applicationId") String applicationId, Model model,
            HttpSession session) {
        String customerId = (String) session.getAttribute("cid_one");

        if (customerId != null && bank != null) {
            LoanApplication targetLoanApplication = null;

            outerLoop: for (Branch branch : bank.getBranches()) {
                for (Customer customer : branch.getCustomers()) {
                    if (customer.getCIF().equals(customerId)) {
                        for (LoanApplication loanApp : branch.getLoanApplications()) {
                            if (loanApp.getApplicationId().equals(applicationId)) {
                                targetLoanApplication = loanApp;
                                break outerLoop;
                            }
                        }
                    }
                }
            }

            if (targetLoanApplication != null) {
                model.addAttribute("loanApplication", targetLoanApplication);
            } else {
                model.addAttribute("error",
                        "Loan application with ID " + applicationId + " not found for this customer.");
            }
        } else {
            model.addAttribute("error", "Customer ID not found or session is invalid.");
        }

        return "customer/loanapp_info";
    }

    @GetMapping("/customer/loanapp_create")
    public String showPaymentForm() {
        return "customer/loanapp_create";
    }

    @PostMapping("/customer/loanapp_create")
    public String createLoanApplication(
            @RequestParam("loanAmount") double loanAmount,
            @RequestParam("loanType") String loanType,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        String customerId = (String) session.getAttribute("cid_one");

        if (customerId != null && bank != null) {
            boolean customerFound = false;

            for (Branch branch : bank.getBranches()) {
                for (Customer customer : branch.getCustomers()) {
                    if (customer.getCIF().equals(customerId)) {
                        LocalDate applicationDate = LocalDate.now();

                        LoanApplication newLoanApp = new LoanApplication(customer, loanAmount, loanType,
                                applicationDate);

                        customer.applyLoan(newLoanApp);
                        branch.addLoanApplication(newLoanApp);

                        redirectAttributes.addFlashAttribute("title", "Successfully Created Loan Application");
                        redirectAttributes.addFlashAttribute("message",
                                "Loan application with application ID " + newLoanApp.getApplicationId() +
                                        " has been created for Customer " + customer.getName() +
                                        ". Kindly wait for some time while the application is reviewed.");

                        customerFound = true;
                        return "redirect:/customer/confirmation";
                    }
                }
            }

            if (!customerFound) {
                model.addAttribute("error", "Customer not found.");
            }
        } else {
            model.addAttribute("error", "Customer not found or session is invalid.");
        }

        return "customer/loanapp_create";
    }

    /*
     * Customer loan account section
     */

    @GetMapping("/customer/loans_list")
    public String getCustomerLoanAccounts(Model model, HttpSession session) {
        String customerId = (String) session.getAttribute("cid_one");

        if (customerId != null && bank != null) {
            List<Map<String, Object>> customerLoanAccountsList = new ArrayList<>();

            outerLoop: for (Branch branch : bank.getBranches()) {
                for (Customer customer : branch.getCustomers()) {
                    if (customer.getCIF().equals(customerId)) {
                        for (LoanAccount loanAccount : branch.getLoanAccounts()) {
                            if (loanAccount.getCustomer().getCIF().equals(customerId)) {
                                Map<String, Object> loanDetails = new HashMap<>();
                                loanDetails.put("loanId", loanAccount.getLoanId());
                                loanDetails.put("interestRate", loanAccount.getInterestRate());
                                loanDetails.put("balance", loanAccount.getBalance());
                                customerLoanAccountsList.add(loanDetails);
                            }
                        }
                        break outerLoop;
                    }
                }
            }

            if (!customerLoanAccountsList.isEmpty()) {
                model.addAttribute("loanAccounts", customerLoanAccountsList);
            } else {
                model.addAttribute("error", "No loan accounts found for customer ID " + customerId);
            }
        } else {
            model.addAttribute("error", "Customer ID not found or session is invalid");
        }

        return "customer/loans_list";
    }

    @GetMapping("/customer/loans_info")
    public String getLoanAccountDetails(
            @RequestParam("loanId") String loanId,
            Model model,
            HttpSession session) {

        String customerId = (String) session.getAttribute("cid_one");

        if (customerId != null && bank != null) {
            boolean loanFound = false;

            outerLoop: for (Branch branch : bank.getBranches()) {
                for (Customer customer : branch.getCustomers()) {
                    if (customer.getCIF().equals(customerId)) {

                        for (LoanAccount loanAcct : branch.getLoanAccounts()) {
                            if (loanAcct.getLoanId().equals(loanId) &&
                                    loanAcct.getCustomer().getCIF().equals(customerId)) {

                                model.addAttribute("loanAccount", loanAcct);
                                loanFound = true;
                                break outerLoop;
                            }
                        }
                    }
                }
            }

            if (!loanFound) {
                model.addAttribute("error", "No loan account found for loan ID " + loanId);
            }
        } else {
            model.addAttribute("error", "Customer ID not found or session is invalid.");
        }

        return "customer/loans_info";
    }

    @GetMapping("/customer/loans_payment")
    public String showLoanPaymentForm(@RequestParam("loanId") String accountId, Model model) {
        model.addAttribute("loanId", accountId);
        return "customer/loans_payment";
    }

    @PostMapping("/customer/loans_payment")
    public String makeLoanPayment(
            @RequestParam("amount") Double amount,
            @RequestParam("loanId") String loanId,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        String customerId = (String) session.getAttribute("cid_one");

        if (customerId != null && bank != null) {
            boolean paymentSuccessful = false;

            for (Branch branch : bank.getBranches()) {
                for (Customer customer : branch.getCustomers()) {
                    if (customer.getCIF().equals(customerId)) {

                        for (LoanAccount loanAcct : branch.getLoanAccounts()) {
                            if (loanAcct.getLoanId().equals(loanId) &&
                                    loanAcct.getCustomer().getCIF().equals(customerId)) {

                                if (amount <= 0) {
                                    model.addAttribute("error", "Invalid payment amount. It must be greater than 0.");
                                    return "customer/loans_payment";
                                }

                                double oldBalance = loanAcct.getBalance();

                                if (amount > oldBalance) {
                                    model.addAttribute("error",
                                            "Payment amount exceeds the remaining balance. Remaining balance: "
                                                    + oldBalance);
                                    return "customer/loans_payment";
                                }

                                loanAcct.makePayment(amount);

                                if (loanAcct.getBalance() == oldBalance - amount) {
                                    paymentSuccessful = true;
                                    redirectAttributes.addFlashAttribute("title", "Payment Successful");
                                    redirectAttributes.addFlashAttribute("message",
                                            "Payment of ₹" + amount + " has been made to loan ID " + loanId +
                                                    ". Remaining balance: ₹" + loanAcct.getBalance());
                                    return "redirect:/customer/confirmation";
                                } else {
                                    model.addAttribute("error", "Error: Balance mismatch after payment.");
                                    return "customer/loans_payment";
                                }
                            }
                        }
                    }
                }
            }

            if (!paymentSuccessful) {
                model.addAttribute("error",
                        "No loan account found for loan ID " + loanId + ", or payment could not be processed.");
                return "customer/loans_payment";
            }
        } else {
            model.addAttribute("error", "Customer ID not found or session is invalid.");
            return "customer/loans_payment";
        }

        return "customer/loans_payment";
    }

    /*
     * Employee application and routing starts from here
     */

    @GetMapping("/employee")
    public String redirectToEmployeeIndex(Model model, HttpSession session) {
        return "employee/index";
    }

    @GetMapping("/employee/confirmation")
    public String employeeConfirmation() {
        return "confirmation";
    }

    @GetMapping("/employee/profile")
    public String redirectToEmployeeProfile(Model model, HttpSession session) {
        String employeeId = (String) session.getAttribute("eid");

        if (employeeId != null && bank != null) {
            for (Branch branch : bank.getBranches()) {
                for (Employee employee : branch.getEmployees()) {
                    if (employee.getEmployeeId().equals(employeeId)) {
                        model.addAttribute("ename", employee.getEmployeeName());
                        model.addAttribute("eid", employee.getEmployeeId());
                        model.addAttribute("edept", employee.getDepartment());
                        model.addAttribute("edesignation", employee.getDesignation());
                        model.addAttribute("esalary", employee.getSalary());
                        model.addAttribute("ephone", employee.getPhoneNumber());
                        model.addAttribute("eemail", employee.getEmail());
                        model.addAttribute("ejoin", employee.getJoiningDate());
                        model.addAttribute("ebranch", employee.getBranch().getBranchName());
                        break;
                    }
                }
            }
        } else {
            model.addAttribute("error", "Employee not found or session is invalid");
        }
        return "employee/profile";
    }

    @GetMapping("/employee/customers_list")
    public String getCustomersOfEmployeeBranch(Model model, HttpSession session) {
        String employeeId = (String) session.getAttribute("eid");

        if (employeeId != null && bank != null) {
            outerLoop: for (Branch branch : bank.getBranches()) {
                for (Employee employee : branch.getEmployees()) {
                    if (employee.getEmployeeId().equals(employeeId)) {
                        List<Map<String, Object>> customersList = new ArrayList<>();

                        for (Customer customer : branch.getCustomers()) {
                            Map<String, Object> customerMap = new HashMap<>();
                            customerMap.put("cif", customer.getCIF());
                            customerMap.put("phoneNo", customer.getPhoneNo());
                            customerMap.put("name", customer.getName());

                            customersList.add(customerMap);
                        }

                        model.addAttribute("customers", customersList);
                        break outerLoop;
                    }
                }
            }
        } else {
            model.addAttribute("error", "Employee not found or session is invalid");
        }

        return "employee/customers_list";
    }

    @GetMapping("/employee/customers_info")
    public String getCustomerDetails(@RequestParam("customerId") String customerId, Model model, HttpSession session) {
        String employeeId = (String) session.getAttribute("eid");

        if (employeeId != null && bank != null) {
            boolean customerFound = false;

            outerLoop: for (Branch branch : bank.getBranches()) {
                for (Employee employee : branch.getEmployees()) {
                    if (employee.getEmployeeId().equals(employeeId)) {
                        for (Customer customer : branch.getCustomers()) {
                            if (customer.getCIF().equals(customerId)) {
                                model.addAttribute("cid", customer.getCIF());
                                model.addAttribute("name", customer.getName());
                                model.addAttribute("phone", customer.getPhoneNo());
                                model.addAttribute("address", customer.getAddress());

                                int accountCount = customer.getAccountDetails().size();
                                model.addAttribute("accountCount", accountCount);

                                int transactionCount = customer.getAccountDetails().stream()
                                        .mapToInt(account -> account.getTransactions().size())
                                        .sum();
                                model.addAttribute("transactionCount", transactionCount);

                                int cardCount = customer.getCardDetails().size();
                                model.addAttribute("cardCount", cardCount);

                                customerFound = true;
                                break outerLoop;
                            }
                        }
                    }
                }
            }

            if (!customerFound) {
                model.addAttribute("error", "Customer with ID " + customerId + " not found for employee's branch");
            }
        } else {
            model.addAttribute("error", "Employee not found or session is invalid");
        }

        return "employee/customers_info";
    }

    @GetMapping("/employee/loanapp_list")
    public String getLoanApplicationsForEmployee(Model model, HttpSession session) {
        String employeeId = (String) session.getAttribute("eid");

        if (employeeId != null && bank != null) {
            List<HashMap<String, Object>> loanApplicationsForBranch = new ArrayList<>();

            outerLoop: for (Branch branch : bank.getBranches()) {
                for (Employee employee : branch.getEmployees()) {
                    if (employee.getEmployeeId().equals(employeeId)) {
                        List<LoanApplication> loanApplications = branch.getLoanApplications();

                        for (LoanApplication loanApplication : loanApplications) {
                            HashMap<String, Object> loanAppData = new HashMap<>();
                            loanAppData.put("customerName", loanApplication.getCustomer().getName());
                            loanAppData.put("loanType", loanApplication.getLoanType());
                            loanAppData.put("loanAmount", loanApplication.getAmount());
                            loanAppData.put("applicationId", loanApplication.getApplicationId());
                            loanApplicationsForBranch.add(loanAppData);
                        }

                        break outerLoop;
                    }
                }
            }

            if (!loanApplicationsForBranch.isEmpty()) {
                model.addAttribute("loanApplications", loanApplicationsForBranch);
            } else {
                model.addAttribute("error", "No loan applications found for the employee's branch.");
            }
        } else {
            model.addAttribute("error", "Employee ID not found or session is invalid.");
        }

        return "employee/loanapp_list";
    }

    @GetMapping("/employee/loanapp_approve")
    public String showLoanApp(@RequestParam("loanId") String accountId, Model model) {
        model.addAttribute("loanId", accountId);
        return "employee/loanapp_approve";
    }

    @PostMapping("/employee/loanapp_approve")
    public String verifyOrRejectLoan(
            @RequestParam("loanId") String loanId,
            @RequestParam("action") String action,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        String employeeId = (String) session.getAttribute("eid");

        if (employeeId != null && bank != null) {
            boolean loanProcessed = false;
            outerLoop: for (Branch branch : bank.getBranches()) {
                for (Employee employee : branch.getEmployees()) {
                    if (employee.getEmployeeId().equals(employeeId)) {

                        for (LoanApplication loanApp : branch.getLoanApplications()) {
                            if (loanApp.getApplicationId().equals(loanId) && !loanApp.isVerified()) {

                                if ("verify".equalsIgnoreCase(action)) {
                                    employee.verifyLoan(loanApp, true);
                                    redirectAttributes.addFlashAttribute("title", "Loan Application Verified");
                                    redirectAttributes.addFlashAttribute("message",
                                            "Loan application with ID " + loanId + " has been successfully verified.");
                                } else if ("reject".equalsIgnoreCase(action)) {
                                    employee.verifyLoan(loanApp, false);
                                    redirectAttributes.addFlashAttribute("title", "Loan Application Rejected");
                                    redirectAttributes.addFlashAttribute("message",
                                            "Loan application with ID " + loanId + " has been rejected.");
                                } else {
                                    model.addAttribute("error", "Invalid action specified.");
                                    return "employee/loanapp_approve";
                                }

                                loanProcessed = true;
                                return "redirect:/employee/confirmation";
                            }
                        }

                        if (!loanProcessed) {
                            model.addAttribute("error", "Loan application not found or already verified.");
                        }
                        break outerLoop;
                    }
                }
            }

            if (!loanProcessed) {
                model.addAttribute("error", "Employee not found or loan application not processed.");
            }
        } else {
            model.addAttribute("error", "Employee not found or session is invalid.");
        }

        return "employee/loanapp_approve";
    }

    /*
     * Manager application and routing starts from here
     */

    @GetMapping("/manager")
    public String redirectToManagerIndex(Model model, HttpSession session) {
        return "manager/index";
    }

    @GetMapping("/manager/confirmation")
    public String managerConfirmation() {
        return "confirmation";
    }

    @GetMapping("/manager/profile")
    public String getManagerProfile(Model model, HttpSession session) {
        String managerId = (String) session.getAttribute("mid");

        if (managerId != null && bank != null) {
            for (Branch branch : bank.getBranches()) {
                for (Employee employee : branch.getEmployees()) {
                    if (employee.getEmployeeId().equals(managerId) && employee instanceof Manager) {
                        Manager manager = (Manager) employee;

                        model.addAttribute("mname", manager.getEmployeeName());
                        model.addAttribute("mid", manager.getEmployeeId());
                        model.addAttribute("mdesignation", employee.getDesignation());
                        model.addAttribute("msalary", manager.getSalary());
                        model.addAttribute("mphone", manager.getPhoneNumber());
                        model.addAttribute("memail", manager.getEmail());
                        model.addAttribute("mjoin", manager.getJoiningDate());
                        model.addAttribute("mbranch", manager.getBranch().getBranchName());
                        break;
                    }
                }
            }
        } else {
            model.addAttribute("error", "Manager not found or session is invalid");
        }

        return "manager/profile";
    }

    @GetMapping("/manager/employees_list")
    public String getEmployeesOfManagerBranch(Model model, HttpSession session) {
        String managerId = (String) session.getAttribute("mid");

        if (managerId != null && bank != null) {
            List<HashMap<String, Object>> employeeList = new ArrayList<>();

            for (Branch branch : bank.getBranches()) {
                for (Employee employee : branch.getEmployees()) {
                    if (employee instanceof Manager && employee.getEmployeeId().equals(managerId)) {
                        continue;
                    }

                    HashMap<String, Object> employeeData = new HashMap<>();
                    employeeData.put("employeeName", employee.getEmployeeName());
                    employeeData.put("designation", employee.getDesignation());
                    employeeData.put("salary", employee.getSalary());
                    employeeData.put("employeeId", employee.getEmployeeId());

                    employeeList.add(employeeData);
                }
            }

            model.addAttribute("employees", employeeList);
        } else {
            model.addAttribute("error", "Manager not found or session is invalid");
        }

        return "manager/employees_list";
    }

    @GetMapping("/manager/employee_info")
    public String getEmployeeInfo(@RequestParam("employeeId") String employeeId, Model model, HttpSession session) {
        String managerId = (String) session.getAttribute("mid");

        if (managerId != null && bank != null) {
            boolean employeeFound = false;
            outerLoop: for (Branch branch : bank.getBranches()) {
                for (Employee employee : branch.getEmployees()) {
                    if (employee.getEmployeeId().equals(employeeId)) {

                        model.addAttribute("name", employee.getEmployeeName());
                        model.addAttribute("id", employee.getEmployeeId());
                        model.addAttribute("desg", employee.getDepartment());
                        model.addAttribute("dept", employee.getDesignation());
                        model.addAttribute("join", employee.getJoiningDate());
                        model.addAttribute("email", employee.getEmail());
                        model.addAttribute("phone", employee.getPhoneNumber());
                        model.addAttribute("salary", employee.getSalary());

                        employeeFound = true;
                        break outerLoop;
                    }
                }
            }

            if (!employeeFound) {
                model.addAttribute("error", "Employee with ID " + employeeId + " not found in the manager's branch");
            }
        } else {
            model.addAttribute("error", "Manager not found or session is invalid");
        }

        return "manager/employee_info";
    }

    @GetMapping("/manager/employee_salary")
    public String showEmployeeApp(@RequestParam("employeeId") String accountId, Model model) {
        model.addAttribute("employeeId", accountId);
        return "manager/employee_salary";
    }

    @PostMapping("/manager/employee_salary")
    public String updateEmployeeSalary(
            @RequestParam("employeeId") String employeeId,
            @RequestParam("newSalary") double newSalary,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        String managerId = (String) session.getAttribute("mid");

        if (managerId != null && bank != null) {
            boolean employeeFound = false;
            outerLoop: for (Branch branch : bank.getBranches()) {
                for (Employee employee : branch.getEmployees()) {
                    if (employee.getEmployeeId().equals(employeeId)) {
                        double oldSalary = employee.getSalary();
                        employee.updateSalary(newSalary);

                        if (employee.getSalary() == newSalary) {
                            redirectAttributes.addFlashAttribute("title", "Salary Update Successful");
                            redirectAttributes.addFlashAttribute("message", "The salary of employee "
                                    + employee.getEmployeeName() + " has been updated from ₹" + oldSalary
                                    + " to ₹" + newSalary);
                            return "redirect:/manager/confirmation";
                        } else {
                            model.addAttribute("error", "Failed to update salary for Employee ID " + employeeId);
                        }

                        employeeFound = true;
                        break outerLoop;
                    }
                }
            }

            if (!employeeFound) {
                model.addAttribute("error", "Employee with ID " + employeeId + " not found in the manager's branch.");
            }
        } else {
            model.addAttribute("error", "Manager not found or session is invalid.");
        }

        return "manager/employee_salary";
    }

    @GetMapping("/manager/loanapp_list")
    public String getLoanApplicationsForManager(Model model, HttpSession session) {
        String managerId = (String) session.getAttribute("mid");

        if (managerId != null && bank != null) {
            List<HashMap<String, Object>> loanApplicationsForBranch = new ArrayList<>();

            outerLoop: for (Branch branch : bank.getBranches()) {
                for (Employee employee : branch.getEmployees()) {
                    if (employee.getEmployeeId().equals(managerId)) {
                        for (LoanApplication loanApp : branch.getLoanApplications()) {
                            if (loanApp.isVerified()) {
                                HashMap<String, Object> loanAppData = new HashMap<>();
                                loanAppData.put("applicationId", loanApp.getApplicationId());
                                loanAppData.put("loanType", loanApp.getLoanType());
                                loanAppData.put("loanAmount", loanApp.getAmount());

                                loanApplicationsForBranch.add(loanAppData);
                            }
                        }
                        break outerLoop;
                    }
                }
            }

            if (!loanApplicationsForBranch.isEmpty()) {
                model.addAttribute("loanApplications", loanApplicationsForBranch);
            } else {
                model.addAttribute("error", "No loan applications found for the employee's branch.");
            }
        } else {
            model.addAttribute("error", "Employee ID not found or session is invalid.");
        }

        return "manager/loanapp_list";
    }

    @GetMapping("/manager/loanapp_approve")
    public String showManLoanApp(@RequestParam("loanId") String accountId, Model model) {
        model.addAttribute("loanId", accountId);
        return "manager/loanapp_approve";
    }

    @PostMapping("/manager/loanapp_approve")
    public String approveLoanApplication(
            @RequestParam("loanId") String loanId,
            @RequestParam("interestRate") double interestRate,
            @RequestParam("loanAmount") double loanAmount,
            @RequestParam("loanTerm") int loanTerm,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        String managerId = (String) session.getAttribute("mid");

        if (managerId != null && bank != null) {
            boolean loanApproved = false;

            outerLoop: for (Branch branch : bank.getBranches()) {
                for (Employee employee : branch.getEmployees()) {
                    if (employee instanceof Manager && employee.getEmployeeId().equals(managerId)) {
                        Manager manager = (Manager) employee;

                        for (LoanApplication loanApp : branch.getLoanApplications()) {
                            if (loanApp.getApplicationId().equals(loanId) && loanApp.isVerified()) {

                                manager.approveLoan(loanApp);

                                Customer customer = null;
                                for (Customer branchCustomer : branch.getCustomers()) {
                                    if (branchCustomer.getCIF().equals(loanApp.getCustomerID())) {
                                        customer = branchCustomer;
                                        break;
                                    }
                                }

                                if (customer != null) {
                                    LocalDate loanStartDate = LocalDate.now();
                                    LoanAccount newLoanAccount = new LoanAccount(
                                            loanId, customer, loanAmount, interestRate, loanTerm, loanStartDate);

                                    branch.addLoanAccount(newLoanAccount);

                                    redirectAttributes.addFlashAttribute("title", "Loan Application Approved");
                                    redirectAttributes.addFlashAttribute("message",
                                            "Loan application has been approved, "
                                                    + "and a new loan account has been created for customer "
                                                    + customer.getName() + ". Loan ID: " + loanId
                                                    + ", Amount: ₹" + loanAmount + ", Term: " + loanTerm + " months.");

                                    loanApproved = true;
                                    return "redirect:/manager/confirmation";
                                } else {
                                    model.addAttribute("error", "Customer not found for the loan application.");
                                }
                                break outerLoop;
                            }
                        }

                        if (!loanApproved) {
                            model.addAttribute("error", "Loan application not found or not verified for approval.");
                        }
                        break outerLoop;
                    }
                }
            }

            if (!loanApproved) {
                model.addAttribute("error", "Manager not found or loan application not processed.");
            }
        } else {
            model.addAttribute("error", "Manager not found or session is invalid.");
        }

        return "manager/loanapp_approve";
    }
}