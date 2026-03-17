package cn.edu.qcl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * MinIO configuration properties for distributed file storage
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    /**
     * MinIO server endpoint URL
     */
    private String endpoint;
    
    /**
     * Access key for authentication
     */
    private String accessKey;
    
    /**
     * Secret key for authentication
     */
    private String secretKey;
    
    /**
     * Default bucket name for file storage
     */
    private String bucketName = "deeptrace-files";
    
    /**
     * Whether to use secure connection (HTTPS)
     */
    private boolean secure = true;
    
    /**
     * Presigned URL expiration time in seconds (default 1 hour)
     */
    private int defaultUrlExpirationSeconds = 3600;
    
    /**
     * Maximum file size in bytes (default 100MB)
     */
    private long maxFileSize = 100 * 1024 * 1024;
    
    /**
     * Region for distributed deployment
     */
    private String region;
}