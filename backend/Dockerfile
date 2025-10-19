# ==============================
# Stage 1: Build the Spring Boot app
# ==============================
FROM eclipse-temurin:25-jdk-alpine AS build

# Set working directory
WORKDIR /app

# Install Maven Wrapper permissions (optional safety)
RUN apk add --no-cache bash curl

# Copy Maven wrapper and pom.xml first for dependency caching
COPY pom.xml mvnw* ./
COPY .mvn .mvn

# Pre-download dependencies (cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the Spring Boot JAR, skip tests to speed up CI/CD
RUN ./mvnw clean package -DskipTests

# ==============================
# Stage 2: Run the Spring Boot app
# ==============================
FROM eclipse-temurin:25-jre-alpine

# Create a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Set working directory
WORKDIR /app

# Copy built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Set environment variables
ENV JAVA_OPTS="-Xms128m -Xmx512m"
ENV TZ=Asia/Kolkata

# Use non-root user
USER spring

# Start the Spring Boot app
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
