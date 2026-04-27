/*
package cn.edu.qcl.config;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseCredentials;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.client.ClickHouseProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

*/
/**
 * ClickHouse Java Client 配置
 * 使用官方 clickhouse-client 实现高性能连接管理
 * 
 * 版本: clickhouse-jdbc 0.9.8
 *//*

@Configuration
public class ClickHouseClientConfig {

    @Value("${clickhouse.datasource.url}")
    private String jdbcUrl;

    @Value("${clickhouse.datasource.username:default}")
    private String username;

    @Value("${clickhouse.datasource.password:}")
    private String password;

    */
/**
     * 使用 ClickHouse Java Client（官方原生客户端），通过 HTTP 协议 连接 ClickHouse
     * 创建 ClickHouse Client 单例
     * 内置连接池管理，支持高并发查询
     *//*

    @Bean(destroyMethod = "close")
    public ClickHouseClient clickHouseClient() {
        return ClickHouseClient.newInstance();
    }

    */
/**
     * 创建 ClickHouse 服务器节点配置
     * 使用的是 ClickHouse Java Client（官方原生客户端），通过 HTTP 协议 连接 ClickHouse
     *//*

    @Bean
    public ClickHouseNode clickHouseNode() {
        // 解析 JDBC URL: jdbc:clickhouse://host:port/database
        String cleanUrl = jdbcUrl.replace("jdbc:clickhouse://", "");
        String[] parts = cleanUrl.split("/");
        String hostPort = parts[0];
//        String database = parts.length > 1 ? parts[1] : "default";
        
        String[] hostPortParts = hostPort.split(":");
        String host = hostPortParts[0];
        int port = hostPortParts.length > 1 ? Integer.parseInt(hostPortParts[1]) : 8123;

        return ClickHouseNode.builder()
                .host(host)
                .port(ClickHouseProtocol.HTTP, port)
//                .database(database)
                .credentials(ClickHouseCredentials.fromUserAndPassword(username, password))
                .build();
    }
}*/
