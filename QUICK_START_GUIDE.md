# Quick Start Guide - Task Management API

This guide will help you get the Task Management API up and running in minutes.

---

## 🚀 Prerequisites

- Java 17 or higher
- PostgreSQL 12+ installed and running
- Maven 3.6+
- Git

---

## 📥 Step 1: Setup Database

### Create Database
```sql
CREATE DATABASE task_management_db;
```

### Create User (Optional)
```sql
CREATE USER task_admin WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE task_management_db TO task_admin;
```

---

## 📦 Step 2: Clone and Configure

### Clone Repository
```bash
git clone <repository-url>
cd task-management-api
```

### Configure Database Connection
Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/task_management_db
    username: postgres  # or task_admin
    password: your_password
```

---

## 🏗️ Step 3: Build the Project

```bash
mvn clean install
```

This will:
- Download dependencies
- Compile the code
- Run Flyway migrations (create tables)
- Run tests
- Create executable JAR

---

## ▶️ Step 4: Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR directly:
```bash
java -jar target/task-management-api-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

---

## 🧪 Step 5: Test the API

### Option 1: Using Swagger UI
Open your browser and navigate to:
```
http://localhost:8080/swagger-ui.html
```

### Option 2: Using cURL

#### 1. Register a New User
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "SecurePass123!",
    "phone": "+1234567890",
    "tenantName": "Acme Corporation"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "user": {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      ...
    },
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer"
  }
}
```

#### 2. Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "SecurePass123!"
  }'
```

**Save the `accessToken` from the response!**

#### 3. Create a Task
```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Implement user authentication",
    "description": "Add JWT-based authentication to the application",
    "status": "TODO",
    "priority": "HIGH",
    "dueDate": "2024-12-31T23:59:59",
    "tags": "backend,security,authentication"
  }'
```

#### 4. Get All Tasks
```bash
curl -X GET "http://localhost:8080/api/v1/tasks?page=0&size=20" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

#### 5. Get Dashboard Statistics
```bash
curl -X GET http://localhost:8080/api/v1/dashboard/stats \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

---

## 📝 Common Use Cases

### Creating a Complete Task Workflow

#### 1. Create Task
```bash
TOKEN="your_access_token_here"

curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Fix login bug",
    "description": "Users cannot login with special characters",
    "status": "TODO",
    "priority": "URGENT",
    "dueDate": "2024-12-20T17:00:00",
    "tags": "bug,login,urgent"
  }'
```

#### 2. Add Comment to Task
```bash
curl -X POST http://localhost:8080/api/v1/tasks/1/comments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "I will start working on this issue today"
  }'
```

#### 3. Upload Attachment
```bash
curl -X POST http://localhost:8080/api/v1/tasks/1/attachments \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/screenshot.png"
```

#### 4. Update Task Status
```bash
curl -X PATCH "http://localhost:8080/api/v1/tasks/1/status?status=IN_PROGRESS" \
  -H "Authorization: Bearer $TOKEN"
```

#### 5. Complete Task
```bash
curl -X PATCH "http://localhost:8080/api/v1/tasks/1/status?status=COMPLETED" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🔍 Filtering Examples

### Get High Priority Incomplete Tasks
```bash
curl -X GET "http://localhost:8080/api/v1/tasks?priorities=HIGH,URGENT&completed=false" \
  -H "Authorization: Bearer $TOKEN"
```

### Get Overdue Tasks
```bash
curl -X GET "http://localhost:8080/api/v1/tasks?overdue=true" \
  -H "Authorization: Bearer $TOKEN"
```

### Search Tasks by Keyword
```bash
curl -X GET "http://localhost:8080/api/v1/tasks?search=authentication" \
  -H "Authorization: Bearer $TOKEN"
```

### Get My Assigned Tasks
```bash
curl -X GET "http://localhost:8080/api/v1/tasks/my-tasks?page=0&size=20" \
  -H "Authorization: Bearer $TOKEN"
```

### Get Tasks by Tag
```bash
curl -X GET "http://localhost:8080/api/v1/tasks?tags=backend" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 👥 User Management Examples

### Update Profile
```bash
curl -X PUT http://localhost:8080/api/v1/users/me \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Smith",
    "phone": "+1234567890"
  }'
```

### Change Password
```bash
curl -X POST http://localhost:8080/api/v1/users/me/change-password \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "SecurePass123!",
    "newPassword": "NewSecurePass456!",
    "confirmPassword": "NewSecurePass456!"
  }'
```

### Get User Statistics
```bash
curl -X GET http://localhost:8080/api/v1/users/1/statistics \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📊 Analytics Examples

### Get Dashboard Stats
```bash
curl -X GET http://localhost:8080/api/v1/dashboard/stats \
  -H "Authorization: Bearer $TOKEN"
```

### Get Task Analytics for Date Range
```bash
curl -X GET "http://localhost:8080/api/v1/dashboard/analytics?startDate=2024-01-01&endDate=2024-12-31" \
  -H "Authorization: Bearer $TOKEN"
```

### Get Personal Dashboard
```bash
curl -X GET http://localhost:8080/api/v1/dashboard/my-stats \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🎯 Testing with Postman

### Import Collection
1. Open Postman
2. Click "Import"
3. Use the Swagger URL: `http://localhost:8080/v3/api-docs`
4. Postman will create a collection with all endpoints

### Setup Environment Variables
Create environment variables in Postman:
- `baseUrl`: `http://localhost:8080`
- `accessToken`: (set after login)

### Auto-set Token
Add this to your login request's "Tests" tab:
```javascript
var jsonData = JSON.parse(responseBody);
pm.environment.set("accessToken", jsonData.data.accessToken);
```

---

## 🔧 Configuration Tips

### Change Server Port
Edit `application.yml`:
```yaml
server:
  port: 9090
```

### Enable Debug Logging
```yaml
logging:
  level:
    com.adewunmi.task_management_api: DEBUG
```

### Configure File Upload Limit
```yaml
app:
  file:
    max-size: 20971520  # 20MB
```

---

## 🐳 Docker Quick Start (Optional)

### Create Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/task-management-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Build and Run
```bash
docker build -t task-management-api .
docker run -p 8080:8080 -e DB_URL=jdbc:postgresql://host.docker.internal:5432/task_management_db task-management-api
```

---

## 🔒 Security Notes

### JWT Token Expiry
- **Access Token**: 24 hours (default)
- **Refresh Token**: 7 days (default)

### Refresh Token
```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

---

## ❌ Troubleshooting

### Port Already in Use
```bash
# Kill process on port 8080
# Windows:
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac:
lsof -ti:8080 | xargs kill -9
```

### Database Connection Failed
- Verify PostgreSQL is running
- Check credentials in `application.yml`
- Ensure database exists

### Flyway Migration Error
```bash
# Clean and rebuild
mvn clean flyway:clean flyway:migrate
```

### Build Errors
```bash
# Clear Maven cache
mvn clean install -U
```

---

## 📚 Additional Resources

- **Full Documentation**: See `README.md`
- **API Reference**: See `API_DOCUMENTATION.md`
- **Implementation Details**: See `IMPLEMENTATION_SUMMARY.md`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs JSON**: `http://localhost:8080/v3/api-docs`

---

## ✅ Health Check

Verify the application is running:
```bash
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

---

## 🎉 You're Ready!

Your Task Management API is now running and ready to use!

### Next Steps:
1. ✅ Create your first tenant and user
2. ✅ Create some tasks
3. ✅ Try filtering and searching
4. ✅ Upload attachments
5. ✅ View dashboard statistics
6. ✅ Integrate with your frontend

---

## 💡 Pro Tips

1. **Use Swagger UI** for easy API exploration
2. **Save your tokens** in environment variables
3. **Use the search feature** to find tasks quickly
4. **Check dashboard** regularly for insights
5. **Use tags** to organize tasks effectively
6. **Enable debug logging** during development
7. **Use pagination** for large datasets
8. **Leverage filters** for custom views

---

**Happy Task Managing! 🚀**
