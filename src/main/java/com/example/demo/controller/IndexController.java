package com.example.demo.controller;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.demo.model.Branch;
import com.example.demo.model.Customer;
import com.example.demo.model.Account;
import com.example.demo.model.Bank;
import com.example.demo.model.Branch;
import com.example.demo.model.Customer;
import com.example.demo.model.Employee;
import com.example.demo.model.Manager;
import com.example.demo.model.SavingsAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.demo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Calendar;
import java.util.Date;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

@Controller
public class IndexController {

    private List<Customer> customers = new ArrayList<>();
    private List<Account> accounts = new ArrayList<>();
    private List<Employee> employees = new ArrayList<>();
    private List<Manager> managers = new ArrayList<>();

    @GetMapping("/")
    public RedirectView initializeBank(Model model) {
        Bank bank = new Bank("Example Bank", "B001");
        Branch branch = new Branch("BR001", "Hyderabad", "123 Main St, Cityville", "123-456-7890");
        bank.addBranch(branch);

        Customer customer = new Customer("John Doe", "456 Elm St, Townsville", "987-654-3210");
        customers.add(customer);

        Account account1 = new SavingsAccount(customer, branch, 1000, 15000, 150);
        Account account2 = new SavingsAccount(customer, branch, 1000, 15000, 150);
        accounts.add(account1);
        accounts.add(account2);
        customer.addAccount(account1);
        customer.addAccount(account2);

        Date employeeJoiningDate = getDate(2022, Calendar.JANUARY, 15);
        Employee employee = new Employee(
                "Alice Johnson", bank, branch, "Teller", "Customer Service",
                50000.00, "123-555-7890", "alice.johnson@example.com",
                employeeJoiningDate
        );
        employees.add(employee);

        Date managerJoiningDate = getDate(2021, Calendar.MAY, 20);
        Manager manager = new Manager(
                "Bob Smith", bank, branch, "Branch Manager", "Operations",
                75000.00, "123-555-1234", "bob.smith@example.com",
                managerJoiningDate, "Customer Service"
        );
        managers.add(manager);

        String id = customer.getCIF();
        String redirectUrl = "/index.html?cid=" + id;
        return new RedirectView(redirectUrl);
    }

    private Date getDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    @GetMapping("/customer")
    public RedirectView redirectToCustomer() {
        return new RedirectView("/customer/index.html");
    }

    @GetMapping("/employee")
    public RedirectView redirectToEmployee() {
        return new RedirectView("/employee/index.html");
    }

    @GetMapping("/manager")
    public RedirectView redirectToManager() {
        return new RedirectView("/manager/index.html");
    }
}