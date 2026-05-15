package cn.edu.qcl.api;

import cn.edu.qcl.dto.data.HostDeviceDTO;
import cn.edu.qcl.dto.data.HostMetricTimeSeriesDTO;
import cn.edu.qcl.dto.param.HostDevicePageQuery;
import cn.edu.qcl.dto.param.HostMetricQueryParam;
import com.alibaba.cola.dto.PageResponse;

import java.util.List;

/**
 * Host Metric Query Service Interface
 * Provides host metric query functionality for different dimensions and aggregation types
 */
public interface HostServiceI {

    /**
     * Query host metric time series data
     * <p>
     * Query metrics for a specific host with the specified aggregation type (avg, max, min).
     * The query supports different metric dimensions (e.g., cpu_usage, memory_usage).
     * </p>
     *
     * @param queryParam the query parameters including:
     *                   virtualTableName - the virtual table name (e.g., 'zerotrace_agent_host_cpu')
     *                   host - the host identifier (e.g., 'auto-vm-202.112.237.33-W16')
     *                   metricName - the metric name to query (e.g., 'cpu_usage')
     *                   aggregationType - the aggregation type: avg, max, min
     *                   startTime - time range start (seconds timestamp)
     *                   endTime - time range end (seconds timestamp)
     *                   teamId - team ID for multi-tenant filtering
     * @return List of HostMetricTimeSeriesDTO containing time series data with minute-level aggregation
     */
    List<HostMetricTimeSeriesDTO> queryHostMetricTimeSeries(HostMetricQueryParam queryParam);

    /**
     * Query host devices with pagination and filtering
     * <p>
     * Query host devices with support for filtering by user_id, name, alias, ip.
     * Returns paginated results using PageResponse.
     * </p>
     *
     * @param query the query parameters including:
     *              pageNum - page number (0-based)
     *              pageSize - page size
     *              userId - filter by user ID (optional)
     *              name - filter by host name (fuzzy match, optional)
     *              alias - filter by alias (fuzzy match, optional)
     *              ip - filter by IP (fuzzy match, optional)
     * @return PageResponse containing list of HostDeviceDTO with pagination info
     */
    PageResponse<HostDeviceDTO> queryHostDevicePage(HostDevicePageQuery query);
}