package com.adewunmi.task_management_api.service;

import com.adewunmi.task_management_api.exception.BadRequestException;
import com.adewunmi.task_management_api.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service implementation for File Storage
 * Provides local file system storage for task attachments
 */
@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;
    private final long maxFileSize;

    public FileStorageServiceImpl(
            @Value("${app.file.upload-dir:./uploads}") String uploadDir,
            @Value("${app.file.max-size:10485760}") long maxFileSize) {
        
        this.maxFileSize = maxFileSize;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("File storage location created at: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, Long tenantId) {
        // Validate file
        if (file.isEmpty()) {
            throw new BadRequestException("Cannot upload empty file");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new BadRequestException("File size exceeds maximum limit of " + (maxFileSize / 1024 / 1024) + "MB");
        }
        
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            // Check if the file's name contains invalid characters
            if (originalFileName.contains("..")) {
                throw new BadRequestException("Filename contains invalid path sequence: " + originalFileName);
            }
            
            // Generate unique filename with tenant prefix
            String fileExtension = getFileExtension(originalFileName);
            String uniqueFileName = "tenant_" + tenantId + "_" + UUID.randomUUID().toString() + fileExtension;
            
            // Create tenant-specific directory
            Path tenantDirectory = this.fileStorageLocation.resolve("tenant_" + tenantId);
            Files.createDirectories(tenantDirectory);
            
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = tenantDirectory.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            String relativePath = "tenant_" + tenantId + "/" + uniqueFileName;
            log.info("File stored successfully: {}", relativePath);
            
            return relativePath;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String filePath) {
        try {
            Path path = this.fileStorageLocation.resolve(filePath).normalize();
            Resource resource = new UrlResource(path.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found: " + filePath);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found: " + filePath);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            Path path = this.fileStorageLocation.resolve(filePath).normalize();
            Files.deleteIfExists(path);
            log.info("File deleted successfully: {}", filePath);
        } catch (IOException ex) {
            log.error("Could not delete file: {}", filePath, ex);
            throw new RuntimeException("Could not delete file: " + filePath);
        }
    }

    @Override
    public String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex);
    }
}
