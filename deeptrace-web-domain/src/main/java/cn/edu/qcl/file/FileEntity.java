package cn.edu.qcl.file;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * File entity for distributed file storage
 */
@Data
public class FileEntity {
    /**
     * Unique file identifier
     *  使用 fileId 作为唯一标识，防止用户上传同名文件被覆盖
     */
    private String fileId;
    
    /**
     * Original file name
     */
    private String fileName;
    
    /**
     * File path in storage (bucket/path/filename)
     * files/{fileId}/ {filename}
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
     * File checksum (MD5/SHA256)
     */
    private String checksum;
    
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
     * Download URL (presigned URL for distributed access)
     */
    private String downloadUrl;
    
    /**
     * URL expiration time in seconds
     */
    private Integer urlExpirationSeconds;
}