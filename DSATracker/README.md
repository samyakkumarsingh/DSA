# DSA Tracker

A web-based DSA (Data Structures & Algorithms) problem tracker with a **Java backend** and **HTML/CSS/JavaScript frontend**. Track your problem-solving progress across different topics and difficulty levels.

## Features

- **Dashboard** – Visual stats showing total, solved, attempted, and pending problems
- **Difficulty Breakdown** – Track Easy, Medium, and Hard problems separately
- **Add Problems** – Log problems with title, topic, difficulty, status, notes, and link
- **Filter & Search** – Filter problems by topic, difficulty, or status
- **Status Toggle** – Quickly cycle through Pending → Attempted → Solved
- **Delete Problems** – Remove problems you no longer want to track
- **Persistent Storage** – Data is saved to disk and survives server restarts
- **Responsive Design** – Works on desktop and mobile

## Tech Stack

- **Frontend:** HTML5, CSS3, JavaScript (Vanilla)
- **Backend:** Java (built-in `com.sun.net.httpserver.HttpServer`)
- **Persistence:** Java Serialization (file-based)
- **No external dependencies** – Uses only Java standard library

## How to Run

### Prerequisites
- Java JDK 11 or higher

### Start the Server

```bash
cd DSATracker/backend
javac *.java
java DSATrackerServer
```

The server starts at **http://localhost:8080**. Open this URL in your browser to use the DSA Tracker.

## Project Structure

```
DSATracker/
├── backend/
│   ├── DSATrackerServer.java   # HTTP server with REST API
│   ├── Problem.java            # Problem model class
│   └── ProblemStore.java       # Data persistence layer
├── frontend/
│   ├── index.html              # Main dashboard page
│   ├── style.css               # Styles and responsive design
│   └── script.js               # Frontend logic and API calls
└── README.md
```

## API Endpoints

| Method | Endpoint             | Description          |
|--------|----------------------|----------------------|
| GET    | `/api/problems`      | Get all problems     |
| POST   | `/api/problems`      | Add a new problem    |
| PUT    | `/api/problems/{id}` | Update a problem     |
| DELETE | `/api/problems/{id}` | Delete a problem     |
| GET    | `/api/stats`         | Get dashboard stats  |
