package cn.edu.qcl.dto.param;

import lombok.Data;

/**
 * Graph Node Metrics Query Parameter
 * Used for querying graph node metrics from flow_metrics database
 */
@Data
public class GraphMetricsQueryParam {
    /**
     * Database name (e.g., "flow_metrics")
     */
    private String database;

    /**
     * Table name (e.g., "application.1m")
     */
    private String tableName;

    /**
     * Filter condition SQL (e.g., "time >= toDateTime(?, 'Asia/Shanghai') and time < toDateTime(?, 'Asia/Shanghai') and app_service=redis and az_id in (11,22)")
     */
    private String filter;

    /**
     * Team ID for multi-tenant filtering
     */
    private Long teamId;
}