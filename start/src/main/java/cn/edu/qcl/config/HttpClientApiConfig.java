package cn.edu.qcl.config;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metric API HTTP Client Configuration
 * Configures Apache HttpClient 5 for querying custom ClickHouse HTTP API
 * 
 * Features:
 * - Connection pooling for better performance
 * - Configurable timeouts
 * - Automatic connection management
 */
@Slf4j
@Configuration
public class HttpClientApiConfig {

    /**
     * -- GETTER --
     *  Get the configured metric API base URL
     */
    @Getter
    @Value("${metric.api.url:http://202.112.237.37:20416}")
    private String metricApiUrl;

    @Value("${metric.api.connection-timeout:10000}")
    private int connectionTimeout;

    @Value("${metric.api.read-timeout:30000}")
    private int readTimeout;

    @Value("${metric.api.connection-request-timeout:5000}")
    private int connectionRequestTimeout;

    @Value("${metric.api.max-total-connections:50}")
    private int maxTotalConnections;

    @Value("${metric.api.max-connections-per-route:20}")
    private int maxConnectionsPerRoute;

    private CloseableHttpClient httpClient;

    /**
     * Create connection pool manager
     * 连接复用：管理一组可复用的 HTTP 连接，避免每次请求都创建新连接
     * 并发控制：限制同时存在的连接数量，防止资源耗尽
     * 性能优化：通过连接复用减少 TCP 握手开销，提高请求效率
     */
    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {
        return PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(maxTotalConnections)
                .setMaxConnPerRoute(maxConnectionsPerRoute)
                // 连接空闲超过10秒后，使用前会先验证是否仍然有效
                .setValidateAfterInactivity(TimeValue.ofSeconds(10))
                .build();
    }

    /**
     * Create Apache HttpClient 5 bean with connection pooling
     * 组装客户端：将连接池、超时配置等组合成一个完整的 HTTP 客户端
     * 统一管理：提供全局统一的 HTTP 请求配置（超时、重试等）
     * 资源清理：自动清理长时间不用的空闲连接，释放资源
     */
    @Bean(name = "metricApiClient")
    public CloseableHttpClient metricApiClient(PoolingHttpClientConnectionManager connectionManager) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(connectionTimeout))
                .setResponseTimeout(Timeout.ofMilliseconds(readTimeout))
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(connectionRequestTimeout))
                .build();

        this.httpClient = HttpClients.custom()
                //使用已创建连接池管理器
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                // 清理30秒未使用的空闲连接
                .evictIdleConnections(TimeValue.ofSeconds(30))
                .build();

        return this.httpClient;
    }

    /**
     * Cleanup resources on shutdown
     */
    @PreDestroy
    public void destroy() {
        if (httpClient != null) {
            try {
                log.info("Closing metric API HTTP client");
                httpClient.close();
            } catch (Exception e) {
                log.warn("Failed to close HTTP client: {}", e.getMessage());
            }
        }
    }
}