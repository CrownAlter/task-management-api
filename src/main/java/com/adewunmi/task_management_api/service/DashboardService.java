package com.adewunmi.task_management_api.service;

import com.adewunmi.task_management_api.dto.response.DashboardStatsResponse;
import com.adewunmi.task_management_api.dto.response.TaskAnalyticsResponse;

import java.time.LocalDate;

/**
 * Service interface for Dashboard and Analytics
 * Provides statistics and metrics for visualization
 */
public interface DashboardService {
    
    /**
     * Get overall dashboard statistics
     */
    DashboardStatsResponse getDashboardStats();
    
    /**
     * Get task analytics for a date range
     */
    TaskAnalyticsResponse getTaskAnalytics(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get personal dashboard statistics for current user
     */
    DashboardStatsResponse getMyDashboardStats();
}
