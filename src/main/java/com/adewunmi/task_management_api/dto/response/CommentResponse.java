package com.adewunmi.task_management_api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing comment details")
public class CommentResponse {

    @Schema(description = "Comment ID", example = "1")
    private Long id;

    @Schema(description = "Comment content")
    private String content;

    @Schema(description = "Task ID this comment belongs to")
    private Long taskId;

    @Schema(description = "User who created the comment")
    private UserSummary user;

    @Schema(description = "Whether the comment has been edited")
    private Boolean edited;

    @Schema(description = "Comment creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Comment last update timestamp")
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        
        public String getFullName() {
            return firstName + " " + lastName;
        }
    }
}
