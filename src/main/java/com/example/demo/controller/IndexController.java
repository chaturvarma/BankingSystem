package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Account;
import com.example.demo.model.Bank;
import com.example.demo.model.Branch;
import com.example.demo.model.Card;
import com.example.demo.model.CreditCard;
import com.example.demo.model.Customer;
import com.example.demo.model.DebitCard;
import com.example.demo.model.Employee;
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

    /*
     * Customer application and routing starts from here
     * This uses the route /customer fto perform all its functions
     */

    @GetMapping("/customer")
    public String redirectToCustomerIndex(Model model, HttpSession session) {
        return "customer/index";
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

    @PostMapping("/customer/accounts_deposit")
    public String depositToAccount(
            @RequestParam("accountId") String accountId,
            @RequestParam("amount") double amount,
            Model model,
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
                                    model.addAttribute("transactionId", transaction.getTransactionId());
                                    model.addAttribute("accountBalance", finalBalance);
                                    model.addAttribute("amount", amount);
                                    model.addAttribute("account", account.getAccountNumber());
                                } else {
                                    model.addAttribute("error", "Balance verification failed after deposit.");
                                }
                            } else {
                                model.addAttribute("error", "Transaction failed");
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

    @PostMapping("/customer/accounts_withdraw")
    public String withdrawFromAccount(
            @RequestParam("accountId") String accountId,
            @RequestParam("amount") double amount,
            Model model,
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
                                double finalBalance = account.getBalance();

                                if (finalBalance == initialBalance - amount) {
                                    model.addAttribute("transactionId", transaction.getTransactionId());
                                    model.addAttribute("accountBalance", finalBalance);
                                    model.addAttribute("amount", amount);
                                    model.addAttribute("account", account.getAccountNumber());
                                } else {
                                    model.addAttribute("error", "Balance verification failed after withdrawal");
                                }
                            } else {
                                model.addAttribute("error", "Transaction failed");
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

    @PostMapping("/customer/accounts_payment")
    public String transferAmount(
            @RequestParam("sourceAccountId") String sourceAccountId,
            @RequestParam("destinationAccountId") String destinationAccountId,
            @RequestParam("amount") double amount,
            Model model,
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
                double sourceInitialBalance = sourceAccount.getBalance();
                double destinationInitialBalance = destinationAccount.getBalance();

                Transaction transaction = new Transaction(sourceAccount, destinationAccount, "Transfer", amount);

                if (transaction.processTransaction()) {
                    double sourceFinalBalance = sourceAccount.getBalance();
                    double destinationFinalBalance = destinationAccount.getBalance();

                    if (sourceFinalBalance == sourceInitialBalance - (amount + transaction.calculateTransactionFees())
                            &&
                            destinationFinalBalance == destinationInitialBalance + amount) {

                        model.addAttribute("transactionId", transaction.getTransactionId());
                        model.addAttribute("sourceAccountBalance", sourceFinalBalance);
                        model.addAttribute("amount", amount);
                        model.addAttribute("source", sourceAccount.getAccountNumber());
                        model.addAttribute("destination", destinationAccount.getAccountNumber());
                    } else {
                        model.addAttribute("error", "Balance verification failed after transfer.");
                    }
                } else {
                    model.addAttribute("error", "Transaction failed");
                }
            } else {
                model.addAttribute("error",
                        "Source or destination account not found, or source account does not belong to the customer.");
            }
        } else {
            model.addAttribute("error", "Customer not found or session is invalid");
        }

        return "customer/accounts_payment";
    }

    @GetMapping("/customer/account_transaction")
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
                ArrayList<Transaction> transactions = targetAccount.getTransactions();
                model.addAttribute("transactions", transactions);
                model.addAttribute("account", targetAccount.getAccountNumber());
                model.addAttribute("balance", targetAccount.getBalance());
                model.addAttribute("count", transactions.size());
            } else {
                model.addAttribute("error", "Account not found or does not belong to the logged-in customer");
            }
        } else {
            model.addAttribute("error", "Customer not found or session is invalid");
        }

        return "customer/account_transaction";
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

    /*
     * Customer loan account section
     */

    /*
     * Employee application and routing starts from here
     */

    @GetMapping("/employee")
    public String redirectToEmployeeIndex(Model model, HttpSession session) {
        return "employee/index";
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
                        List<Customer> customers = new ArrayList<>();

                        for (Customer customer : branch.getCustomers()) {
                            customers.add(customer);
                        }

                        model.addAttribute("customers", customers);
                        model.addAttribute("branchName", branch.getBranchName());

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

    /*
     * Manager application and routing starts from here
     */

    @GetMapping("/manager")
    public String redirectToManagerIndex(Model model, HttpSession session) {
        return "manager/index";
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

    @GetMapping("/manager/employee_list")
    public String getEmployeesOfManagerBranch(Model model, HttpSession session) {
        String managerId = (String) session.getAttribute("mid");

        if (managerId != null && bank != null) {
            List<Employee> employeeList = new ArrayList<>();
            outerLoop: for (Branch branch : bank.getBranches()) {
                for (Employee employee : branch.getEmployees()) {
                    if (employee instanceof Manager && employee.getEmployeeId().equals(managerId)) {
                        continue outerLoop;
                    }

                    employeeList.add(employee);
                }
            }

            model.addAttribute("employees", employeeList);
        } else {
            model.addAttribute("error", "Manager not found or session is invalid");
        }

        return "manager/employee_list";
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

    @PostMapping("/manager/employee_salary")
    public String updateEmployeeSalary(
            @RequestParam("employeeId") String employeeId,
            @RequestParam("newSalary") double newSalary,
            Model model,
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
                            model.addAttribute("oldSalary", oldSalary);
                            model.addAttribute("newSalary", newSalary);
                            model.addAttribute("employee", employee.getEmployeeName());
                        } else {
                            model.addAttribute("error", "Failed to update salary for Employee ID " + employeeId);
                        }

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

        return "manager/employee_salary";
    }

}