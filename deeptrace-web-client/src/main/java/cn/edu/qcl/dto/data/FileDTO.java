package cn.edu.qcl.dto.data;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * File data transfer object
 */
@Data
public class FileDTO {
    /**
     * Unique file identifier
     */
    private String fileId;
    
    /**
     * Original file name
     */
    private String fileName;
    
    /**
     * File path in storage
     */
    private String filePath;
    
    /**
     * File size in bytes
     */
    private Long fileSize;
    
    /**
     * Content type (MIME type)
     */
    private String contentType;
    
    /**
     * Storage bucket name
     */
    private String bucketName;
    
    /**
     * Upload user ID
     */
    private String uploadUserId;
    
    /**
     * Upload time
     */
    private LocalDateTime uploadTime;
    
    /**
     * Presigned download URL for distributed access
     */
    private String downloadUrl;
    
    /**
     * URL expiration time in seconds
     */
    private Integer urlExpirationSeconds;
}