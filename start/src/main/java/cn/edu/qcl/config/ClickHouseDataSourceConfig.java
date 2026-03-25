package cn.edu.qcl.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


/**
 * ClickHouse DataSource + MyBatis 配置
 */
@Configuration
@MapperScan(basePackages = "cn.edu.qcl.mapper.clickhouse", sqlSessionFactoryRef = "clickhouseSqlSessionFactory")
public class ClickHouseDataSourceConfig {

    @Value("${clickhouse.datasource.url}")
    private String jdbcUrl;

    @Value("${clickhouse.datasource.username:default}")
    private String username;

    @Value("${clickhouse.datasource.password:}")
    private String password;

    @Value("${clickhouse.datasource.connection-timeout:10000}")
    private long connectionTimeout;

    @Value("${clickhouse.datasource.socket-timeout:300000}")
    private long socketTimeout;

    @Bean(name = "clickhouseDataSource")
    public DataSource clickhouseDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.clickhouse.jdbc.ClickHouseDriver");

        //连接池配置
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        // ClickHouse 特定属性
        config.addDataSourceProperty("socket_timeout", socketTimeout);
        config.addDataSourceProperty("connection_timeout", connectionTimeout);
        return new HikariDataSource(config);
    }

    @Bean(name = "clickhouseTransactionManager")
    public DataSourceTransactionManager clickhouseTransactionManager(@Qualifier("clickhouseDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "clickhouseSqlSessionFactory")
    public SqlSessionFactory clickhouseSqlSessionFactory(@Qualifier("clickhouseDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        // ClickHouse-specific mapper xml 的位置
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:mapper/clickhouse/*.xml"));
        return sessionFactory.getObject();
    }
}