package cn.edu.qcl.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO client configuration for distributed file storage
 */
@Configuration
public class MinioConfig {
    
    @Autowired
    private MinioProperties minioProperties;
    
    /**
     * Create MinIO client bean for distributed storage access
     */
    @Bean
    public MinioClient minioClient() {
        MinioClient.Builder builder = MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey());
        
        if (minioProperties.getRegion() != null && !minioProperties.getRegion().isEmpty()) {
            builder.region(minioProperties.getRegion());
        }
        
        return builder.build();
    }
}