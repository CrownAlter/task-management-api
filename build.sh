#!/bin/bash
# Build script for Render deployment
# This script is executed during the build phase on Render
# Documentation: https://render.com/docs/deploy-spring-boot

set -e  # Exit immediately if a command exits with a non-zero status
set -o pipefail  # Catch errors in pipelines

echo "=========================================="
echo "🚀 Starting Build Process"
echo "=========================================="

# Display system information
echo "📊 System Information:"
echo "  Date: $(date)"
echo "  PWD: $(pwd)"
echo "  User: $(whoami)"

# Display Java version
echo ""
echo "☕ Java Version:"
java -version 2>&1 | head -n 3

# Display Maven version
echo ""
echo "📦 Maven Version:"
mvn -version 2>&1 | head -n 3

# Set Maven options for better performance and less verbose output
export MAVEN_OPTS="-Xmx1024m -XX:+UseG1GC -Djava.awt.headless=true"

# Clean and build the application
echo ""
echo "🔨 Building application with Maven..."
echo "  - Skipping tests (run separately for faster builds)"
echo "  - Using batch mode for CI/CD"
echo ""

./mvnw clean package -DskipTests -B \
  -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn \
  || mvn clean package -DskipTests -B \
  -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

# Verify JAR was created (find any JAR file, not hardcoded name)
echo ""
echo "🔍 Verifying build artifacts..."

JAR_FILE=$(find target -name "*.jar" -not -name "*-sources.jar" -not -name "*-javadoc.jar" | head -n 1)

if [ -f "$JAR_FILE" ]; then
    echo "✅ Build successful! JAR file created:"
    ls -lh "$JAR_FILE"
    echo ""
    echo "📝 JAR Details:"
    echo "  File: $JAR_FILE"
    echo "  Size: $(du -h "$JAR_FILE" | cut -f1)"
else
    echo "❌ Build failed! JAR file not found in target directory."
    echo "Contents of target directory:"
    ls -la target/ || echo "Target directory not found"
    exit 1
fi

# Optional: Display dependencies tree (commented out to reduce build time)
# echo ""
# echo "📋 Dependency Tree:"
# mvn dependency:tree -B

echo ""
echo "=========================================="
echo "✅ Build process completed successfully!"
echo "=========================================="
