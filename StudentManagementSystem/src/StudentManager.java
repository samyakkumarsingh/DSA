import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manages student records with CRUD operations and file-based persistence.
 */
public class StudentManager {
    private final List<Student> students;
    private final String dataFile;
    private int nextId;

    public StudentManager(String dataFile) {
        this.dataFile = dataFile;
        this.students = new ArrayList<>();
        this.nextId = 1;
        loadFromFile();
    }

    /**
     * Adds a new student to the system.
     */
    public Student addStudent(String name, int age, String grade, String email) {
        Student student = new Student(nextId++, name, age, grade, email);
        students.add(student);
        saveToFile();
        return student;
    }

    /**
     * Returns all students in the system.
     */
    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    /**
     * Finds a student by their ID.
     */
    public Optional<Student> findById(int id) {
        return students.stream().filter(s -> s.getId() == id).findFirst();
    }

    /**
     * Searches students by name (case-insensitive partial match).
     */
    public List<Student> searchByName(String name) {
        String lowerName = name.toLowerCase();
        List<Student> results = new ArrayList<>();
        for (Student s : students) {
            if (s.getName().toLowerCase().contains(lowerName)) {
                results.add(s);
            }
        }
        return results;
    }

    /**
     * Updates an existing student's information.
     * Returns true if the student was found and updated.
     */
    public boolean updateStudent(int id, String name, int age, String grade, String email) {
        Optional<Student> studentOpt = findById(id);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            if (name != null && !name.isEmpty()) student.setName(name);
            if (age > 0) student.setAge(age);
            if (grade != null && !grade.isEmpty()) student.setGrade(grade);
            if (email != null && !email.isEmpty()) student.setEmail(email);
            saveToFile();
            return true;
        }
        return false;
    }

    /**
     * Deletes a student by their ID.
     * Returns true if the student was found and deleted.
     */
    public boolean deleteStudent(int id) {
        boolean removed = students.removeIf(s -> s.getId() == id);
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    /**
     * Returns the total number of students.
     */
    public int getStudentCount() {
        return students.size();
    }

    /**
     * Saves all student data to a file using Java serialization.
     */
    @SuppressWarnings("unchecked")
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile))) {
            oos.writeInt(nextId);
            oos.writeObject(students);
        } catch (IOException e) {
            System.err.println("Warning: Could not save data to file: " + e.getMessage());
        }
    }

    /**
     * Loads student data from file.
     */
    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        File file = new File(dataFile);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            nextId = ois.readInt();
            List<Student> loaded = (List<Student>) ois.readObject();
            students.addAll(loaded);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Warning: Could not load data from file: " + e.getMessage());
        }
    }
}
