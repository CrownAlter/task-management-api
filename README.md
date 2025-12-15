# Task Management API

A comprehensive, production-ready **Multi-Tenant Task Management REST API** built with **Spring Boot**, featuring advanced task management capabilities, role-based access control, file attachments, audit logging, and analytics.

---

## 🚀 Features

### ✅ **Core Features**
- **Multi-Tenancy Architecture** - Complete tenant isolation with automatic context filtering
- **JWT Authentication** - Secure token-based authentication with refresh tokens
- **Role-Based Access Control (RBAC)** - Fine-grained permissions (Admin, User roles)
- **RESTful API Design** - Clean, consistent API following REST principles
- **Comprehensive Validation** - Input validation with detailed error messages
- **Soft Delete Support** - Data preservation with logical deletion

### 📋 **Task Management**
- **Complete CRUD Operations** - Create, Read, Update, Delete tasks
- **Advanced Filtering** - Filter by status, priority, assignee, creator, due date, tags
- **Full-Text Search** - Search in task title and description
- **Pagination & Sorting** - Efficient data retrieval with customizable sorting
- **Task Assignment** - Assign/reassign tasks to team members
- **Status Management** - Workflow with status transitions (TODO → IN_PROGRESS → IN_REVIEW → COMPLETED)
- **Priority Levels** - LOW, MEDIUM, HIGH, URGENT
- **Due Date Tracking** - Track overdue tasks automatically
- **Tags System** - Organize tasks with custom tags

### 💬 **Comments & Collaboration**
- **Task Comments** - Add, edit, delete comments on tasks
- **User Attribution** - Track comment authors with timestamps
- **Edit History** - Mark edited comments
- **Pagination Support** - Efficient comment loading

### 📎 **File Attachments**
- **File Upload** - Upload files to tasks (max 10MB per file)
- **File Download** - Secure file download with authentication
- **Multiple Formats** - Support for various file types
- **File Management** - Delete attachments with proper authorization
- **Storage Isolation** - Tenant-specific file storage

### 👥 **User Management**
- **User Registration** - Self-service user registration
- **Profile Management** - Update user information
- **Password Management** - Change password with validation
- **User Search** - Search users by name or email
- **User Activation/Deactivation** - Admin control over user accounts
- **User Statistics** - Track individual user performance

### 📊 **Dashboard & Analytics**
- **Dashboard Statistics** - Overview of tasks, users, and completion rates
- **Personal Dashboard** - User-specific statistics
- **Task Analytics** - Time-series data for tasks created and completed
- **Top Performers** - Identify top task creators and assignees
- **Tag Analytics** - Most frequently used tags
- **Visual Data** - Ready for chart visualization

### 📝 **Audit Logging**
- **Comprehensive Audit Trail** - Track all system operations
- **User Activity Tracking** - Monitor user actions
- **Entity History** - View changes to specific entities
- **Action Filtering** - Filter by action type
- **IP Address Tracking** - Record user IP addresses

### 🔒 **Security**
- **JWT Token Security** - Secure authentication mechanism
- **Password Encryption** - BCrypt password hashing
- **Tenant Isolation** - Automatic filtering by tenant context
- **Authorization Checks** - Endpoint-level security
- **CORS Configuration** - Configurable cross-origin support

---

## 🛠️ Technology Stack

| Component | Technology |
|-----------|-----------|
| **Framework** | Spring Boot 3.x |
| **Language** | Java 17 |
| **Database** | PostgreSQL |
| **ORM** | Spring Data JPA / Hibernate |
| **Security** | Spring Security + JWT |
| **Migration** | Flyway |
| **Documentation** | OpenAPI/Swagger |
| **Build Tool** | Maven |
| **Validation** | Jakarta Bean Validation |
| **Logging** | SLF4J + Logback |

---

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Git**

---

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd task-management-api
```

### 2. Configure Database
Create a PostgreSQL database:
```sql
CREATE DATABASE task_management_db;
```

### 3. Configure Application
Update `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/task_management_db
    username: your_username
    password: your_password
```

### 4. Build the Project
```bash
mvn clean install
```

### 5. Run the Application
```bash
mvn spring-boot:run
```

The API will be available at: `http://localhost:8080`

---

## 📚 API Documentation

### Swagger UI
Access interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### API Endpoints Overview

#### **Authentication**
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login user
- `POST /api/v1/auth/refresh` - Refresh access token

#### **Task Management**
- `GET /api/v1/tasks` - Get all tasks (with filtering)
- `POST /api/v1/tasks` - Create new task
- `GET /api/v1/tasks/{id}` - Get task by ID
- `PUT /api/v1/tasks/{id}` - Update task
- `DELETE /api/v1/tasks/{id}` - Delete task
- `PATCH /api/v1/tasks/{id}/status` - Update task status
- `POST /api/v1/tasks/{id}/assign/{userId}` - Assign task
- `POST /api/v1/tasks/{id}/unassign` - Unassign task
- `GET /api/v1/tasks/my-tasks` - Get my assigned tasks
- `GET /api/v1/tasks/created-by-me` - Get tasks I created

#### **Task Comments**
- `GET /api/v1/tasks/{taskId}/comments` - Get task comments
- `POST /api/v1/tasks/{taskId}/comments` - Add comment
- `GET /api/v1/tasks/comments/{commentId}` - Get comment by ID
- `PUT /api/v1/tasks/comments/{commentId}` - Update comment
- `DELETE /api/v1/tasks/comments/{commentId}` - Delete comment

#### **Task Attachments**
- `GET /api/v1/tasks/{taskId}/attachments` - Get task attachments
- `POST /api/v1/tasks/{taskId}/attachments` - Upload attachment
- `GET /api/v1/tasks/attachments/{attachmentId}` - Get attachment info
- `GET /api/v1/tasks/attachments/{attachmentId}/download` - Download file
- `DELETE /api/v1/tasks/attachments/{attachmentId}` - Delete attachment

#### **User Management**
- `GET /api/v1/users/me` - Get current user profile
- `PUT /api/v1/users/me` - Update current user profile
- `POST /api/v1/users/me/change-password` - Change password
- `GET /api/v1/users` - Get all users (paginated)
- `GET /api/v1/users/{id}` - Get user by ID
- `GET /api/v1/users/search` - Search users
- `POST /api/v1/users/{id}/activate` - Activate user (Admin)
- `POST /api/v1/users/{id}/deactivate` - Deactivate user (Admin)
- `DELETE /api/v1/users/{id}` - Delete user (Admin)
- `PUT /api/v1/users/{id}/roles` - Update user roles (Admin)
- `GET /api/v1/users/{id}/statistics` - Get user statistics

#### **Dashboard & Analytics**
- `GET /api/v1/dashboard/stats` - Get dashboard statistics
- `GET /api/v1/dashboard/my-stats` - Get personal statistics
- `GET /api/v1/dashboard/analytics` - Get task analytics (date range)

#### **Audit Logs** (Admin Only)
- `GET /api/v1/audit-logs` - Get all audit logs
- `GET /api/v1/audit-logs/entity/{type}/{id}` - Get logs for entity
- `GET /api/v1/audit-logs/user/{userId}` - Get logs for user
- `GET /api/v1/audit-logs/action/{action}` - Get logs by action

---

## 🔐 Authentication

All endpoints (except registration and login) require JWT authentication.

### Login Flow
1. Register or login to get access token
2. Include token in all requests:
```http
Authorization: Bearer <your_access_token>
```

### Example: Register User
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "SecurePass123!",
    "tenantName": "Acme Corp"
  }'
```

### Example: Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "SecurePass123!"
  }'
```

---

## 📊 Task Filtering Examples

### Get High Priority Tasks
```bash
GET /api/v1/tasks?priorities=HIGH,URGENT&statuses=TODO,IN_PROGRESS
```

### Search Tasks
```bash
GET /api/v1/tasks?search=authentication&page=0&size=20
```

### Get Overdue Tasks
```bash
GET /api/v1/tasks?overdue=true&completed=false
```

### Get Tasks Assigned to Specific User
```bash
GET /api/v1/tasks?assignedToId=5
```

### Get Tasks by Date Range
```bash
GET /api/v1/tasks?dueDateFrom=2024-01-01T00:00:00&dueDateTo=2024-12-31T23:59:59
```

---

## 🏗️ Project Structure

```
src/main/java/com/adewunmi/task_management_api/
├── config/              # Configuration classes
│   ├── SecurityConfig.java
│   ├── CorsConfig.java
│   ├── OpenApiConfig.java
│   └── ...
├── controller/          # REST Controllers
│   ├── AuthController.java
│   ├── TaskController.java
│   ├── TaskCommentController.java
│   ├── TaskAttachmentController.java
│   ├── UserController.java
│   ├── DashboardController.java
│   └── AuditLogController.java
├── dto/                 # Data Transfer Objects
│   ├── request/
│   └── response/
├── entity/              # JPA Entities
│   ├── User.java
│   ├── Task.java
│   ├── TaskComment.java
│   ├── TaskAttachment.java
│   ├── Tenant.java
│   ├── Role.java
│   └── AuditLog.java
├── enums/               # Enumerations
│   ├── TaskStatus.java
│   ├── TaskPriority.java
│   └── RoleType.java
├── exception/           # Custom Exceptions
│   ├── GlobalExceptionHandler.java
│   └── ...
├── multitenant/         # Multi-tenancy Support
│   ├── TenantContext.java
│   └── TenantFilter.java
├── repository/          # Spring Data Repositories
├── security/            # Security Components
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── ...
├── service/             # Business Logic
│   ├── TaskService.java
│   ├── TaskCommentService.java
│   ├── TaskAttachmentService.java
│   ├── UserService.java
│   ├── DashboardService.java
│   ├── AuditLogService.java
│   └── FileStorageService.java
└── validation/          # Custom Validators
    └── TaskValidator.java

src/main/resources/
├── application.yml
├── application-dev.yml
├── application-prod.yml
└── db/migration/        # Flyway Migration Scripts
    ├── V1__create_tenant_table.sql
    ├── V2__create_role_table.sql
    ├── V3__create_user_table.sql
    ├── V4__create_user_roles_table.sql
    ├── V5__create_task_table.sql
    ├── V6__create_task_comment_table.sql
    ├── V7__create_task_attachment_table.sql
    └── V8__create_audit_log_table.sql
```

---

## 🎯 Task Status Workflow

```
TODO → IN_PROGRESS → IN_REVIEW → COMPLETED
  ↓         ↓            ↓
CANCELLED (can be reopened)
```

### Valid Transitions:
- **TODO**: Can go to any status
- **IN_PROGRESS**: Can go to IN_REVIEW, COMPLETED, CANCELLED, or back to TODO
- **IN_REVIEW**: Can go to COMPLETED, CANCELLED, or back to IN_PROGRESS/TODO
- **COMPLETED**: Can be reopened to any status except CANCELLED
- **CANCELLED**: Can be reopened to any status except COMPLETED

---

## 🔧 Configuration

### Environment Variables
```properties
# Database
DB_URL=jdbc:postgresql://localhost:5432/task_management_db
DB_USERNAME=postgres
DB_PASSWORD=password

# JWT
JWT_SECRET=your-secret-key-at-least-256-bits
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# File Upload
FILE_UPLOAD_DIR=./uploads
FILE_MAX_SIZE=10485760

# Server
SERVER_PORT=8080
```

### Profiles
- `dev` - Development profile
- `test` - Testing profile
- `prod` - Production profile

Activate profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn verify
```

### Run with Coverage
```bash
mvn clean test jacoco:report
```

---

## 📦 Deployment

### Build for Production
```bash
mvn clean package -Pprod
```

### Run Production JAR
```bash
java -jar target/task-management-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Docker Support (Optional)
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

---

## 🔍 Key Features in Detail

### Multi-Tenancy
- Automatic tenant context extraction from JWT token
- Tenant-specific data isolation at database level
- No cross-tenant data leakage
- Per-tenant resource limits

### Security
- JWT-based stateless authentication
- BCrypt password encryption (strength 10)
- Role-based endpoint protection
- CSRF protection disabled for API
- Tenant-aware authorization

### Audit Logging
- Asynchronous logging to avoid performance impact
- Tracks: Action, Entity Type, Entity ID, User, IP Address, Timestamp
- Admin-only access to audit logs
- Filterable by entity, user, or action type

### File Storage
- Local file system storage with tenant isolation
- Configurable upload directory
- File size validation (default 10MB)
- Secure file download with authentication
- Automatic cleanup on attachment deletion

---

## 📈 Performance Considerations

- **Pagination**: All list endpoints support pagination to handle large datasets
- **Indexing**: Database indexes on frequently queried fields
- **Lazy Loading**: JPA relationships use lazy loading where appropriate
- **Connection Pooling**: HikariCP for efficient database connections
- **Caching**: Ready for Redis/EhCache integration
- **Async Operations**: Audit logging runs asynchronously

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👨‍💻 Author

**Adewunmi**

---

## 🙏 Acknowledgments

- Spring Boot Team for the excellent framework
- PostgreSQL Community
- All contributors and users of this project

---

## 📞 Support

For issues, questions, or suggestions:
- Create an issue in the repository
- Contact: [Your Email]

---

## 🗺️ Roadmap

### Upcoming Features
- [ ] Real-time notifications (WebSocket)
- [ ] Email notifications
- [ ] Task templates
- [ ] Recurring tasks
- [ ] Task dependencies
- [ ] Time tracking
- [ ] Calendar view integration
- [ ] Export to PDF/Excel
- [ ] Mobile app API optimization
- [ ] GraphQL support
- [ ] Redis caching
- [ ] Elasticsearch integration for advanced search

---

**Made with ❤️ using Spring Boot**
