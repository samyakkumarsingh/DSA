import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Manages problem data with file-based persistence.
 */
public class ProblemStore {
    private final List<Problem> problems;
    private final String dataFile;
    private int nextId;

    public ProblemStore(String dataFile) {
        this.dataFile = dataFile;
        this.problems = new ArrayList<>();
        this.nextId = 1;
        loadFromFile();
    }

    public synchronized Problem addProblem(String title, String topic, String difficulty,
                                           String status, String notes, String link) {
        Problem problem = new Problem(nextId++, title, topic, difficulty, status, notes, link);
        problems.add(problem);
        saveToFile();
        return problem;
    }

    public synchronized List<Problem> getAllProblems() {
        return new ArrayList<>(problems);
    }

    public synchronized Optional<Problem> findById(int id) {
        return problems.stream().filter(p -> p.getId() == id).findFirst();
    }

    public synchronized boolean updateProblem(int id, String title, String topic,
                                              String difficulty, String status,
                                              String notes, String link) {
        Optional<Problem> existing = findById(id);
        if (existing.isPresent()) {
            Problem p = existing.get();
            if (title != null && !title.isEmpty()) p.setTitle(title);
            if (topic != null && !topic.isEmpty()) p.setTopic(topic);
            if (difficulty != null && !difficulty.isEmpty()) p.setDifficulty(difficulty);
            if (status != null && !status.isEmpty()) p.setStatus(status);
            if (notes != null) p.setNotes(notes);
            if (link != null) p.setLink(link);
            saveToFile();
            return true;
        }
        return false;
    }

    public synchronized boolean deleteProblem(int id) {
        boolean removed = problems.removeIf(p -> p.getId() == id);
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    /**
     * Returns statistics as a JSON string.
     */
    public synchronized String getStatsJson() {
        long total = problems.size();
        long solved = problems.stream().filter(p -> "Solved".equalsIgnoreCase(p.getStatus())).count();
        long attempted = problems.stream().filter(p -> "Attempted".equalsIgnoreCase(p.getStatus())).count();
        long pending = problems.stream().filter(p -> "Pending".equalsIgnoreCase(p.getStatus())).count();
        long easy = problems.stream().filter(p -> "Easy".equalsIgnoreCase(p.getDifficulty())).count();
        long medium = problems.stream().filter(p -> "Medium".equalsIgnoreCase(p.getDifficulty())).count();
        long hard = problems.stream().filter(p -> "Hard".equalsIgnoreCase(p.getDifficulty())).count();

        return "{\"total\":" + total
                + ",\"solved\":" + solved
                + ",\"attempted\":" + attempted
                + ",\"pending\":" + pending
                + ",\"easy\":" + easy
                + ",\"medium\":" + medium
                + ",\"hard\":" + hard + "}";
    }

    /**
     * Returns all problems as a JSON array string.
     */
    public synchronized String toJsonArray() {
        return "[" + problems.stream()
                .map(Problem::toJson)
                .collect(Collectors.joining(",")) + "]";
    }

    @SuppressWarnings("unchecked")
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile))) {
            oos.writeInt(nextId);
            oos.writeObject(problems);
        } catch (IOException e) {
            System.err.println("Warning: Could not save data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        File file = new File(dataFile);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            nextId = ois.readInt();
            List<Problem> loaded = (List<Problem>) ois.readObject();
            problems.addAll(loaded);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Warning: Could not load data: " + e.getMessage());
        }
    }
}
