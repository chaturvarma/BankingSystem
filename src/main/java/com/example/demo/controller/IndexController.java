package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String redirectToStaticIndex() {
        return "forward:/index.html";  // This forwards the request to the static index.html
    }

    @GetMapping("/customer")
    public String redirectToCustomerIndex() {
        return "customer/index";  // Maps to /templates/customer/index.html
    }

    @GetMapping("/employee")
    public String redirectToEmployeeIndex() {
        return "employee/index";  // Maps to /templates/employee/index.html
    }

    @GetMapping("/manager")
    public String redirectToManagerIndex() {
        return "manager/index";  // Maps to /templates/manager/index.html
    }
}