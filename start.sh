#!/bin/bash
# Start script for Render deployment
# This script is executed to start the application on Render
# Note: For Docker deployments, this script may not be used (CMD in Dockerfile is used instead)
# Documentation: https://render.com/docs/deploy-spring-boot

set -e  # Exit immediately if a command exits with a non-zero status
set -o pipefail  # Catch errors in pipelines

echo "=========================================="
echo "🚀 Starting Task Management API"
echo "=========================================="

# Display startup information
echo "📊 Startup Information:"
echo "  Date: $(date)"
echo "  PWD: $(pwd)"
echo "  User: $(whoami)"
echo ""

# Display environment info (without sensitive data)
echo "🔧 Environment Configuration:"
echo "  PORT: ${PORT:-8080}"
echo "  SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE:-prod}"
echo "  DB_HOST: ${DB_HOST:-not-set}"
echo "  DB_NAME: ${DB_NAME:-not-set}"
echo "  DB_USER: ${DB_USER:-not-set}"
echo "  FILE_UPLOAD_DIR: ${FILE_UPLOAD_DIR:-/var/data/uploads}"
echo ""

# Verify database connectivity (optional, but helpful)
if command -v psql &> /dev/null && [ -n "$DB_HOST" ]; then
    echo "🔍 Testing database connectivity..."
    # This is just a check, actual connection happens in the app
    echo "  Database host is configured: $DB_HOST"
fi

# Create uploads directory if it doesn't exist
UPLOAD_DIR="${FILE_UPLOAD_DIR:-/var/data/uploads}"
mkdir -p "$UPLOAD_DIR"
echo "✅ Uploads directory ready: $UPLOAD_DIR"
echo ""

# Find the JAR file (more flexible than hardcoding)
JAR_FILE=$(find target -name "*.jar" -not -name "*-sources.jar" -not -name "*-javadoc.jar" 2>/dev/null | head -n 1)

if [ ! -f "$JAR_FILE" ]; then
    echo "❌ ERROR: JAR file not found!"
    echo "Searching in target directory..."
    ls -la target/ || echo "Target directory not found"
    exit 1
fi

echo "📦 Starting application from: $JAR_FILE"
echo ""

# JVM Configuration
# Using JAVA_TOOL_OPTIONS for better container support
export JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:--XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom -Duser.timezone=UTC}"

echo "☕ JVM Configuration:"
echo "  JAVA_TOOL_OPTIONS: $JAVA_TOOL_OPTIONS"
echo ""

echo "=========================================="
echo "🎯 Application starting..."
echo "=========================================="
echo ""

# Start the application with exec to replace the shell process
# This ensures proper signal handling (important for graceful shutdown)
exec java \
    -Dserver.port="${PORT:-8080}" \
    -Dspring.profiles.active="${SPRING_PROFILES_ACTIVE:-prod}" \
    -jar "$JAR_FILE"
