# ==============================
# Stage 1: Build the Spring Boot app
# ==============================
FROM eclipse-temurin:25-jdk-alpine AS build

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first for caching dependencies
COPY pom.xml mvnw* ./
COPY .mvn .mvn

# Download dependencies only
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the Spring Boot JAR, skip tests to speed up
RUN ./mvnw clean package -DskipTests

# ==============================
# Stage 2: Run the Spring Boot app
# ==============================
FROM eclipse-temurin:25-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Optional: Set Java memory options
ENV JAVA_OPTS="-Xms128m -Xmx512m"

# Run the JAR
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
