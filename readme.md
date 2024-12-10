# OOPS Banking System

This repository contains a banking system project developed using Java and Spring Boot. This document provides instructions for setting up and running the project

## Prerequisites

Ensure the following software is installed on your system:

- **Java**: JDK 22.0.2 (2024-07-16)
- **Java(TM)**: SE Runtime Environment (build 22.0.2+9-70)
- **Java HotSpot(TM)**: 64-Bit Server VM (build 22.0.2+9-70, mixed mode, sharing)

## Steps to Run

1. Download the provided `demo-0.0.1-SNAPSHOT.jar` (https://github.com/chaturvarma/BankingSystem/blob/main/demo-0.0.1-SNAPSHOT.jar) file from the repository for the Java Banking System application

2. Open a terminal or command prompt and navigate to the directory where the `.jar` file is located

3. Run the application using the following command:
   ```bash
   java -jar demo-0.0.1-SNAPSHOT.jar

4. Navigate to **http://localhost:8080** in your browser to access the application

## Libraries and Programming Languages Used

- **Java**: Version 22.0.2 (2024-07-16): Core programming language
- **Spring Boot**: Version 2.7.0: Framework for building Java applications
- **HTML/CSS**: For user interface structure and styling
- **JavaScript**: For client-side interactivity
- **Thymeleaf**: Template engine for dynamic content rendering in Spring applications
- **Maven**: Primary build and dependency management tool
- **Gradle**: Version 8.0

## Code Contents

### Backend Code (All Classes)

The backend code is located in the following directory:  
`src/main/java/com/example/demo`

#### `model/`

Contains the core classes of the application, representing entities like User, Account, Transaction, etc. These classes define the structure of the data and interact with the database

#### `controller/`

Contains the controller classes responsible for routing and API control. These files manage HTTP requests, interact with the services, and return responses to the client

### Frontend Code

The frontend code and Thymeleaf templates are located in:  
`src/main/resources`

#### `templates/`

Contains HTML templates that define the structure and layout of the pages for various profiles of the application. These templates are used by Thymeleaf to dynamically render content based on the backend data

#### `static/`

Contains static assets like CSS, JavaScript, and image files used for the frontend

## Contributors

- Sri Sai Deep Dunduka (https://github.com/Saideep3376)
- Bhargav Madala (https://github.com/MvB2358)
- Krushik Teja Thati (https://github.com/Krushik2004)
- Anish Kumar Maganti (https://github.com/anish2626)
- Chatur Varma Inampudi (https://github.com/chaturvarma)
