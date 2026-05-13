package cn.edu.qcl.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Host Metric Type Enum
 * Defines the virtual table names for different host metric dimensions
 */
@Getter
@AllArgsConstructor
public enum HostMetricTypeEnum {
    /**
     * CPU related metrics
     */
    CPU("cpu", "zerotrace_agent_host_cpu"),

    /**
     * Memory related metrics
     */
    MEMORY("memory", "zerotrace_agent_host_memory"),

    /**
     * Network interface send/receive metrics
     */
    NETWORK("network", "zerotrace_agent_host_network"),

    /**
     * Disk read/write and latency metrics
     */
    DISK("disk", "zerotrace_agent_host_disk");

    /**
     * Metric type key (e.g., "cpu", "memory")
     */
    private final String metricCategory;

    /**
     * Virtual table name in ClickHouse
     */
    private final String virtualTableName;

    /**
     * Get HostMetricTypeEnum by key
     *
     * @param key the metric type key
     * @return HostMetricTypeEnum or null if not found
     */
    public static HostMetricTypeEnum getByKey(String key) {
        if (key == null) {
            return null;
        }
        for (HostMetricTypeEnum type : values()) {
            if (type.getMetricCategory().equalsIgnoreCase(key)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Get virtual table name by key
     *
     * @param key the metric type key
     * @return virtual table name or null if not found
     */
    public static String getVirtualTableNameByKey(String key) {
        HostMetricTypeEnum type = getByKey(key);
        return type != null ? type.getVirtualTableName() : null;
    }

    /**
     * Check if the key is valid
     *
     * @param key the metric type key
     * @return true if valid, false otherwise
     */
    public static boolean isValidKey(String key) {
        return getByKey(key) != null;
    }
}