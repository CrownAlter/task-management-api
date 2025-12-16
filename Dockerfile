# Multi-stage Dockerfile for Task Management API
# Optimized for Render deployment with best practices

# Stage 1: Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS build

# Set working directory
WORKDIR /app

# Copy Maven wrapper for better caching
COPY .mvn .mvn
COPY mvnw mvnw.cmd ./
RUN chmod +x mvnw

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B || mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application with optimizations
RUN ./mvnw clean package -DskipTests -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn || \
    mvn clean package -DskipTests -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Metadata labels
LABEL maintainer="Task Management API Team"
LABEL version="1.0"
LABEL description="Multi-tenant Task Management API with Spring Boot"

# Install security updates and utilities
RUN apk update && \
    apk upgrade && \
    apk add --no-cache \
    dumb-init \
    curl \
    tzdata && \
    rm -rf /var/cache/apk/* && \
    # Set timezone to UTC
    cp /usr/share/zoneinfo/UTC /etc/localtime && \
    echo "UTC" > /etc/timezone

# Create non-root user for security (best practice)
RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

# Set working directory
WORKDIR /app

# Copy JAR from build stage with specific naming
COPY --from=build --chown=appuser:appuser /app/target/*.jar app.jar

# Create necessary directories with proper permissions
RUN mkdir -p /var/data/uploads /app/logs && \
    chown -R appuser:appuser /var/data /app

# Switch to non-root user (security best practice)
USER appuser

# Expose application port (documenting the port, Render will use $PORT)
EXPOSE 8080

# Health check (optimized intervals for faster startup detection)
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${PORT:-8080}/actuator/health || exit 1

# Use dumb-init to handle signals properly (PID 1 zombie reaping)
ENTRYPOINT ["dumb-init", "--"]

# Start application with optimized JVM flags
# Render will override PORT via environment variable
CMD ["sh", "-c", "java \
     -Dserver.port=${PORT:-8080} \
     -XX:+UseContainerSupport \
     -XX:MaxRAMPercentage=75.0 \
     -XX:InitialRAMPercentage=50.0 \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:ParallelGCThreads=2 \
     -XX:ConcGCThreads=1 \
     -XX:+UseStringDeduplication \
     -XX:+ExitOnOutOfMemoryError \
     -Djava.security.egd=file:/dev/./urandom \
     -Duser.timezone=UTC \
     -jar app.jar"]
