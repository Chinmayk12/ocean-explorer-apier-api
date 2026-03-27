# 🌊 Ocean Explorer API

![Java](https://img.shields.io/badge/Java-21-orange.svg) 
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)
![Build](https://img.shields.io/badge/build-passing-success)

A production-ready Spring Boot REST API built to simulate and control a **Submersible Probe** exploring the ocean floor. The application safely navigates a 2D grid, parsing movement commands while strictly enforcing boundary limits and avoiding obstacles.

---

## 🎯 Core Features

- **Stateful Navigation**: Tracks the probe's starting position, current facing direction, and an ordered list of all visited coordinates.
- **Physical Constraints**: Implements hard boundaries (e.g., probe cannot fall off the grid) and obstacle avoidance (e.g., probe halts if path is blocked).
- **Fail-Fast Validation**: Incoming API requests are strictly validated using Bean Validation annotations. Invalid grids or commands are rejected immediately with structured `400 Bad Request` JSON maps.
- **Clean Architecture**: Strong boundary enforcement between Web Controllers, Service Orchestrators, and Core Domain Models (Grid, Probe, Position).
- **100% Test Coverage Strategy**: Validated through extensive Unit and Integration Testing (`MockMvc`) handling edge cases.

---

## 🚀 How to Run Locally

You do **not** need Docker to run this project. It is built to run cleanly right out of the box using the Maven Wrapper.

### Prerequisites
- **JDK 21** installed and configured in your `JAVA_HOME`.

### 1. Build the Project
Open your terminal at the root of the project and build the executable JAR:
```bash
./mvnw clean package
```

### 2. Start the Server
Run the built application:
```bash
java -jar target/*.jar
```
*(Alternatively, you can just run `./mvnw spring-boot:run`)*

The API will start locally on port `8080`.

---

## 📡 API Reference

### Move Probe Endpoint
Processes a sequence of commands and returns the probe's journey timeline.

`POST /api/probe/move`

**Headers:**
`Content-Type: application/json`

**Example Request:**
```json
{
  "startX": 0,
  "startY": 0,
  "direction": "NORTH",
  "commands": "FFRFF",
  "gridSize": 5,
  "obstacles": [
    [1, 2],
    [2, 2]
  ]
}
```

#### Request Fields Specification:
| Field | Type | Required | Description |
|---|---|---|---|
| `startX` | `Integer` | ✅ | Starting X coordinate (must be $\ge$ 0) |
| `startY` | `Integer` | ✅ | Starting Y coordinate (must be $\ge$ 0) |
| `direction`| `String` | ✅ | Initial facing direction: `NORTH`, `SOUTH`, `EAST`, `WEST` |
| `commands` | `String` | ✅ | Movement string containing only: `F` (Forward), `B` (Backward), `L` (Turn Left), `R` (Turn Right) |
| `gridSize` | `Integer` | ✅ | Dimension of the square grid (e.g., `5` creates a 5x5 grid from 0,0 to 4,4) |
| `obstacles`| `int[][]` | ❌ | Array of pairs representing blocked coordinates: `[[X1,Y1], [X2,Y2]]` |

**Example Response (200 OK):**
```json
{
    "finalX": 0,
    "finalY": 2,
    "direction": "EAST",
    "visited": [
        [0, 0],
        [0, 1],
        [0, 2]
    ]
}
```
*Notice in the example above, the probe stopped at `[0,2]` and dropped the final two forward commands because coordinate `[1,2]` was blocked by an obstacle!*

---

## 🛑 Error Handling

The application provides developer-friendly error arrays if validations fail.

**Example: Sending invalid commands or an invalid direction text**
```json
{
    "timestamp": "2026-03-28T01:13:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed",
    "details": [
        "direction: direction must be one of: NORTH, SOUTH, EAST, WEST",
        "commands: commands must only contain characters: F, B, L, R"
    ]
}
```

---

## 🏗️ Architecture & Design Decisions

This repository adheres strictly to **SOLID Principles**:

1. **Single Responsibility (SRP)**:
   - `Grid`: Only handles borders and obstacles.
   - `Probe`: Only calculates coordinates and turn states.
   - `GlobalExceptionHandler`: Isolates all messy HTTP status code logic out of the controllers.
2. **Immutability**:
   Core domain objects like `Position` are strictly immutable. Moving the probe does not overwrite the old coordinate; it returns a new mathematical coordinate, preventing unintended state-mutation bugs.
3. **Defensive Coding**: 
   The probe's visited timeline is exposed as an `unmodifiableList`. External services cannot accidentally wipe or tamper with the history logs.

---

## 🧪 Testing

The codebase includes over **110+ passing test cases**, split into:
- **Domain Unit Tests**: Ensures core engine rules (e.g., turning left from North equals West) calculate perfectly without Spring overhead.
- **Integration Tests**: Employs `@WebMvcTest` to shoot mock JSON requests through the Spring DispatcherServlet to verify JSON parsing and Exception matching.

**Run the tests via:**
```bash
./mvnw test
```

---

## ☁️ Deployment (Ready for Render)

This application is configured for seamless deployment to "Platform as a Service" providers like **Render.com**. 

The configuration `server.port=${PORT:8080}` is explicitly set in `application.properties` so the web server automatically binds to whatever dynamic port Render provides in the environment.

**Steps to Deploy to Render:**
1. Connect this GitHub repository.
2. Select **Web Service**.
3. Build Command: `./mvnw clean package`
4. Start Command: `java -jar target/*.jar`
