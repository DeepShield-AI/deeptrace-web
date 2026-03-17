package cn.edu.qcl.web;

import cn.edu.qcl.api.FileServiceI;
import cn.edu.qcl.dto.data.FileDTO;
import cn.edu.qcl.file.FileEntity;
import cn.edu.qcl.file.gateway.FileStorageGateway;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * File controller for distributed file download and upload
 * Supports multiple application instances accessing the same storage
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileServiceI fileService;

    @Autowired
    private FileStorageGateway fileStorageGateway;

    /**
     * Upload file to distributed storage
     *
     * @param file Multipart file
     * @param userId User ID who uploads the file (optional)
     * @return File metadata with download URL
     */
    @PostMapping("/upload")
    public SingleResponse<FileDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "userId", required = false) String userId) {

        try {
            // Create file entity
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFileName(file.getOriginalFilename());
            fileEntity.setFileSize(file.getSize());
            fileEntity.setContentType(file.getContentType());
            fileEntity.setUploadUserId(userId);

            // Upload file
            FileEntity uploadedFile = fileStorageGateway.upload(fileEntity, file.getInputStream());

            // Generate presigned URL for immediate download
            String downloadUrl = fileStorageGateway.generateDownloadUrl(uploadedFile.getFileId(), 3600);
            uploadedFile.setDownloadUrl(downloadUrl);
            uploadedFile.setUrlExpirationSeconds(3600);

            // Convert to DTO
            FileDTO fileDTO = new FileDTO();
            fileDTO.setFileId(uploadedFile.getFileId());
            fileDTO.setFileName(uploadedFile.getFileName());
            fileDTO.setFilePath(uploadedFile.getFilePath());
            fileDTO.setFileSize(uploadedFile.getFileSize());
            fileDTO.setContentType(uploadedFile.getContentType());
            fileDTO.setBucketName(uploadedFile.getBucketName());
            fileDTO.setUploadUserId(uploadedFile.getUploadUserId());
            fileDTO.setUploadTime(uploadedFile.getUploadTime());
            fileDTO.setDownloadUrl(uploadedFile.getDownloadUrl());
            fileDTO.setUrlExpirationSeconds(uploadedFile.getUrlExpirationSeconds());

            return SingleResponse.of(fileDTO);

        } catch (Exception e) {
            log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            return SingleResponse.buildFailure("UPLOAD_ERROR", "Failed to upload file: " + e.getMessage());
        }
    }


    /**
     * Generate presigned URL for file download（生成预签名下载URL）
     * @param bucketName
     * @param fileName
     * @param expirationSeconds
     * @return
     */
    @GetMapping("/download-url/by-name")
    public SingleResponse<String> generateDownloadUrl( @RequestParam(value = "bucketName", required = false) String bucketName,
                                                  @RequestParam(value = "fileName", required = true) String fileName,
                                                  @RequestParam(value = "expirationSeconds", required = false) Integer expirationSeconds) {

        return fileService.generateDownloadUrl(bucketName,fileName, expirationSeconds);
    }



    /**
     * Download file directly (streaming response)
     * Suitable for small files or when presigned URL is not applicable
     *
     * @param fileId Unique file identifier
     * @param response HTTP response for streaming file content
     */
    @GetMapping("/download/{fileId}")
    public void downloadFile(
            @PathVariable String fileId,
            HttpServletResponse response) {

        try {
            // Get file metadata
            FileEntity fileEntity = fileStorageGateway.getFileMetadata(fileId);
            if (fileEntity == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("File not found: " + fileId);
                return;
            }

            // Set response headers
            response.setContentType(fileEntity.getContentType() != null
                    ? fileEntity.getContentType()
                    : MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setContentLengthLong(fileEntity.getFileSize());

            // Encode filename for Content-Disposition header
            String encodedFilename = URLEncoder.encode(fileEntity.getFileName(), String.valueOf(StandardCharsets.UTF_8))
                    .replace("+", "%20");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename*=UTF-8''" + encodedFilename);

            // Stream file content
            try (InputStream inputStream = fileStorageGateway.download(fileId);
                 OutputStream outputStream = response.getOutputStream()) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }

            log.info("File downloaded successfully: fileId={}, fileName={}", fileId, fileEntity.getFileName());

        } catch (Exception e) {
            log.error("Failed to download file: {}", fileId, e);
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Failed to download file: " + e.getMessage());
            } catch (Exception ex) {
                log.error("Failed to write error response", ex);
            }
        }
    }

    /**
     * Get presigned download URL for distributed access
     * Recommended for distributed deployment - allows direct download from storage
     * 
     * @param fileId Unique file identifier
     * @param expirationSeconds URL expiration time in seconds (optional, default from config)
     * @return Presigned download URL
     */
    @GetMapping("/download-url/by-id")
    public SingleResponse<String> getDownloadUrl(
            @RequestParam(value = "fileId", required = true)  String fileId,
            @RequestParam(required = false) Integer expirationSeconds) {
        
        return fileService.generateDownloadUrl(fileId, expirationSeconds);
    }

    /**
     * 已验证
     * @param bucketName
     * @param fileName
     * @return
     */
    @GetMapping("/metadata/by-name")
    public SingleResponse<FileDTO> getFileMetadata( @RequestParam(value = "bucketName", required = false) String bucketName,
                                                    @RequestParam(value = "fileName", required = true) String fileName) {
        return fileService.getFileMetadata(bucketName, fileName);
    }

    /**
     * fileId相关的 待从页面上传时验证
     * @param fileId
     * @return
     */
    @GetMapping("/metadata/by-id")
    public SingleResponse<FileDTO> getFileMetadata( @RequestParam(value = "fileId", required = true) String fileId) {
        return fileService.getFileMetadata(fileId);
    }

    /**
     * Check if file exists
     * 
     * @param fileId Unique file identifier
     * @return true if file exists
     */
    @GetMapping("/exists/{fileId}")
    public SingleResponse<Boolean> fileExists(@PathVariable String fileId) {
        return fileService.fileExists(fileId);
    }


    /**
     * Delete file from distributed storage
     * 
     * @param fileId Unique file identifier
     * @return Response indicating success or failure
     */
    @DeleteMapping("/{fileId}")
    public Response deleteFile(@PathVariable String fileId) {
        return fileService.deleteFile(fileId);
    }
}