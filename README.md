# 🌊 Ocean Explorer API

![Java](https://img.shields.io/badge/Java-21-orange.svg) 
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)
![Build](https://img.shields.io/badge/build-passing-success)

A production-ready Spring Boot REST API built to simulate and control a **Submersible Probe** exploring the ocean floor. The application safely navigates a 2D grid, parsing movement commands while strictly enforcing boundary limits and avoiding obstacles.

---

## 📖 Problem Statement & Rules
The probe operates within a 2D grid (X, Y) with a configurable size and faces one of four directions (`NORTH`, `SOUTH`, `EAST`, `WEST`). It accepts a sequence of movement commands (`F` for forward, `B` for backward, `L` to turn left, `R` to turn right).

**System Behaviors:**
- **Boundary Control**: Prevents the probe from moving outside the predefined grid edges.
- **Obstacle Avoidance**: Halts movement gracefully if the path overlaps with a known blocked coordinate (obstacle).
- **State Tracking**: Logs and returns all visited positions along with the final coordinate and direction.

---

## 🎯 Core Features

- **Stateful Navigation**: Tracks the probe's starting position, current facing direction, and an ordered list of all visited coordinates.
- **Fail-Fast Validation**: Incoming API requests are strictly validated using `jakarta.validation` rules. Invalid grids or commands are rejected immediately with structured `400 Bad Request` messages.
- **Clean Architecture & SOLID Principles**: Strong boundaries and dependency inversion between Web Controllers, Service Orchestrators, and Domain Models (`Grid`, `Probe`, `Position`).
- **Swagger Documentation**: Integrated `springdoc-openapi` for visual API exploration. 
- **Extensive Testing**: Codebase behaviors validated through Unit and Integration Testing (`JUnit 5`, `MockMvc`).

---

## 🏗️ Architecture & Design Decisions

The application flows cleanly through three main layers:
1. **Controller Layer (`ProbeController`)**: Takes HTTP POST requests, validates JSON bodies, and maps exceptions through a global `@RestControllerAdvice` error handler.
2. **Service Layer (`ProbeService`)**: Orchestrates the initial domain startup, processing the valid commands string safely.
3. **Domain Model Layer (`Probe`, `Position`, `Grid`)**: Pure Java object-oriented logic. 
    - `Position` is immutable.
    - `Direction` and `Command` are defined via strong **Enums**.

> **Immutability advantage**: Moving the probe algorithmically doesn't mutate existing position coordinates; it returns a new one, avoiding shared-state side-effects.

---

## 🚀 How to Run Locally

You do **not** need Docker to run this project. It runs right out of the box using the Maven Wrapper.

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
*(Alternatively, you can skip the build and just run `./mvnw spring-boot:run`)*

The API will start locally on port `8080`.

---

## 📡 API Reference

### Move Probe Endpoint
Processes a sequence of commands and returns the probe's journey timeline.

**URL**: `POST /api/probe/move`  
**Content-Type**: `application/json`

#### Request Payload
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
| `gridSize` | `Integer` | ✅ | Dimension of the square grid (e.g., `5` creates a grid from 0,0 to 4,4) |
| `obstacles`| `int[][]` | ❌ | Array of pairs representing blocked coordinates: `[[X1,Y1], [X2,Y2]]` |

#### Response (200 OK):
Returns the final probe position, direction, and step-by-step history trace.
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
*Note: The probe stopped early at `[0,2]` ignoring the final commands because coordinate `[1,2]` was blocked!*

---

## 📖 Swagger Interactive Docs

With the application running, view the interactive Open API Swagger documentation in your browser to test endpoints and read schemas visually:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 🛑 Global Error Handling

The application provides developer-friendly error collections if validations fail. Edge cases (invalid direction string, falling off the grid, empty commands matrix) are seamlessly handled.

**Example: Sending invalid commands or an incorrect direction code**
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

## 🧪 Testing

The codebase includes over **110+ passing JUnit 5 test cases** ensuring core movement, matrix turning logic, boundary conditions, and obstacle avoidance are robust.

**Run the tests via:**
```bash
./mvnw test
```

- **Domain Unit Tests**: Ensures core logic (like enum evaluations for turning from North to West) operates fully isolated from Spring.
- **Integration Tests**: Employs `@WebMvcTest` with Spring context for end-to-end `Controller -> Service` verification, checking input validation loops and exceptions.

---

## ☁️ Deployment (Render)

This application is production-ready and configured for deployment on **Render.com**.

### Deployment Configuration

**Render Settings:**
- **Type**: Web Service
- **Environment**: Docker
- **Health Check Path**: `/actuator/health`
- **Port**: Automatically configured via `PORT` environment variable

### Available Endpoints

Once deployed, your service will be available at:
- **Root**: `https://your-app.onrender.com/` - API information and available endpoints
- **Health Check**: `https://your-app.onrender.com/actuator/health` - Service health status
- **API Info**: `https://your-app.onrender.com/actuator/info` - Application metadata
- **Probe API**: `https://your-app.onrender.com/api/probe/move` - Main probe control endpoint

### Docker Deployment

The included `Dockerfile` uses a multi-stage build:
1. **Builder stage**: Compiles the application using Maven
2. **Runtime stage**: Runs the application with JRE only (smaller image)

The application automatically binds to the port provided by Render via the `PORT` environment variable.

### Manual Deployment Steps

1. Connect your GitHub repository to Render
2. Create a new Web Service
3. Select **Docker** as the environment
4. Set Health Check Path to `/actuator/health`
5. Deploy!

Render will automatically rebuild and redeploy on every push to your main branch.
