package cn.edu.qcl.dto.param;

import lombok.Data;

/**
 * Metric Query Parameter
 * Used for querying metrics from flow_metrics database
 */
@Data
public class FieldOptionQueryParam {
    /**
     * Database name (e.g., "flow_metrics")
     */
    private String database;

    /**
     * Table name (e.g., "application.1m")
     */
    private String tableName;

    /**
     * Query method (e.g., "query")
     */
    private String method;

    /**
     * Field to query (e.g., "XXX")
     */
    private String field;

    /**
     * Filter condition (e.g., "time >= toDateTime('2026-04-27 09:58:26', 'Asia/Shanghai') AND time < toDateTime('2026-04-27 09:59:26', 'Asia/Shanghai')")
     */
    private String filter;
}