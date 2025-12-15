package com.adewunmi.task_management_api.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for File Storage operations
 * Handles file upload, retrieval, and deletion
 */
public interface FileStorageService {
    
    /**
     * Store a file and return the file path
     */
    String storeFile(MultipartFile file, Long tenantId);
    
    /**
     * Load a file as a Resource
     */
    Resource loadFileAsResource(String filePath);
    
    /**
     * Delete a file
     */
    void deleteFile(String filePath);
    
    /**
     * Get file extension from filename
     */
    String getFileExtension(String filename);
}
