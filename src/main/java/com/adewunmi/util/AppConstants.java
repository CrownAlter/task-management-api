package com.adewunmi.util;

public class AppConstants {
    // Pagination
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "desc";

    // JWT
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    // Tenant
    public static final String TENANT_HEADER = "X-Tenant-ID";

    // File Upload
    public static final long MAX_FILE_SIZE = 10485760; // 10MB
    public static final String[] ALLOWED_FILE_EXTENSIONS = {
            "pdf", "doc", "docx", "xls", "xlsx",
            "jpg", "jpeg", "png", "gif", "txt"
    };

    // Email
    public static final String FROM_EMAIL = "noreply@taskmanagement.com";

    // Date Format
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private AppConstants() {
        // Private constructor to prevent instantiation
    }
}
