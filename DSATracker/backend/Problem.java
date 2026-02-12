import java.io.Serializable;

/**
 * Represents a DSA problem being tracked.
 */
public class Problem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String topic;
    private String difficulty;
    private String status;
    private String notes;
    private String link;

    public Problem(int id, String title, String topic, String difficulty,
                   String status, String notes, String link) {
        this.id = id;
        this.title = title;
        this.topic = topic;
        this.difficulty = difficulty;
        this.status = status;
        this.notes = notes;
        this.link = link;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    /**
     * Converts this problem to a JSON string.
     */
    public String toJson() {
        return "{"
                + "\"id\":" + id + ","
                + "\"title\":\"" + escapeJson(title) + "\","
                + "\"topic\":\"" + escapeJson(topic) + "\","
                + "\"difficulty\":\"" + escapeJson(difficulty) + "\","
                + "\"status\":\"" + escapeJson(status) + "\","
                + "\"notes\":\"" + escapeJson(notes) + "\","
                + "\"link\":\"" + escapeJson(link) + "\""
                + "}";
    }

    private static String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                     .replace("\"", "\\\"")
                     .replace("\n", "\\n")
                     .replace("\r", "\\r")
                     .replace("\t", "\\t");
    }
}
