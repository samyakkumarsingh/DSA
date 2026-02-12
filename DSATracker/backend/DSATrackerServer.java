import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * DSA Tracker - Java HTTP Server
 * Serves the frontend files and provides REST API for problem tracking.
 */
public class DSATrackerServer {
    private static final int PORT = 8080;
    private static final ProblemStore store = new ProblemStore("problems.dat");
    private static String frontendDir;

    public static void main(String[] args) throws IOException {
        // Determine frontend directory relative to backend
        frontendDir = Paths.get(System.getProperty("user.dir"), "..", "frontend")
                .normalize().toAbsolutePath().toString();

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // API endpoints
        server.createContext("/api/problems", DSATrackerServer::handleProblems);
        server.createContext("/api/stats", DSATrackerServer::handleStats);

        // Static file serving
        server.createContext("/", DSATrackerServer::handleStaticFiles);

        server.setExecutor(null);
        server.start();
        System.out.println("DSA Tracker Server started at http://localhost:" + PORT);
        System.out.println("Serving frontend from: " + frontendDir);
    }

    private static void handleProblems(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            switch (method) {
                case "GET":
                    handleGetProblems(exchange);
                    break;
                case "POST":
                    handleAddProblem(exchange);
                    break;
                case "PUT":
                    handleUpdateProblem(exchange, path);
                    break;
                case "DELETE":
                    handleDeleteProblem(exchange, path);
                    break;
                default:
                    sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }

    private static void handleGetProblems(HttpExchange exchange) throws IOException {
        String json = store.toJsonArray();
        sendResponse(exchange, 200, json);
    }

    private static void handleAddProblem(HttpExchange exchange) throws IOException {
        Map<String, String> data = parseJsonBody(exchange);
        Problem problem = store.addProblem(
                data.getOrDefault("title", ""),
                data.getOrDefault("topic", ""),
                data.getOrDefault("difficulty", "Easy"),
                data.getOrDefault("status", "Pending"),
                data.getOrDefault("notes", ""),
                data.getOrDefault("link", "")
        );
        sendResponse(exchange, 201, problem.toJson());
    }

    private static void handleUpdateProblem(HttpExchange exchange, String path) throws IOException {
        int id = extractIdFromPath(path);
        if (id == -1) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid problem ID\"}");
            return;
        }
        Map<String, String> data = parseJsonBody(exchange);
        boolean updated = store.updateProblem(id,
                data.get("title"), data.get("topic"),
                data.get("difficulty"), data.get("status"),
                data.get("notes"), data.get("link"));
        if (updated) {
            sendResponse(exchange, 200, store.findById(id).get().toJson());
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Problem not found\"}");
        }
    }

    private static void handleDeleteProblem(HttpExchange exchange, String path) throws IOException {
        int id = extractIdFromPath(path);
        if (id == -1) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid problem ID\"}");
            return;
        }
        if (store.deleteProblem(id)) {
            sendResponse(exchange, 200, "{\"message\":\"Problem deleted\"}");
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Problem not found\"}");
        }
    }

    private static void handleStats(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        sendResponse(exchange, 200, store.getStatsJson());
    }

    private static void handleStaticFiles(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if ("/".equals(path)) path = "/index.html";

        // Resolve and validate the file path to prevent directory traversal
        Path basePath = Paths.get(frontendDir).normalize();
        Path filePath = basePath.resolve(path.substring(1)).normalize();

        if (!filePath.startsWith(basePath)) {
            sendResponse(exchange, 403, "Forbidden");
            return;
        }

        File file = filePath.toFile();
        if (!file.exists() || !file.isFile()) {
            sendResponse(exchange, 404, "Not Found");
            return;
        }

        String contentType = getContentType(path);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        byte[] fileBytes = Files.readAllBytes(filePath);
        exchange.sendResponseHeaders(200, fileBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(fileBytes);
        }
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String body)
            throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = body.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private static int extractIdFromPath(String path) {
        // Expected path: /api/problems/{id}
        String[] parts = path.split("/");
        if (parts.length >= 4) {
            try {
                return Integer.parseInt(parts[3]);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    /**
     * Simple JSON parser for flat key-value objects.
     */
    private static Map<String, String> parseJsonBody(HttpExchange exchange) throws IOException {
        Map<String, String> result = new HashMap<>();
        String body;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), "UTF-8"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            body = sb.toString().trim();
        }

        if (body.startsWith("{") && body.endsWith("}")) {
            body = body.substring(1, body.length() - 1);
            String[] pairs = body.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            for (String pair : pairs) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2) {
                    String key = kv[0].trim().replaceAll("^\"|\"$", "");
                    String value = kv[1].trim().replaceAll("^\"|\"$", "");
                    result.put(key, value);
                }
            }
        }
        return result;
    }

    private static String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".svg")) return "image/svg+xml";
        if (path.endsWith(".ico")) return "image/x-icon";
        return "application/octet-stream";
    }
}
