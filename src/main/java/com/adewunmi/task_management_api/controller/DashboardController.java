package com.adewunmi.task_management_api.controller;

import com.adewunmi.task_management_api.dto.response.ApiResponse;
import com.adewunmi.task_management_api.dto.response.DashboardStatsResponse;
import com.adewunmi.task_management_api.dto.response.TaskAnalyticsResponse;
import com.adewunmi.task_management_api.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST Controller for Dashboard and Analytics
 * Provides endpoints for statistics, metrics, and data visualization
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard & Analytics", description = "Endpoints for statistics and analytics")
@SecurityRequirement(name = "Bearer Authentication")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Get dashboard statistics", description = "Retrieves overall dashboard statistics for the tenant")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        DashboardStatsResponse response = dashboardService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/my-stats")
    @Operation(summary = "Get my dashboard statistics", description = "Retrieves personal dashboard statistics for the current user")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getMyDashboardStats() {
        DashboardStatsResponse response = dashboardService.getMyDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get task analytics", description = "Retrieves task analytics for a specified date range")
    public ResponseEntity<ApiResponse<TaskAnalyticsResponse>> getTaskAnalytics(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        TaskAnalyticsResponse response = dashboardService.getTaskAnalytics(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
