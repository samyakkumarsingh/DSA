# Student Management System

A console-based Student Management System built using **Core Java** only. This application provides complete CRUD (Create, Read, Update, Delete) operations for managing student records.

## Features

- **Add Student** – Register new students with name, age, grade, and email
- **View All Students** – Display all student records in a formatted table
- **Search Student** – Find students by ID or name (supports partial name matching)
- **Update Student** – Modify existing student information
- **Delete Student** – Remove student records with confirmation
- **Data Persistence** – Student data is automatically saved to and loaded from a file

## Tech Stack

- **Language:** Java (Core Java only, no external frameworks)
- **Persistence:** Java Serialization (file-based storage)
- **UI:** Console-based menu-driven interface

## How to Run

### Prerequisites
- Java JDK 11 or higher

### Compile and Run

```bash
cd StudentManagementSystem/src
javac *.java
java Main
```

## Project Structure

```
StudentManagementSystem/
├── src/
│   ├── Main.java            # Entry point with menu-driven console UI
│   ├── Student.java         # Student model class (Serializable)
│   └── StudentManager.java  # Business logic and file persistence
└── README.md
```

## Usage

```
==============================================
    STUDENT MANAGEMENT SYSTEM
==============================================
----------------------------------------------
  1. Add New Student
  2. View All Students
  3. Search Student
  4. Update Student
  5. Delete Student
  6. Exit
----------------------------------------------
Enter your choice:
```
