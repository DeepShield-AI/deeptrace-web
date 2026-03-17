package cn.edu.qcl.api;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import cn.edu.qcl.dto.data.FileDTO;

/**
 * File service interface for distributed file operations
 */
public interface FileServiceI {
    
    /**
     * Get file metadata by file ID
     * @return File metadata
     */
    SingleResponse<FileDTO> getFileMetadata(String bucketName, String fileName);
    /**
     * Get file metadata by file ID
     * @return File metadata
     */
    SingleResponse<FileDTO> getFileMetadata(String fileId);
    
    /**
     * Generate presigned download URL for distributed access
     * @param fileId Unique file identifier
     * @param expirationSeconds URL expiration time in seconds
     * @return Presigned download URL
     */
    SingleResponse<String> generateDownloadUrl(String fileId, Integer expirationSeconds);
    
    /**
     * Delete file
     * @param fileId Unique file identifier
     * @return Response indicating success or failure
     */
    Response deleteFile(String fileId);
    
    /**
     * Check if file exists
     * @param fileId Unique file identifier
     * @return true if file exists
     */
    SingleResponse<Boolean> fileExists(String fileId);

    SingleResponse<String> generateDownloadUrl(String bucketName, String fileName, Integer expirationSeconds);
}