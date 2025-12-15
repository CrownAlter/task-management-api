#!/bin/bash
# Build script for Render deployment
# This script is executed during the build phase

set -e  # Exit on error

echo "=========================================="
echo "Starting Build Process for Task Management API"
echo "=========================================="

# Display Java version
echo "Java Version:"
java -version

# Display Maven version
echo "Maven Version:"
mvn -version

# Clean and build the application
echo "Building application with Maven..."
mvn clean package -DskipTests -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

# Verify JAR was created
if [ -f target/task-management-api-0.0.1-SNAPSHOT.jar ]; then
    echo "✅ Build successful! JAR file created."
    ls -lh target/task-management-api-0.0.1-SNAPSHOT.jar
else
    echo "❌ Build failed! JAR file not found."
    exit 1
fi

echo "=========================================="
echo "Build process completed successfully!"
echo "=========================================="
