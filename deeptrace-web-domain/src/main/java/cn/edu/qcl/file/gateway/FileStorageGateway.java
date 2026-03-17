package cn.edu.qcl.file.gateway;

import cn.edu.qcl.file.FileEntity;
import java.io.InputStream;

/**
 * File storage gateway interface for distributed file storage
 * Supports MinIO, AWS S3, Aliyun OSS, etc.
 */
public interface FileStorageGateway {
    
    /**
     * Upload file to distributed storage
     * @param fileEntity File metadata
     * @param inputStream File content stream
     * @return File entity with storage information
     */
    FileEntity upload(FileEntity fileEntity, InputStream inputStream);
    
    /**
     * Download file from distributed storage
     * @param fileId Unique file identifier
     * @return File content stream
     */
    InputStream download(String fileId);
    
    /**
     * Get file metadata
     * @return File entity with metadata
     */
    FileEntity getFileMetadata(String bucketName, String fileName);

    /**
     * Get file metadata
     * @return File entity with metadata
     */
    FileEntity getFileMetadata(String fileId);
    
    /**
     * Generate presigned download URL for distributed access
     * @param fileId Unique file identifier
     * @param expirationSeconds URL expiration time in seconds
     * @return Presigned download URL
     */
    String generateDownloadUrl(String fileId, int expirationSeconds);
    
    /**
     * Get download URL for file by bucket name and object name
     * @param bucketName Bucket name
     * @param objectName Object name (file path in bucket)
     * @param expirationSeconds URL expiration time in seconds
     * @return Presigned download URL
     */
    String generateDownloadUrl(String bucketName, String objectName, Integer expirationSeconds);
    
    /**
     * Delete file from distributed storage
     * @param fileId Unique file identifier
     * @return true if deleted successfully
     */
    boolean delete(String fileId);
    
    /**
     * Check if file exists
     * @param fileId Unique file identifier
     * @return true if file exists
     */
    boolean exists(String fileId);
}