#!/bin/bash
# Start script for Render deployment
# This script is executed to start the application

set -e  # Exit on error

echo "=========================================="
echo "Starting Task Management API"
echo "=========================================="

# Display environment info
echo "PORT: $PORT"
echo "SPRING_PROFILES_ACTIVE: $SPRING_PROFILES_ACTIVE"
echo "DB_HOST: $DB_HOST"
echo "DB_NAME: $DB_NAME"

# Create uploads directory if it doesn't exist
mkdir -p ${FILE_UPLOAD_DIR:-/opt/render/project/src/uploads}
echo "✅ Uploads directory ready: ${FILE_UPLOAD_DIR:-/opt/render/project/src/uploads}"

# Start the application with optimized JVM settings
exec java \
    -Dserver.port=${PORT:-8080} \
    -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} \
    ${JAVA_OPTS:--Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200} \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+UseStringDeduplication \
    -Djava.security.egd=file:/dev/./urandom \
    -jar target/task-management-api-0.0.1-SNAPSHOT.jar
