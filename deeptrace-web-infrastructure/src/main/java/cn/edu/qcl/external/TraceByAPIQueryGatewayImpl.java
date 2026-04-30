package cn.edu.qcl.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Get Trace Info by DeepFlow API Gateway Implementation
 * Executes HTTP POST requests to the ClickHouse HTTP API using Apache HttpClient 5
 * 
 * Features:
 * - Connection pooling for better performance
 * - Uses configured API URL only (prevents SSRF)
 * - Form-urlencoded body matching curl command:
 *   curl -X POST "http://ip:port/v1/query/" -d 'db=flow_metrics' --data-urlencode 'sql=SELECT ...'
 * 
 * Security:
 * - Only uses the configured API URL to prevent SSRF attacks
 */
@Repository
public class TraceByAPIQueryGatewayImpl implements TraceByAPIQueryGateway {

    private static final Logger log = LoggerFactory.getLogger(TraceByAPIQueryGatewayImpl.class);
    private static final String QUERY_ENDPOINT = "/v1/query/";

    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    @Value("${metric.api.url:http://202.112.237.37:20416}")
    private String apiUrl;

    /**
     * Dangerous SQL keywords that should be blocked
     */
    private static final Set<String> DANGEROUS_KEYWORDS = new HashSet<>(Arrays.asList(
            "DROP", "DELETE", "TRUNCATE", "INSERT", "UPDATE", "ALTER", "CREATE",
            "GRANT", "REVOKE", "EXEC", "EXECUTE", "MERGE", "CALL"
    ));

    public TraceByAPIQueryGatewayImpl(@Qualifier("metricApiClient") CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Map<String, Object>> executeQuery(String db, String sql) {
        log.info("Executing metric query - API: {}, DB: {}, SQL: {}", apiUrl, db, sql);
        
        // 1.Validate sql
        String validationError = validateSqlQuery(sql);
        if (validationError != null) {
            throw new RuntimeException("sql is invalidate，  " + validationError);
        }
        // 2.Validate database name
        if (db == null || db.trim().isEmpty()) {
            throw new RuntimeException("Database name is required");
        }

        // 3.Build the full URL using configured API URL
        String fullUrl = apiUrl.endsWith("/") 
            ? apiUrl + "v1/query/" 
            : apiUrl + QUERY_ENDPOINT;

        HttpPost httpPost = new HttpPost(fullUrl);
        
        try {
            // 4.Prepare form data (application/x-www-form-urlencoded)
            List<NameValuePair> formData = new ArrayList<>();
            formData.add(new BasicNameValuePair("db", db));
            formData.add(new BasicNameValuePair("sql", sql));
            
            httpPost.setEntity(new UrlEncodedFormEntity(formData, StandardCharsets.UTF_8));
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            log.debug("Sending POST request to: {}", fullUrl);

            // 5.Execute request and handle response
            return httpClient.execute(httpPost, response -> {
                int statusCode = response.getCode();
                log.debug("Response status: {}", statusCode);

                if (statusCode >= 200 && statusCode < 300) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String responseBody = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                        log.debug("Response body: {}", responseBody);
                        return parseResponse(responseBody);
                    }
                }
                
                // Handle error response
                HttpEntity errorEntity = response.getEntity();
                String errorBody = errorEntity != null ? EntityUtils.toString(errorEntity, StandardCharsets.UTF_8) : "No error details";
                log.error("Query failed with status: {}, body: {}", statusCode, errorBody);
                throw new RuntimeException("Query failed with status: " + statusCode + ", body: " + errorBody);
            });

        } catch (Exception e) {
            log.error("Failed to execute metric query: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to execute metric query", e);
        }
    }


    /**
     * 将API返回的结果解析为List<Map<String, Object>>格式
     * @param responseBody
     * @return
     */
    public static List<Map<String, Object>> parseResponse(String responseBody) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse response as JSON {}", e.getMessage());
            throw new RuntimeException("Failed to parse API response", e);
        }

        // 兼容外层包裹result结构
        JsonNode resultNode = root.has("result") ? root.get("result") : root;

        // 获取列名
        List<String> columns = new ArrayList<>();
        for (JsonNode col : resultNode.get("columns")) {
            columns.add(col.asText());
        }

        // 获取数据
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (JsonNode valueRow : resultNode.get("values")) {
            Map<String, Object> rowMap = new LinkedHashMap<>();
            for (int i = 0; i < columns.size(); i++) {
                rowMap.put(columns.get(i), valueRow.get(i).asText());
            }
            dataList.add(rowMap);
        }
        return dataList;
    }

    /**
     * Validate SQL query for security
     *
     * @param sql The SQL query to validate
     * @return Error message if validation fails, null if valid
     */
    private String validateSqlQuery(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return "SQL query is required";
        }

        String upperSql = sql.toUpperCase().trim();

        // Must be a SELECT statement
        if (!upperSql.startsWith("SELECT")) {
            return "Only SELECT queries are allowed";
        }

        // Check for dangerous keywords
        for (String keyword : DANGEROUS_KEYWORDS) {
            // Use word boundary check to avoid false positives
            if (upperSql.matches(".*\\b" + keyword + "\\b.*")) {
                return "Forbidden SQL keyword detected: " + keyword;
            }
        }

        // Check for comment injection
        if (sql.contains("--") || sql.contains("/*") || sql.contains("*/")) {
            return "SQL comments are not allowed";
        }

        // Check for semicolon (multiple statement injection)
        if (sql.contains(";")) {
            return "Multiple statements are not allowed (semicolon detected)";
        }

        return null; // Valid
    }
    /**
     * Parse the JSON response from the API
     * Expected format: [{"col1": val1, "col2": val2, ...}, ...]
     */
    /*private List<Map<String, Object>> parseResponse(String responseBody) {
        try {
            // Try to parse as a JSON array
            List<Map<String, Object>> result = objectMapper.readValue(
                responseBody,
                new TypeReference<List<Map<String, Object>>>() {}
            );
            log.info("Query returned {} records", result.size());
            return result;
        } catch (Exception e) {
            log.warn("Failed to parse response as JSON array, attempting alternative parsing: {}", e.getMessage());
            
            // If parsing fails, return empty list or try alternative format
            try {
                // Try parsing as a single object
                Map<String, Object> singleResult = objectMapper.readValue(
                    responseBody,
                    new TypeReference<Map<String, Object>>() {}
                );
                List<Map<String, Object>> result = new ArrayList<>();
                result.add(singleResult);
                return result;
            } catch (Exception ex) {
                log.error("Failed to parse response body: {}", responseBody, ex);
                throw new RuntimeException("Failed to parse API response", ex);
            }
        }
    }*/
}