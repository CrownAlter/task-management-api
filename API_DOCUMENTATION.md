# Task Management API - Documentation

## Overview
This is a comprehensive multi-tenant Task Management REST API with advanced features including filtering, search, pagination, and role-based access control.

## Base URL
```
http://localhost:8080/api/v1
```

## Authentication
All task endpoints require JWT authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <your_jwt_token>
```

---

## Task Management Endpoints

### 1. Create Task
**Endpoint:** `POST /api/v1/tasks`

**Description:** Creates a new task in the current tenant.

**Request Body:**
```json
{
  "title": "Implement user authentication",
  "description": "Implement JWT-based authentication for the API",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2024-12-31T23:59:59",
  "assignedToId": 2,
  "tags": "backend,authentication,security"
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "Task created successfully",
  "data": {
    "id": 1,
    "title": "Implement user authentication",
    "description": "Implement JWT-based authentication for the API",
    "status": "TODO",
    "priority": "HIGH",
    "dueDate": "2024-12-31T23:59:59",
    "createdBy": {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john@example.com"
    },
    "assignedTo": {
      "id": 2,
      "firstName": "Jane",
      "lastName": "Smith",
      "email": "jane@example.com"
    },
    "tags": "backend,authentication,security",
    "completedAt": null,
    "commentCount": 0,
    "attachmentCount": 0,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

**Validation Rules:**
- Title: Required, 3-200 characters
- Description: Optional, max 5000 characters
- Status: Required (TODO, IN_PROGRESS, IN_REVIEW, COMPLETED, CANCELLED)
- Priority: Required (LOW, MEDIUM, HIGH, URGENT)
- Due Date: Cannot be in the past
- Tags: Max 500 characters, alphanumeric with hyphens and underscores only

---

### 2. Get Task by ID
**Endpoint:** `GET /api/v1/tasks/{id}`

**Description:** Retrieves a specific task by ID (tenant isolated).

**Path Parameters:**
- `id` (Long) - Task ID

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "Implement user authentication",
    ...
  }
}
```

---

### 3. Update Task
**Endpoint:** `PUT /api/v1/tasks/{id}`

**Description:** Updates an existing task.

**Path Parameters:**
- `id` (Long) - Task ID

**Request Body:** Same as Create Task

**Response:** `200 OK`

**Status Transition Rules:**
- **FROM TODO:** Can transition to any status
- **FROM IN_PROGRESS:** Can go to IN_REVIEW, COMPLETED, CANCELLED, or back to TODO
- **FROM IN_REVIEW:** Can go to COMPLETED, CANCELLED, or back to IN_PROGRESS/TODO
- **FROM COMPLETED:** Can reopen to TODO, IN_PROGRESS, or IN_REVIEW (but not CANCELLED)
- **FROM CANCELLED:** Can reopen to TODO, IN_PROGRESS, or IN_REVIEW (but not COMPLETED)

---

### 4. Delete Task
**Endpoint:** `DELETE /api/v1/tasks/{id}`

**Description:** Soft deletes a task.

**Path Parameters:**
- `id` (Long) - Task ID

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Task deleted successfully",
  "data": null
}
```

---

### 5. Get All Tasks (with Advanced Filtering)
**Endpoint:** `GET /api/v1/tasks`

**Description:** Retrieves all tasks with filtering, search, pagination, and sorting.

**Query Parameters:**
- `search` (String) - Search in title and description
- `statuses` (List<TaskStatus>) - Filter by status(es)
- `priorities` (List<TaskPriority>) - Filter by priority(ies)
- `assignedToId` (Long) - Filter by assigned user
- `createdById` (Long) - Filter by creator
- `dueDateFrom` (DateTime) - Filter by due date from
- `dueDateTo` (DateTime) - Filter by due date to
- `tags` (String) - Filter by tags (comma-separated)
- `overdue` (Boolean) - Show only overdue tasks
- `completed` (Boolean) - Show only completed/incomplete tasks
- `page` (Integer) - Page number (default: 0)
- `size` (Integer) - Page size (default: 20)
- `sortBy` (String) - Sort field (default: createdAt)
- `sortDirection` (String) - Sort direction: asc/desc (default: desc)

**Example Request:**
```
GET /api/v1/tasks?search=authentication&statuses=TODO,IN_PROGRESS&priorities=HIGH,URGENT&page=0&size=20&sortBy=dueDate&sortDirection=asc
```

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "content": [...],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20
    },
    "totalElements": 45,
    "totalPages": 3,
    "last": false,
    "first": true
  }
}
```

---

### 6. Assign Task to User
**Endpoint:** `POST /api/v1/tasks/{id}/assign/{userId}`

**Description:** Assigns a task to a specific user within the same tenant.

**Path Parameters:**
- `id` (Long) - Task ID
- `userId` (Long) - User ID

**Response:** `200 OK`

**Rules:**
- Cannot assign completed or cancelled tasks
- User must belong to the same tenant
- User must be active

---

### 7. Unassign Task
**Endpoint:** `POST /api/v1/tasks/{id}/unassign`

**Description:** Removes the assignment from a task.

**Path Parameters:**
- `id` (Long) - Task ID

**Response:** `200 OK`

---

### 8. Update Task Status
**Endpoint:** `PATCH /api/v1/tasks/{id}/status`

**Description:** Updates only the status of a task with validation.

**Path Parameters:**
- `id` (Long) - Task ID

**Query Parameters:**
- `status` (TaskStatus) - New status

**Example:**
```
PATCH /api/v1/tasks/1/status?status=IN_PROGRESS
```

**Response:** `200 OK`

---

### 9. Get My Tasks
**Endpoint:** `GET /api/v1/tasks/my-tasks`

**Description:** Retrieves tasks assigned to the current authenticated user.

**Query Parameters:** Same filtering options as "Get All Tasks" (except `assignedToId`)

**Response:** `200 OK`

---

### 10. Get Tasks Created By Me
**Endpoint:** `GET /api/v1/tasks/created-by-me`

**Description:** Retrieves tasks created by the current authenticated user.

**Query Parameters:** Same filtering options as "Get All Tasks" (except `createdById`)

**Response:** `200 OK`

---

## Task Enums

### TaskStatus
- `TODO` - Task is not started
- `IN_PROGRESS` - Task is being worked on
- `IN_REVIEW` - Task is under review
- `COMPLETED` - Task is completed
- `CANCELLED` - Task is cancelled

### TaskPriority
- `LOW` - Low priority
- `MEDIUM` - Medium priority
- `HIGH` - High priority
- `URGENT` - Urgent priority

---

## Security & Tenant Isolation

### Multi-Tenancy
- All task operations are automatically isolated by tenant
- Users can only access tasks within their own tenant
- Tenant context is extracted from the JWT token via `TenantFilter`

### Access Control
- All endpoints require authentication
- Users can only assign tasks to users within their tenant
- Task creators and assignees can view/modify their tasks

---

## Error Responses

### 400 Bad Request
```json
{
  "success": false,
  "message": "Task title must be at least 3 characters long",
  "data": null
}
```

### 401 Unauthorized
```json
{
  "success": false,
  "message": "Unauthorized access",
  "data": null
}
```

### 404 Not Found
```json
{
  "success": false,
  "message": "Task not found with id: 123",
  "data": null
}
```

---

## Example Workflows

### 1. Creating and Assigning a Task
```bash
# Step 1: Create a task
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Fix login bug",
    "description": "Users cannot login with special characters in password",
    "status": "TODO",
    "priority": "URGENT",
    "dueDate": "2024-12-20T17:00:00",
    "tags": "bug,login,security"
  }'

# Step 2: Assign to a user
curl -X POST http://localhost:8080/api/v1/tasks/1/assign/5 \
  -H "Authorization: Bearer YOUR_TOKEN"

# Step 3: Update status
curl -X PATCH "http://localhost:8080/api/v1/tasks/1/status?status=IN_PROGRESS" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 2. Filtering Tasks
```bash
# Get all high priority tasks that are overdue
curl -X GET "http://localhost:8080/api/v1/tasks?priorities=HIGH,URGENT&overdue=true&page=0&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN"

# Get my incomplete tasks
curl -X GET "http://localhost:8080/api/v1/tasks/my-tasks?completed=false&sortBy=dueDate&sortDirection=asc" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 3. Searching Tasks
```bash
# Search for tasks containing "authentication"
curl -X GET "http://localhost:8080/api/v1/tasks?search=authentication" \
  -H "Authorization: Bearer YOUR_TOKEN"

# Search with multiple filters
curl -X GET "http://localhost:8080/api/v1/tasks?search=bug&statuses=TODO,IN_PROGRESS&priorities=HIGH,URGENT&tags=backend" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Swagger Documentation

Access interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

---

## Best Practices

1. **Use appropriate status transitions** - Follow the status transition rules to maintain data integrity
2. **Set due dates** - Always set realistic due dates for better task tracking
3. **Use tags effectively** - Use consistent, descriptive tags for better filtering
4. **Assign tasks promptly** - Assign tasks to specific users for accountability
5. **Leverage filtering** - Use the advanced filtering to create custom views (e.g., "my overdue high-priority tasks")
6. **Monitor overdue tasks** - Regularly check for overdue tasks using the `overdue=true` filter

---

## Implementation Features

✅ Complete CRUD operations for tasks
✅ Advanced filtering (by status, priority, assignee, creator, due date, tags)
✅ Full-text search in title and description
✅ Pagination and sorting
✅ Task assignment and reassignment
✅ Status transition validation
✅ Tenant-based isolation
✅ Input validation with detailed error messages
✅ Soft delete support
✅ Automatic completion timestamp tracking
✅ Comment and attachment counters
✅ Security with JWT authentication
