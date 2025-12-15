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
@Schema(description = "Response object containing attachment details")
public class AttachmentResponse {

    @Schema(description = "Attachment ID", example = "1")
    private Long id;

    @Schema(description = "File name", example = "document.pdf")
    private String fileName;

    @Schema(description = "File size in bytes", example = "1048576")
    private Long fileSize;

    @Schema(description = "MIME type", example = "application/pdf")
    private String mimeType;

    @Schema(description = "Task ID this attachment belongs to")
    private Long taskId;

    @Schema(description = "User who uploaded the attachment")
    private UserSummary uploadedBy;

    @Schema(description = "Download URL")
    private String downloadUrl;

    @Schema(description = "Upload timestamp")
    private LocalDateTime createdAt;

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
