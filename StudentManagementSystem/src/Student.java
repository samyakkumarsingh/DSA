import java.io.Serializable;

/**
 * Represents a student entity with basic information.
 */
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private int age;
    private String grade;
    private String email;

    public Student(int id, String name, int age, String grade, String email) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.grade = grade;
        this.email = email;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return String.format("| %-5d | %-20s | %-5d | %-8s | %-25s |",
                id, name, age, grade, email);
    }

    /**
     * Returns a formatted header for displaying student records in a table.
     */
    public static String getTableHeader() {
        String separator = "+" + "-".repeat(7) + "+" + "-".repeat(22) + "+"
                + "-".repeat(7) + "+" + "-".repeat(10) + "+" + "-".repeat(27) + "+";
        String header = String.format("| %-5s | %-20s | %-5s | %-8s | %-25s |",
                "ID", "Name", "Age", "Grade", "Email");
        return separator + "\n" + header + "\n" + separator;
    }

    public static String getTableFooter() {
        return "+" + "-".repeat(7) + "+" + "-".repeat(22) + "+"
                + "-".repeat(7) + "+" + "-".repeat(10) + "+" + "-".repeat(27) + "+";
    }
}
