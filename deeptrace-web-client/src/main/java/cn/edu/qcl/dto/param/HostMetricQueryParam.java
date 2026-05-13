package cn.edu.qcl.dto.param;

import lombok.Data;

/**
 * Host Metric Query Parameter
 * Used for querying host metrics with different dimensions and aggregation types
 */
@Data
public class HostMetricQueryParam {
    /**
     * Metric category (e.g., "cpu", "memory", "network", "disk")
     */
    private String metricCategory;
    /**
     * Virtual table name (e.g., 'zerotrace_agent_host_cpu')
     */
    private String virtualTableName;

    /**
     * Host identifier (e.g., 'auto-vm-202.112.237.33-W16')
     */
    private String host;

    /**
     * Metric name to query (e.g., 'cpu_usage', 'memory_usage', etc.)
     */
    private String metricName;

    /**
     * Aggregation type: avg, max, min
     */
    private String aggregationType;

    /**
     * Time range start (seconds timestamp)
     */
    private Long startTime;

    /**
     * Time range end (seconds timestamp)
     */
    private Long endTime;

    /**
     * Team ID for multi-tenant filtering
     */
    private Long teamId;
}