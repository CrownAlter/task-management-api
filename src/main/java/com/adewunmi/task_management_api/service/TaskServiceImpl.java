package com.adewunmi.task_management_api.service;

import com.adewunmi.task_management_api.dto.request.TaskFilterRequest;
import com.adewunmi.task_management_api.dto.request.TaskRequest;
import com.adewunmi.task_management_api.dto.response.TaskResponse;
import com.adewunmi.task_management_api.entity.Task;
import com.adewunmi.task_management_api.entity.Tenant;
import com.adewunmi.task_management_api.entity.User;
import com.adewunmi.task_management_api.enums.TaskStatus;
import com.adewunmi.task_management_api.exception.BadRequestException;
import com.adewunmi.task_management_api.exception.ResourceNotFoundException;
import com.adewunmi.task_management_api.multitenant.TenantContext;
import com.adewunmi.task_management_api.repository.TaskRepository;
import com.adewunmi.task_management_api.repository.TenantRepository;
import com.adewunmi.task_management_api.repository.UserRepository;
import com.adewunmi.task_management_api.security.CustomUserDetails;
import com.adewunmi.task_management_api.validation.TaskValidator;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final TaskValidator taskValidator;

    @Override
    public TaskResponse createTask(TaskRequest request) {
        log.info("Creating new task with title: {}", request.getTitle());
        
        // Validate task creation
        taskValidator.validateTaskCreation(request);
        
        Long tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new BadRequestException("Tenant context not found");
        }
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "id", tenantId));
        
        CustomUserDetails currentUser = getCurrentUserDetails();
        User creator = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));
        
        // Validate assigned user if provided
        User assignedUser = null;
        if (request.getAssignedToId() != null) {
            assignedUser = userRepository.findByIdAndTenantId(request.getAssignedToId(), tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedToId()));
        }
        
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .tenant(tenant)
                .createdBy(creator)
                .assignedTo(assignedUser)
                .tags(request.getTags())
                .build();
        
        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with ID: {}", savedTask.getId());
        
        return mapToResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long taskId) {
        log.info("Fetching task with ID: {}", taskId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        Task task = taskRepository.findByIdAndTenantId(taskId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        return mapToResponse(task);
    }

    @Override
    public TaskResponse updateTask(Long taskId, TaskRequest request) {
        log.info("Updating task with ID: {}", taskId);
        
        // Validate task update
        taskValidator.validateTaskUpdate(request);
        
        Long tenantId = TenantContext.getCurrentTenant();
        Task task = taskRepository.findByIdAndTenantId(taskId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        // Validate status transition if status is changing
        if (!task.getStatus().equals(request.getStatus())) {
            taskValidator.validateStatusTransition(task.getStatus(), request.getStatus());
        }
        
        // Update fields
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setTags(request.getTags());
        
        // Update assigned user if provided
        if (request.getAssignedToId() != null) {
            User assignedUser = userRepository.findByIdAndTenantId(request.getAssignedToId(), tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedToId()));
            task.setAssignedTo(assignedUser);
        } else {
            task.setAssignedTo(null);
        }
        
        // Set completion timestamp if status is COMPLETED
        if (request.getStatus() == TaskStatus.COMPLETED && task.getCompletedAt() == null) {
            task.setCompletedAt(LocalDateTime.now());
        } else if (request.getStatus() != TaskStatus.COMPLETED) {
            task.setCompletedAt(null);
        }
        
        Task updatedTask = taskRepository.save(task);
        log.info("Task updated successfully with ID: {}", updatedTask.getId());
        
        return mapToResponse(updatedTask);
    }

    @Override
    public void deleteTask(Long taskId) {
        log.info("Deleting task with ID: {}", taskId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        Task task = taskRepository.findByIdAndTenantId(taskId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        // Validate task deletion
        taskValidator.validateTaskDeletion(task.getStatus());
        
        taskRepository.delete(task);
        log.info("Task deleted successfully with ID: {}", taskId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(TaskFilterRequest filterRequest) {
        log.info("Fetching all tasks with filters");
        
        Long tenantId = TenantContext.getCurrentTenant();
        Specification<Task> spec = createSpecification(filterRequest, tenantId);
        Pageable pageable = createPageable(filterRequest);
        
        Page<Task> tasks = taskRepository.findAll(spec, pageable);
        return tasks.map(this::mapToResponse);
    }

    @Override
    public TaskResponse assignTask(Long taskId, Long userId) {
        log.info("Assigning task {} to user {}", taskId, userId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        Task task = taskRepository.findByIdAndTenantId(taskId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        // Validate task assignment
        taskValidator.validateTaskAssignment(task.getStatus());
        
        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        if (!user.getActive()) {
            throw new BadRequestException("Cannot assign task to an inactive user");
        }
        
        task.setAssignedTo(user);
        Task updatedTask = taskRepository.save(task);
        
        log.info("Task assigned successfully");
        return mapToResponse(updatedTask);
    }

    @Override
    public TaskResponse unassignTask(Long taskId) {
        log.info("Unassigning task {}", taskId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        Task task = taskRepository.findByIdAndTenantId(taskId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        task.setAssignedTo(null);
        Task updatedTask = taskRepository.save(task);
        
        log.info("Task unassigned successfully");
        return mapToResponse(updatedTask);
    }

    @Override
    public TaskResponse updateTaskStatus(Long taskId, TaskStatus status) {
        log.info("Updating task {} status to {}", taskId, status);
        
        Long tenantId = TenantContext.getCurrentTenant();
        Task task = taskRepository.findByIdAndTenantId(taskId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        TaskStatus oldStatus = task.getStatus();
        
        // Validate status transition
        taskValidator.validateStatusTransition(oldStatus, status);
        
        task.setStatus(status);
        
        // Set completion timestamp if status is COMPLETED
        if (status == TaskStatus.COMPLETED && oldStatus != TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        } else if (status != TaskStatus.COMPLETED && oldStatus == TaskStatus.COMPLETED) {
            task.setCompletedAt(null);
        }
        
        Task updatedTask = taskRepository.save(task);
        log.info("Task status updated successfully");
        
        return mapToResponse(updatedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getMyTasks(TaskFilterRequest filterRequest) {
        log.info("Fetching tasks assigned to current user");
        
        CustomUserDetails currentUser = getCurrentUserDetails();
        filterRequest.setAssignedToId(currentUser.getId());
        
        return getAllTasks(filterRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksCreatedByMe(TaskFilterRequest filterRequest) {
        log.info("Fetching tasks created by current user");
        
        CustomUserDetails currentUser = getCurrentUserDetails();
        filterRequest.setCreatedById(currentUser.getId());
        
        return getAllTasks(filterRequest);
    }
    
    /**
     * Get current authenticated user details
     */
    private CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("User not authenticated");
        }
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            throw new BadRequestException("Invalid user details");
        }
        
        return (CustomUserDetails) principal;
    }

    /**
     * Create JPA Specification for dynamic filtering
     */
    private Specification<Task> createSpecification(TaskFilterRequest filter, Long tenantId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Tenant isolation - CRITICAL
            predicates.add(criteriaBuilder.equal(root.get("tenant").get("id"), tenantId));
            
            // Soft delete filter
            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));
            
            // Search in title and description
            if (filter.getSearch() != null && !filter.getSearch().trim().isEmpty()) {
                String searchPattern = "%" + filter.getSearch().toLowerCase() + "%";
                Predicate titleMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")), searchPattern);
                Predicate descriptionMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")), searchPattern);
                predicates.add(criteriaBuilder.or(titleMatch, descriptionMatch));
            }
            
            // Filter by status
            if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) {
                predicates.add(root.get("status").in(filter.getStatuses()));
            }
            
            // Filter by priority
            if (filter.getPriorities() != null && !filter.getPriorities().isEmpty()) {
                predicates.add(root.get("priority").in(filter.getPriorities()));
            }
            
            // Filter by assigned user
            if (filter.getAssignedToId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("assignedTo").get("id"), filter.getAssignedToId()));
            }
            
            // Filter by creator
            if (filter.getCreatedById() != null) {
                predicates.add(criteriaBuilder.equal(root.get("createdBy").get("id"), filter.getCreatedById()));
            }
            
            // Filter by due date range
            if (filter.getDueDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate"), filter.getDueDateFrom()));
            }
            if (filter.getDueDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dueDate"), filter.getDueDateTo()));
            }
            
            // Filter by tags
            if (filter.getTags() != null && !filter.getTags().trim().isEmpty()) {
                String tagPattern = "%" + filter.getTags().toLowerCase() + "%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("tags")), tagPattern));
            }
            
            // Filter overdue tasks
            if (Boolean.TRUE.equals(filter.getOverdue())) {
                predicates.add(criteriaBuilder.lessThan(root.get("dueDate"), LocalDateTime.now()));
                predicates.add(criteriaBuilder.notEqual(root.get("status"), TaskStatus.COMPLETED));
            }
            
            // Filter completed tasks
            if (filter.getCompleted() != null) {
                if (filter.getCompleted()) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), TaskStatus.COMPLETED));
                } else {
                    predicates.add(criteriaBuilder.notEqual(root.get("status"), TaskStatus.COMPLETED));
                }
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Create Pageable with sorting
     */
    private Pageable createPageable(TaskFilterRequest filter) {
        Sort.Direction direction = filter.getSortDirection().equalsIgnoreCase("asc") 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
        
        Sort sort = Sort.by(direction, filter.getSortBy());
        
        return PageRequest.of(filter.getPage(), filter.getSize(), sort);
    }

    /**
     * Map Task entity to TaskResponse DTO
     */
    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .createdBy(mapUserToSummary(task.getCreatedBy()))
                .assignedTo(task.getAssignedTo() != null ? mapUserToSummary(task.getAssignedTo()) : null)
                .tags(task.getTags())
                .completedAt(task.getCompletedAt())
                .commentCount(task.getComments() != null ? task.getComments().size() : 0)
                .attachmentCount(task.getAttachments() != null ? task.getAttachments().size() : 0)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    /**
     * Map User entity to UserSummary DTO
     */
    private TaskResponse.UserSummary mapUserToSummary(User user) {
        return TaskResponse.UserSummary.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
