package cn.edu.qcl.file;

import cn.edu.qcl.config.MinioProperties;
import cn.edu.qcl.file.gateway.FileStorageGateway;
import cn.edu.qcl.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MinIO implementation of FileStorageGateway for distributed file storage
 * Supports multiple application instances accessing the same storage
 * 
 * MinIO 获取文件元数据的两种方式：
 * 1. 从 JSON 元数据文件获取（推荐）- 存储在 .metadata/{fileId}.json
 * 2. 从 MinIO 对象属性获取 - 使用 statObject API
 */
@Slf4j
@Component
public class FileStorageGatewayImpl implements FileStorageGateway {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioProperties minioProperties;

    /**
     * Storage path prefix for file metadata (JSON format)
     */
    private static final String METADATA_PREFIX = ".metadata/";
    
    /**
     * Storage path prefix for file content
     */
    private static final String CONTENT_PREFIX = "files/";

    /**
     *
     * fileId 是业务设计的概念，用于提供更友好的文件管理接口，不是 MinIO 原生概念。
     * @param fileEntity File metadata
     * @param inputStream File content stream
     * @return
     */
    @Override
    public FileEntity upload(FileEntity fileEntity, InputStream inputStream) {
        try {
            // Generate unique file ID if not provided
            //将fileId保存到对象属性
            if (fileEntity.getFileId() == null || fileEntity.getFileId().isEmpty()) {
                fileEntity.setFileId(UUID.randomUUID().toString());
            }
            
            // Set default bucket name if not provided
            if (fileEntity.getBucketName() == null || fileEntity.getBucketName().isEmpty()) {
                fileEntity.setBucketName(minioProperties.getBucketName());
            }
            
            // Ensure bucket exists
            ensureBucketExists(fileEntity.getBucketName());
            
            // Generate file path
            String filePath = CONTENT_PREFIX + fileEntity.getFileId() + "/" + fileEntity.getFileName();
            fileEntity.setFilePath(filePath);
            
            // Set upload time
            fileEntity.setUploadTime(LocalDateTime.now());
            
            // Upload file content with user metadata
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(fileEntity.getBucketName())
                    .object(filePath)
                    .stream(inputStream, fileEntity.getFileSize(), minioProperties.getMaxFileSize())
                    .contentType(fileEntity.getContentType())
                    .userMetadata(createUserMetadata(fileEntity))
                    .build()
            );
            
            // Store file metadata separately as JSON
            //存储元数据映射
            storeMetadata(fileEntity);
            
            log.info("File uploaded successfully: fileId={}, path={}", fileEntity.getFileId(), filePath);
            return fileEntity;
            
        } catch (Exception e) {
            log.error("Failed to upload file: {}", fileEntity.getFileName(), e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    @Override
    public InputStream download(String fileId) {
        try {
            FileEntity metadata = getFileMetadata(fileId);
            if (metadata == null) {
                throw new RuntimeException("File not found: " + fileId);
            }
            
            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(metadata.getBucketName())
                    .object(metadata.getFilePath())
                    .build()
            );
            
        } catch (Exception e) {
            log.error("Failed to download file: {}", fileId, e);
            throw new RuntimeException("Failed to download file: " + e.getMessage(), e);
        }
    }

    /**
     * Get file metadata by fileId
     * 
     * 方式1：从存储的 JSON 元数据文件获取（推荐）
     * 元数据存储路径：.metadata/{fileId}.json
     * 
     * @param fileId Unique file identifier
     * @return File entity with metadata
     */
    @Override
    public FileEntity getFileMetadata(String fileId) {
        try {
            // 从 MinIO 读取 JSON 格式的元数据文件
            String metadataPath = METADATA_PREFIX + fileId + ".json";
            
            InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(metadataPath)
                    .build()
            );
            
            // 解析 JSON 为 FileEntity
            // Read all bytes from the input stream
            byte[] buffer = new byte[8192];
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            int bytesRead;
            while ((bytesRead = stream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            String json = baos.toString(StandardCharsets.UTF_8.name());
            stream.close();
            
            return JSON.parseObject(json, FileEntity.class);
            
        } catch (Exception e) {
            log.warn("Failed to get file metadata from JSON: {}", fileId, e);
            return null;
        }
    }

    /**
     * Get file metadata by bucket name and file name
     * 
     * 方式2：从 MinIO 对象属性获取元数据
     * 使用 statObject API 获取对象的基本信息（大小、contentType、etag、userMetadata等）
     * 
     * @param fileName Object name (file path in bucket)
     * @return File entity with metadata
     */
    @Override
    public FileEntity getFileMetadata(String bucketName, String fileName) {
        if (StringUtils.isEmpty(bucketName)){
            bucketName = minioProperties.getBucketName();
        }
        try {
            // 使用 statObject 获取对象元数据
            StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(fileName)
                    .build()
            );
            
            // 从 StatObjectResponse 构建FileEntity
            FileEntity metadata = new FileEntity();
            metadata.setBucketName(minioProperties.getBucketName());
            metadata.setFilePath(fileName);
            metadata.setFileSize(stat.size());
            metadata.setContentType(stat.contentType());
            metadata.setChecksum(stat.etag()); // ETag 通常是文件的 MD5 值
            
            // 从 userMetadata 获取自定义元数据
            Map<String, String> userMetadata = stat.userMetadata();
            if (userMetadata != null) {
                metadata.setFileId(userMetadata.get("file-id"));
                metadata.setFileName(userMetadata.get("original-filename"));
                metadata.setUploadUserId(userMetadata.get("upload-user-id"));
            }
            
            // 从文件路径提取文件名（如果 userMetadata 中没有）
            if (metadata.getFileName() == null && fileName != null) {
                int lastSlash = fileName.lastIndexOf('/');
                if (lastSlash >= 0 && lastSlash < fileName.length() - 1) {
                    metadata.setFileName(fileName.substring(lastSlash + 1));
                } else {
                    metadata.setFileName(fileName);
                }
            }
            
            // 获取最后修改时间
            if (stat.lastModified() != null) {
                metadata.setUploadTime(LocalDateTime.ofInstant(
                    stat.lastModified().toInstant(), 
                    java.time.ZoneId.systemDefault()
                ));
            }
            
            return metadata;
            
        } catch (Exception e) {
            log.warn("Failed to get file metadata from MinIO statObject: bucket={}, object={}", bucketName, fileName, e);
            return null;
        }
    }

    @Override
    public String generateDownloadUrl(String fileId, int expirationSeconds) {
        try {
            FileEntity metadata = getFileMetadata(fileId);
            if (metadata == null) {
                throw new RuntimeException("File not found: " + fileId);
            }
            
            return generateDownloadUrl(metadata.getBucketName(), metadata.getFilePath(), expirationSeconds);
            
        } catch (Exception e) {
            log.error("Failed to generate presigned URL: {}", fileId, e);
            throw new RuntimeException("Failed to generate presigned URL: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get download URL for file by bucket name and object name
     * 生成预签名下载URL（有时效限制）
     *
     * @param bucketName Bucket name
     * @param objectName Object name (file path in bucket)
     * @param expirationSeconds URL expiration time in seconds
     * @return Presigned download URL
     */
    @Override
    public String generateDownloadUrl(String bucketName, String objectName, Integer expirationSeconds) {
        if (StringUtils.isEmpty(bucketName)){
            bucketName = minioProperties.getBucketName();
        }
        if (expirationSeconds == null ||expirationSeconds < 0 ){
            expirationSeconds = minioProperties.getDefaultUrlExpirationSeconds();
        }

        try {
            // 生成预签名下载URL（有时效限制）
            String downloadUrl = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)                           // HTTP GET 方法
                    .bucket(bucketName)                           // bucket 名称
                    .object(objectName)                           // 对象路径
                    .expiry(expirationSeconds, TimeUnit.SECONDS)  // 过期时间（秒）
                    .build()
            );
            
            log.info("Generated download URL: bucket={}, object={}, expiration={}s", bucketName, objectName, expirationSeconds);
            return downloadUrl;
            
        } catch (Exception e) {
            log.error("Failed to get download URL: bucket={}, object={}", bucketName, objectName, e);
            throw new RuntimeException("Failed to get download URL: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(String fileId) {
        try {
            FileEntity metadata = getFileMetadata(fileId);
            if (metadata == null) {
                log.warn("File not found for deletion: {}", fileId);
                return false;
            }
            
            // Delete file content
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(metadata.getBucketName())
                    .object(metadata.getFilePath())
                    .build()
            );
            
            // Delete metadata JSON file
            String metadataPath = METADATA_PREFIX + fileId + ".json";
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(metadataPath)
                    .build()
            );
            
            log.info("File deleted successfully: fileId={}", fileId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to delete file: {}", fileId, e);
            return false;
        }
    }

    @Override
    public boolean exists(String fileId) {
        try {
            FileEntity metadata = getFileMetadata(fileId);
            return metadata != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Ensure the bucket exists, create if not
     */
    private void ensureBucketExists(String bucketName) throws Exception {
        boolean exists = minioClient.bucketExists(
            BucketExistsArgs.builder()
                .bucket(bucketName)
                .build()
        );
        
        if (!exists) {
            minioClient.makeBucket(
                MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build()
            );
            log.info("Created bucket: {}", bucketName);
        }
    }

    /**
     * Create user metadata map from file entity
     * These metadata will be stored with the object in MinIO
     */
    private Map<String, String> createUserMetadata(FileEntity fileEntity) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("file-id", fileEntity.getFileId());
        metadata.put("original-filename", fileEntity.getFileName());
        if (fileEntity.getUploadUserId() != null) {
            metadata.put("upload-user-id", fileEntity.getUploadUserId());
        }
        return metadata;
    }

    /**
     * Store file metadata separately as JSON for quick access
     * 存储路径：.metadata/{fileId}.json
     */
    private void storeMetadata(FileEntity fileEntity) throws Exception {
        String metadataPath = METADATA_PREFIX + fileEntity.getFileId() + ".json";
        String json = JSON.toJSONString(fileEntity);
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(metadataPath)
                .stream(new ByteArrayInputStream(jsonBytes), jsonBytes.length, -1)
                .contentType("application/json")
                .build()
        );
        
        log.debug("Stored metadata: path={}", metadataPath);
    }
}