import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Student Management System - Main Application
 * A console-based CRUD application for managing student records.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentManager manager = new StudentManager("students.dat");

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("    STUDENT MANAGEMENT SYSTEM");
        System.out.println("==============================================");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");
            System.out.println();

            switch (choice) {
                case 1: addStudent(); break;
                case 2: viewAllStudents(); break;
                case 3: searchStudent(); break;
                case 4: updateStudent(); break;
                case 5: deleteStudent(); break;
                case 6:
                    running = false;
                    System.out.println("Thank you for using Student Management System!");
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println();
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("----------------------------------------------");
        System.out.println("  1. Add New Student");
        System.out.println("  2. View All Students");
        System.out.println("  3. Search Student");
        System.out.println("  4. Update Student");
        System.out.println("  5. Delete Student");
        System.out.println("  6. Exit");
        System.out.println("----------------------------------------------");
    }

    private static void addStudent() {
        System.out.println("--- Add New Student ---");
        String name = readString("Enter name: ");
        int age = readInt("Enter age: ");
        String grade = readString("Enter grade (e.g., A, B, C): ");
        String email = readString("Enter email: ");

        Student student = manager.addStudent(name, age, grade, email);
        System.out.println("\nStudent added successfully!");
        System.out.println(Student.getTableHeader());
        System.out.println(student);
        System.out.println(Student.getTableFooter());
    }

    private static void viewAllStudents() {
        System.out.println("--- All Students ---");
        List<Student> students = manager.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No students found. Add some students first!");
            return;
        }
        System.out.println("Total students: " + students.size());
        System.out.println(Student.getTableHeader());
        for (Student s : students) {
            System.out.println(s);
        }
        System.out.println(Student.getTableFooter());
    }

    private static void searchStudent() {
        System.out.println("--- Search Student ---");
        System.out.println("  1. Search by ID");
        System.out.println("  2. Search by Name");
        int choice = readInt("Enter choice: ");

        if (choice == 1) {
            int id = readInt("Enter student ID: ");
            Optional<Student> student = manager.findById(id);
            if (student.isPresent()) {
                System.out.println(Student.getTableHeader());
                System.out.println(student.get());
                System.out.println(Student.getTableFooter());
            } else {
                System.out.println("No student found with ID: " + id);
            }
        } else if (choice == 2) {
            String name = readString("Enter name to search: ");
            List<Student> results = manager.searchByName(name);
            if (results.isEmpty()) {
                System.out.println("No students found matching: " + name);
            } else {
                System.out.println("Found " + results.size() + " student(s):");
                System.out.println(Student.getTableHeader());
                for (Student s : results) {
                    System.out.println(s);
                }
                System.out.println(Student.getTableFooter());
            }
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static void updateStudent() {
        System.out.println("--- Update Student ---");
        int id = readInt("Enter student ID to update: ");

        Optional<Student> existing = manager.findById(id);
        if (existing.isEmpty()) {
            System.out.println("No student found with ID: " + id);
            return;
        }

        System.out.println("Current details:");
        System.out.println(Student.getTableHeader());
        System.out.println(existing.get());
        System.out.println(Student.getTableFooter());

        System.out.println("\nEnter new values (press Enter to keep current value):");
        String name = readString("New name [" + existing.get().getName() + "]: ");
        String ageStr = readString("New age [" + existing.get().getAge() + "]: ");
        int age = ageStr.isEmpty() ? -1 : Integer.parseInt(ageStr);
        String grade = readString("New grade [" + existing.get().getGrade() + "]: ");
        String email = readString("New email [" + existing.get().getEmail() + "]: ");

        if (manager.updateStudent(id, name, age, grade, email)) {
            System.out.println("\nStudent updated successfully!");
            manager.findById(id).ifPresent(s -> {
                System.out.println(Student.getTableHeader());
                System.out.println(s);
                System.out.println(Student.getTableFooter());
            });
        }
    }

    private static void deleteStudent() {
        System.out.println("--- Delete Student ---");
        int id = readInt("Enter student ID to delete: ");

        Optional<Student> existing = manager.findById(id);
        if (existing.isEmpty()) {
            System.out.println("No student found with ID: " + id);
            return;
        }

        System.out.println("Student to delete:");
        System.out.println(Student.getTableHeader());
        System.out.println(existing.get());
        System.out.println(Student.getTableFooter());

        String confirm = readString("Are you sure? (yes/no): ");
        if (confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y")) {
            if (manager.deleteStudent(id)) {
                System.out.println("Student deleted successfully!");
            }
        } else {
            System.out.println("Delete cancelled.");
        }
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
