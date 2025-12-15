package com.adewunmi.task_management_api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dashboard statistics response")
public class DashboardStatsResponse {

    @Schema(description = "Total number of tasks")
    private Long totalTasks;

    @Schema(description = "Number of tasks by status")
    private Map<String, Long> tasksByStatus;

    @Schema(description = "Number of tasks by priority")
    private Map<String, Long> tasksByPriority;

    @Schema(description = "Number of overdue tasks")
    private Long overdueTasks;

    @Schema(description = "Number of tasks due today")
    private Long tasksDueToday;

    @Schema(description = "Number of tasks due this week")
    private Long tasksDueThisWeek;

    @Schema(description = "Number of my assigned tasks")
    private Long myAssignedTasks;

    @Schema(description = "Number of my completed tasks")
    private Long myCompletedTasks;

    @Schema(description = "Total number of users in tenant")
    private Long totalUsers;

    @Schema(description = "Number of active users")
    private Long activeUsers;

    @Schema(description = "Task completion rate (percentage)")
    private Double completionRate;

    @Schema(description = "Average tasks per user")
    private Double averageTasksPerUser;
}
