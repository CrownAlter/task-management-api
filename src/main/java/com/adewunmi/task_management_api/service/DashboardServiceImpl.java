package com.adewunmi.task_management_api.service;

import com.adewunmi.task_management_api.dto.response.DashboardStatsResponse;
import com.adewunmi.task_management_api.dto.response.TaskAnalyticsResponse;
import com.adewunmi.task_management_api.entity.Task;
import com.adewunmi.task_management_api.enums.TaskPriority;
import com.adewunmi.task_management_api.enums.TaskStatus;
import com.adewunmi.task_management_api.exception.BadRequestException;
import com.adewunmi.task_management_api.multitenant.TenantContext;
import com.adewunmi.task_management_api.repository.TaskRepository;
import com.adewunmi.task_management_api.repository.UserRepository;
import com.adewunmi.task_management_api.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for Dashboard and Analytics
 * Provides comprehensive statistics and metrics for data visualization
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public DashboardStatsResponse getDashboardStats() {
        log.info("Fetching dashboard statistics");
        
        Long tenantId = TenantContext.getCurrentTenant();
        
        // Get all tasks for tenant
        Specification<Task> tenantSpec = (root, query, cb) -> 
            cb.and(
                cb.equal(root.get("tenant").get("id"), tenantId),
                cb.isNull(root.get("deletedAt"))
            );
        
        List<Task> allTasks = taskRepository.findAll(tenantSpec);
        
        // Calculate statistics
        Long totalTasks = (long) allTasks.size();
        
        Map<String, Long> tasksByStatus = allTasks.stream()
                .collect(Collectors.groupingBy(
                    task -> task.getStatus().name(),
                    Collectors.counting()
                ));
        
        Map<String, Long> tasksByPriority = allTasks.stream()
                .collect(Collectors.groupingBy(
                    task -> task.getPriority().name(),
                    Collectors.counting()
                ));
        
        LocalDateTime now = LocalDateTime.now();
        Long overdueTasks = allTasks.stream()
                .filter(task -> task.getDueDate() != null && 
                               task.getDueDate().isBefore(now) && 
                               task.getStatus() != TaskStatus.COMPLETED)
                .count();
        
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        Long tasksDueToday = allTasks.stream()
                .filter(task -> task.getDueDate() != null && 
                               task.getDueDate().isBefore(todayEnd) && 
                               task.getDueDate().isAfter(now))
                .count();
        
        LocalDateTime weekEnd = now.plusDays(7);
        Long tasksDueThisWeek = allTasks.stream()
                .filter(task -> task.getDueDate() != null && 
                               task.getDueDate().isAfter(now) && 
                               task.getDueDate().isBefore(weekEnd))
                .count();
        
        // Get current user statistics
        CustomUserDetails currentUser = getCurrentUserDetails();
        Long myAssignedTasks = allTasks.stream()
                .filter(task -> task.getAssignedTo() != null && 
                               task.getAssignedTo().getId().equals(currentUser.getId()))
                .count();
        
        Long myCompletedTasks = allTasks.stream()
                .filter(task -> task.getAssignedTo() != null && 
                               task.getAssignedTo().getId().equals(currentUser.getId()) &&
                               task.getStatus() == TaskStatus.COMPLETED)
                .count();
        
        // User statistics
        Long totalUsers = userRepository.countActiveUsersByTenantId(tenantId);
        Long activeUsers = totalUsers; // All users returned by countActiveUsersByTenantId are active
        
        // Calculate completion rate
        Long completedTasks = allTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
                .count();
        Double completionRate = totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0.0;
        
        // Average tasks per user
        Double averageTasksPerUser = totalUsers > 0 ? (totalTasks * 1.0 / totalUsers) : 0.0;
        
        return DashboardStatsResponse.builder()
                .totalTasks(totalTasks)
                .tasksByStatus(tasksByStatus)
                .tasksByPriority(tasksByPriority)
                .overdueTasks(overdueTasks)
                .tasksDueToday(tasksDueToday)
                .tasksDueThisWeek(tasksDueThisWeek)
                .myAssignedTasks(myAssignedTasks)
                .myCompletedTasks(myCompletedTasks)
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .completionRate(Math.round(completionRate * 100.0) / 100.0)
                .averageTasksPerUser(Math.round(averageTasksPerUser * 100.0) / 100.0)
                .build();
    }

    @Override
    public TaskAnalyticsResponse getTaskAnalytics(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching task analytics from {} to {}", startDate, endDate);
        
        Long tenantId = TenantContext.getCurrentTenant();
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        // Get tasks within date range
        Specification<Task> dateRangeSpec = (root, query, cb) -> 
            cb.and(
                cb.equal(root.get("tenant").get("id"), tenantId),
                cb.isNull(root.get("deletedAt")),
                cb.between(root.get("createdAt"), startDateTime, endDateTime)
            );
        
        List<Task> tasks = taskRepository.findAll(dateRangeSpec);
        
        // Tasks created over time
        Map<LocalDate, Long> tasksCreatedMap = tasks.stream()
                .collect(Collectors.groupingBy(
                    task -> task.getCreatedAt().toLocalDate(),
                    Collectors.counting()
                ));
        
        List<TaskAnalyticsResponse.TimeSeriesData> tasksCreatedOverTime = tasksCreatedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> TaskAnalyticsResponse.TimeSeriesData.builder()
                        .date(entry.getKey().toString())
                        .count(entry.getValue())
                        .build())
                .collect(Collectors.toList());
        
        // Tasks completed over time
        Map<LocalDate, Long> tasksCompletedMap = tasks.stream()
                .filter(task -> task.getCompletedAt() != null)
                .collect(Collectors.groupingBy(
                    task -> task.getCompletedAt().toLocalDate(),
                    Collectors.counting()
                ));
        
        List<TaskAnalyticsResponse.TimeSeriesData> tasksCompletedOverTime = tasksCompletedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> TaskAnalyticsResponse.TimeSeriesData.builder()
                        .date(entry.getKey().toString())
                        .count(entry.getValue())
                        .build())
                .collect(Collectors.toList());
        
        // Top task creators
        Map<Long, Long> creatorCountMap = tasks.stream()
                .collect(Collectors.groupingBy(
                    task -> task.getCreatedBy().getId(),
                    Collectors.counting()
                ));
        
        List<TaskAnalyticsResponse.UserTaskCount> topTaskCreators = creatorCountMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    Task task = tasks.stream()
                            .filter(t -> t.getCreatedBy().getId().equals(entry.getKey()))
                            .findFirst()
                            .orElse(null);
                    return TaskAnalyticsResponse.UserTaskCount.builder()
                            .userId(entry.getKey())
                            .userName(task != null ? task.getCreatedBy().getFullName() : "Unknown")
                            .taskCount(entry.getValue())
                            .build();
                })
                .collect(Collectors.toList());
        
        // Top task assignees
        Map<Long, Long> assigneeCountMap = tasks.stream()
                .filter(task -> task.getAssignedTo() != null)
                .collect(Collectors.groupingBy(
                    task -> task.getAssignedTo().getId(),
                    Collectors.counting()
                ));
        
        List<TaskAnalyticsResponse.UserTaskCount> topTaskAssignees = assigneeCountMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    Task task = tasks.stream()
                            .filter(t -> t.getAssignedTo() != null && t.getAssignedTo().getId().equals(entry.getKey()))
                            .findFirst()
                            .orElse(null);
                    return TaskAnalyticsResponse.UserTaskCount.builder()
                            .userId(entry.getKey())
                            .userName(task != null ? task.getAssignedTo().getFullName() : "Unknown")
                            .taskCount(entry.getValue())
                            .build();
                })
                .collect(Collectors.toList());
        
        // Most used tags
        Map<String, Long> tagCountMap = new HashMap<>();
        tasks.stream()
                .filter(task -> task.getTags() != null && !task.getTags().trim().isEmpty())
                .forEach(task -> {
                    String[] tags = task.getTags().split(",");
                    for (String tag : tags) {
                        String trimmedTag = tag.trim();
                        if (!trimmedTag.isEmpty()) {
                            tagCountMap.merge(trimmedTag, 1L, Long::sum);
                        }
                    }
                });
        
        List<TaskAnalyticsResponse.TagCount> mostUsedTags = tagCountMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> TaskAnalyticsResponse.TagCount.builder()
                        .tag(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .collect(Collectors.toList());
        
        return TaskAnalyticsResponse.builder()
                .tasksCreatedOverTime(tasksCreatedOverTime)
                .tasksCompletedOverTime(tasksCompletedOverTime)
                .topTaskCreators(topTaskCreators)
                .topTaskAssignees(topTaskAssignees)
                .mostUsedTags(mostUsedTags)
                .build();
    }

    @Override
    public DashboardStatsResponse getMyDashboardStats() {
        log.info("Fetching personal dashboard statistics");
        
        Long tenantId = TenantContext.getCurrentTenant();
        CustomUserDetails currentUser = getCurrentUserDetails();
        
        // Get all tasks assigned to current user
        Specification<Task> userTaskSpec = (root, query, cb) -> 
            cb.and(
                cb.equal(root.get("tenant").get("id"), tenantId),
                cb.isNull(root.get("deletedAt")),
                cb.equal(root.get("assignedTo").get("id"), currentUser.getId())
            );
        
        List<Task> myTasks = taskRepository.findAll(userTaskSpec);
        
        Long totalTasks = (long) myTasks.size();
        
        Map<String, Long> tasksByStatus = myTasks.stream()
                .collect(Collectors.groupingBy(
                    task -> task.getStatus().name(),
                    Collectors.counting()
                ));
        
        Map<String, Long> tasksByPriority = myTasks.stream()
                .collect(Collectors.groupingBy(
                    task -> task.getPriority().name(),
                    Collectors.counting()
                ));
        
        LocalDateTime now = LocalDateTime.now();
        Long overdueTasks = myTasks.stream()
                .filter(task -> task.getDueDate() != null && 
                               task.getDueDate().isBefore(now) && 
                               task.getStatus() != TaskStatus.COMPLETED)
                .count();
        
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        Long tasksDueToday = myTasks.stream()
                .filter(task -> task.getDueDate() != null && 
                               task.getDueDate().isBefore(todayEnd) && 
                               task.getDueDate().isAfter(now))
                .count();
        
        LocalDateTime weekEnd = now.plusDays(7);
        Long tasksDueThisWeek = myTasks.stream()
                .filter(task -> task.getDueDate() != null && 
                               task.getDueDate().isAfter(now) && 
                               task.getDueDate().isBefore(weekEnd))
                .count();
        
        Long myCompletedTasks = myTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
                .count();
        
        Double completionRate = totalTasks > 0 ? (myCompletedTasks * 100.0 / totalTasks) : 0.0;
        
        return DashboardStatsResponse.builder()
                .totalTasks(totalTasks)
                .tasksByStatus(tasksByStatus)
                .tasksByPriority(tasksByPriority)
                .overdueTasks(overdueTasks)
                .tasksDueToday(tasksDueToday)
                .tasksDueThisWeek(tasksDueThisWeek)
                .myAssignedTasks(totalTasks)
                .myCompletedTasks(myCompletedTasks)
                .completionRate(Math.round(completionRate * 100.0) / 100.0)
                .build();
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
}
