package cn.edu.qcl.external;

import java.util.List;
import java.util.Map;

/**
 * Get Trace Info by DeepFlow API Gateway Interface
 * Defines the contract for executing metric queries via HTTP API
 * 
 * Security: Uses only the configured API URL to prevent SSRF attacks
 */
public interface TraceByAPIQueryGateway {

    /**
     * Execute a SQL query against the specified database
     * Uses the configured API URL from application properties
     *
     * @param db     Database name (e.g., "flow_metrics")
     * @param sql    SQL query to execute
     * @return List of result rows as Map<String, Object>
     */
    List<Map<String, Object>> executeQuery(String db, String sql);
}