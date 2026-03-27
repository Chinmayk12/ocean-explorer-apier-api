# 🌊 Ocean Explorer API

Welcome to the **Ocean Explorer API**! This project provides a fully-functional robust REST backend to navigate and simulate deep-sea probes.

---

## 🎮 What is this game / application?

The Ocean Explorer acts as a command center for a high-tech submersible probe exploring the bottom of the ocean. 

Imagine a 2D grid representing the ocean floor. You can deploy a probe at a specific starting coordinate (`X`, `Y`) facing a specific direction (`NORTH`, `SOUTH`, `EAST`, `WEST`). 

Once deployed, you can send it a sequence of navigation commands:
*   `F` - Move **Forward** 
*   `B` - Move **Backward**
*   `L` - Turn **Left** (90 degrees, in place)
*   `R` - Turn **Right** (90 degrees, in place)

### 🛑 The Challenge:
1.  **Grid Boundaries:** The ocean floor grid has finite limits (e.g., 5x5). The probe cannot fall off the grid. If instructed to move out of bounds, it simply stays in place and ignores that current move command.
2.  **Obstacles:** You can define obstacles (like shipwrecks or deep-sea trenches) on specific grid coordinates. If a probe's next move hits an obstacle, it immediately stops moving towards that path but can still execute subsequent commands (like turning and moving elsewhere).

This API takes your initial setup, processes the entire string of commands sequentially, and returns exactly where the probe ended up and identically maps the coordinates it successfully visited!

---

## 🛠️ Tech Stack
- **Java 21**
- **Spring Boot 3.x**
- **Maven**
- **JUnit 5 / MockMvc / Mockito** (for robust unit & integration testing)

---

## 🚀 How to Run the Application End-to-End

### Step 1: Prerequisites
Ensure you have the following installed on your machine:
- **Java 21 JDK** installed and configured in your environment variables.
- **Maven** (or you can use the `mvnw` wrapper included in the repo).

### Step 2: Running the Spring Boot Server
1. Open your terminal or command prompt.
2. Navigate to the root folder of this project (the folder containing `pom.xml`, `mvnw`, and the `src` directory).
3. Start the application by running the following command:
   ```bash
   # On Windows
   mvnw.cmd spring-boot:run
   
   # On Mac/Linux
   ./mvnw spring-boot:run
   ```
4. The embedded Tomcat server will spin up locally. Look for a log line indicating `Tomcat started on port(s): 8080 (http)`. That means your application is successfully up and running!

### Step 3: Running the Tests (Recommended)
This API is built with High Coverage test suites validating edge cases, validations, and controller flows. To trigger tests:
```bash
./mvnw test
```

---

## 📬 API Reference: What Data to Pass

The application exposes a single, powerful endpoint to dictate your probe's exploration.

### `POST /api/probe/move`
Sends a payload defining grid configurations, starting placement, and commands to simulate the probe.

**Headers Required**:
*   `Content-Type: application/json`

**What Data to Pass (JSON Request Body Guidelines)**:
Here's a breakdown of every piece of data you should input:

| Field | Type | Description |
|---|---|---|
| `startX` | Integer | The initial X coordinate of the probe (Starts at `0`) |
| `startY` | Integer | The initial Y coordinate of the probe (Starts at `0`) |
| `direction` | String | Initial facing direction. Must be one of: `NORTH`, `SOUTH`, `EAST`, `WEST` |
| `commands` | String | A sequence of movement/turning commands. Only `F`, `B`, `L`, `R` (case-insensitive) are allowed. e.g., `FFRFF` |
| `gridSize` | Integer | The size of the N x N bounds (e.g., passing `5` creates a grid with coordinates 0 to 4 in both x and y). |
| `obstacles` | int[][] | (Optional) Coordinates of blocked areas. e.g., `[[1, 2], [2, 2]]` |

---

## 📝 Example Uses (End to End)

Use [Postman](https://www.postman.com/), [Insomnia](https://insomnia.rest/), or `curl` to send these requests to your running server.

### Scenario 1: Standard Movement Open Ocean
**Request (cURL):**
```bash
curl -X POST http://localhost:8080/api/probe/move \
-H "Content-Type: application/json" \
-d '{
  "startX": 0,
  "startY": 0,
  "direction": "NORTH",
  "commands": "FFRFF",
  "gridSize": 5,
  "obstacles": []
}'
```

**Step-by-step Execution of Commands (`FFRFF`):**
1. Starts at `(0,0)` facing `NORTH`
2. `F` -> moves North to `(0, 1)`
3. `F` -> moves North to `(0, 2)`
4. `R` -> rotates Right, now facing `EAST` at `(0, 2)`
5. `F` -> moves East to `(1, 2)`
6. `F` -> moves East to `(2, 2)`

**Response:**
```json
{
  "finalX": 2,
  "finalY": 2,
  "direction": "EAST",
  "visited": [
    [0, 0],
    [0, 1],
    [0, 2],
    [1, 2],
    [2, 2]
  ]
}
```

### Scenario 2: Encountering an Obstacle
Let's see what happens if an obstacle lies directly in its path.
**Request:**
```bash
curl -X POST http://localhost:8080/api/probe/move \
-H "Content-Type: application/json" \
-d '{
  "startX": 0,
  "startY": 0,
  "direction": "EAST",
  "commands": "FFFF",
  "gridSize": 5,
  "obstacles": [[2, 0]]
}'
```

**Step-by-step Execution of Commands (`FFFF`):**
1. Starts at `(0,0)` facing `EAST`.
2. `F` -> moves East to `(1,0)`
3. `F` -> path to `(2,0)` is **BLOCKED** by an obstacle! The probe stays at `(1,0)`.
4. `F` -> path to `(2,0)` is still blocked. The probe stays at `(1,0)`.
5. `F` -> path to `(2,0)` is still blocked. The probe stays at `(1,0)`.

**Response:**
```json
{
  "finalX": 1,
  "finalY": 0,
  "direction": "EAST",
  "visited": [
    [0, 0],
    [1, 0]
  ]
}
```

---

## 🏗️ Architecture and Design Principles Used

To ensure readability, robustness, and scalability, the underlying code encompasses professional enterprise principles:

*   **Immutable Domain Objects:** `Position` objects are fully immutable, ensuring thread safety and preventing accidental mutation side effects across the service layers.
*   **Encapsulation & Exhaustive Enums:** The `Direction` enum inherently owns the logic to compute left/right turns and calculate future X/Y axis coordinate shifts. This removes the need for large messy nested `if-else` business logic blocks.
*   **Decoupled Controller/Service Layer:** `ProbeService` handles pure domain orchestration while `ProbeController` strictly manages parsing and REST mapping.
*   **Robust Pre-Validation Layer:** Using standard `jakarta.validation` (`@Valid`), the controller catches and clearly identifies bad input strings, improper start coordinates, and out-of-grid obstacles immediately before wasting server load engaging the business logic.
*   **Global Exception Handling:** Centralized `@ControllerAdvice` processes any throwables cleanly into customized JSON error schemas ensuring the frontend consumer never sees an ugly Java stack-trace. 

🌊 *Happy Exploring!* 🌊
