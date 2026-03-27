# -----------------------------
# Stage 1: Build the application
# -----------------------------
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first (for caching)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster build)
RUN ./mvnw clean package -DskipTests


# -----------------------------
# Stage 2: Run the application
# -----------------------------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy built jar from builder
COPY --from=builder /app/target/*.jar app.jar

# Expose port (Render will set PORT env var dynamically)
EXPOSE 8080

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]
