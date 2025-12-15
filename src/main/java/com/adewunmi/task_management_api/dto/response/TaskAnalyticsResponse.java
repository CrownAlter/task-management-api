package com.adewunmi.task_management_api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Task analytics response")
public class TaskAnalyticsResponse {

    @Schema(description = "Tasks created over time")
    private List<TimeSeriesData> tasksCreatedOverTime;

    @Schema(description = "Tasks completed over time")
    private List<TimeSeriesData> tasksCompletedOverTime;

    @Schema(description = "Top task creators")
    private List<UserTaskCount> topTaskCreators;

    @Schema(description = "Top task assignees")
    private List<UserTaskCount> topTaskAssignees;

    @Schema(description = "Most used tags")
    private List<TagCount> mostUsedTags;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSeriesData {
        private String date;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserTaskCount {
        private Long userId;
        private String userName;
        private Long taskCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagCount {
        private String tag;
        private Long count;
    }
}
