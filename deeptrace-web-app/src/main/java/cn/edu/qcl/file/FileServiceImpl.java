package cn.edu.qcl.file;

import cn.edu.qcl.api.FileServiceI;
import cn.edu.qcl.dto.data.FileDTO;
import cn.edu.qcl.file.gateway.FileStorageGateway;
import com.alibaba.cola.catchlog.CatchAndLog;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * File service implementation for distributed file operations
 */
@Slf4j
@Service
@CatchAndLog
public class FileServiceImpl implements FileServiceI {

    @Resource
    private FileStorageGateway fileStorageGateway;

    @Override
    public SingleResponse<FileDTO> getFileMetadata(String bucketName, String fileName) {
        try {
            cn.edu.qcl.file.FileEntity fileEntity = fileStorageGateway.getFileMetadata(bucketName,fileName);
            if (fileEntity == null) {
                return SingleResponse.buildFailure("FILE_NOT_FOUND", "File not found: " + fileName);
            }
            
            FileDTO fileDTO = convertToDTO(fileEntity);
            return SingleResponse.of(fileDTO);
            
        } catch (Exception e) {
            log.error("Failed to get file metadata: {}", fileName, e);
            return SingleResponse.buildFailure("GET_FILE_ERROR", "Failed to get file metadata: " + e.getMessage());
        }
    }

    @Override
    public SingleResponse<FileDTO> getFileMetadata(String fileId) {
        try {
            cn.edu.qcl.file.FileEntity fileEntity = fileStorageGateway.getFileMetadata(fileId);
            if (fileEntity == null) {
                return SingleResponse.buildFailure("FILE_NOT_FOUND", "File not found: " + fileId);
            }

            FileDTO fileDTO = convertToDTO(fileEntity);
            return SingleResponse.of(fileDTO);

        } catch (Exception e) {
            log.error("Failed to get file metadata: {}", fileId, e);
            return SingleResponse.buildFailure("GET_FILE_ERROR", "Failed to get file metadata: " + e.getMessage());
        }
    }


    @Override
    public SingleResponse<String> generateDownloadUrl(String fileId, Integer expirationSeconds) {
        try {
            // Check if file exists
            if (!fileStorageGateway.exists(fileId)) {
                return SingleResponse.buildFailure("FILE_NOT_FOUND", "File not found: " + fileId);
            }
            
            String url = fileStorageGateway.generateDownloadUrl(fileId, expirationSeconds);
            return SingleResponse.of(url);
            
        } catch (Exception e) {
            log.error("Failed to generate download URL: {}", fileId, e);
            return SingleResponse.buildFailure("GENERATE_URL_ERROR", "Failed to generate download URL: " + e.getMessage());
        }
    }
    
    @Override
    public SingleResponse<String> generateDownloadUrl(String bucketName, String fileName, Integer expirationSeconds) {
        try {

            String url = fileStorageGateway.generateDownloadUrl(bucketName, fileName, expirationSeconds);
            return SingleResponse.of(url);

        } catch (Exception e) {
            log.error("Failed to generate download URL: {}", fileName, e);
            return SingleResponse.buildFailure("GENERATE_URL_ERROR", "Failed to generate download URL: " + e.getMessage());
        }
    }

    @Override
    public Response deleteFile(String fileId) {
        try {
            boolean deleted = fileStorageGateway.delete(fileId);
            if (deleted) {
                return Response.buildSuccess();
            } else {
                return Response.buildFailure("DELETE_FAILED", "Failed to delete file: " + fileId);
            }
        } catch (Exception e) {
            log.error("Failed to delete file: {}", fileId, e);
            return Response.buildFailure("DELETE_ERROR", "Failed to delete file: " + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Boolean> fileExists(String fileId) {
        try {
            boolean exists = fileStorageGateway.exists(fileId);
            return SingleResponse.of(exists);
        } catch (Exception e) {
            log.error("Failed to check file existence: {}", fileId, e);
            return SingleResponse.of(false);
        }
    }

    /**
     * Convert FileEntity to FileDTO
     */
    private FileDTO convertToDTO(cn.edu.qcl.file.FileEntity entity) {
        FileDTO dto = new FileDTO();
        dto.setFileId(entity.getFileId());
        dto.setFileName(entity.getFileName());
        dto.setFilePath(entity.getFilePath());
        dto.setFileSize(entity.getFileSize());
        dto.setContentType(entity.getContentType());
        dto.setBucketName(entity.getBucketName());
        dto.setUploadUserId(entity.getUploadUserId());
        dto.setUploadTime(entity.getUploadTime());
        dto.setDownloadUrl(entity.getDownloadUrl());
        dto.setUrlExpirationSeconds(entity.getUrlExpirationSeconds());
        return dto;
    }
}