package cn.edu.qcl.host;

import cn.edu.qcl.api.HostServiceI;
import cn.edu.qcl.dto.data.HostDeviceDTO;
import cn.edu.qcl.dto.data.HostMetricTimeSeriesDTO;
import cn.edu.qcl.dto.param.HostDevicePageQuery;
import cn.edu.qcl.dto.param.HostMetricQueryParam;
import cn.edu.qcl.enums.HostMetricTypeEnum;
import cn.edu.qcl.host.gateway.HostDeviceGateway;
import cn.edu.qcl.mapper.clickhouse.ClickHouseMapper;
import com.alibaba.cola.dto.PageResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Host Metric Query Service Implementation
 * Handles host metric queries with different aggregation types
 */
@Slf4j
@Service
public class HostServiceImpl implements HostServiceI {

    @Resource
    private ClickHouseMapper clickHouseMapper;

    @Resource
    private HostDeviceGateway hostDeviceGateway;

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
    @Override
    public List<HostMetricTimeSeriesDTO> queryHostMetricTimeSeries(HostMetricQueryParam queryParam) {
        queryParam.setVirtualTableName(HostMetricTypeEnum.getVirtualTableNameByKey(queryParam.getMetricCategory()));

        // Validate input parameters
        validateHostMetricQueryParam(queryParam);

        log.info("Querying host metric time series with params: virtualTableName={}, host={}, metricName={}, aggregationType={}, startTime={}, endTime={}, teamId={}",
                queryParam.getVirtualTableName(), queryParam.getHost(), queryParam.getMetricName(),
                queryParam.getAggregationType(), queryParam.getStartTime(), queryParam.getEndTime(),
                queryParam.getTeamId());

        // Execute SQL query
        List<HostMetricTimeSeriesDTO> timeSeries = clickHouseMapper.queryHostMetricTimeSeries(queryParam);
        log.info("Host metric time series query returned {} records", timeSeries.size());

        return timeSeries;
    }

    /**
     * Validate query parameters
     *
     * @param queryParam the query parameters to validate
     */
    private void validateHostMetricQueryParam(HostMetricQueryParam queryParam) {
        if (queryParam == null) {
            throw new IllegalArgumentException("Query parameters cannot be null");
        }
        if (!StringUtils.hasText(queryParam.getVirtualTableName())) {
            throw new IllegalArgumentException("Virtual table name is required");
        }
        if (!StringUtils.hasText(queryParam.getHost())) {
            throw new IllegalArgumentException("Host is required");
        }
        if (!StringUtils.hasText(queryParam.getMetricName())) {
            throw new IllegalArgumentException("Metric name is required");
        }
        if (!StringUtils.hasText(queryParam.getAggregationType())) {
            throw new IllegalArgumentException("Aggregation type is required");
        }
        // Validate aggregation type
        String aggType = queryParam.getAggregationType().toLowerCase();
        if (!aggType.equals("avg") && !aggType.equals("max") && !aggType.equals("min")) {
            throw new IllegalArgumentException("Aggregation type must be one of: avg, max, min");
        }
    }

    /**
     * Query host devices with pagination and filtering
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
    @Override
    public PageResponse<HostDeviceDTO> queryHostDevicePage(HostDevicePageQuery query) {
        log.info("Querying host devices with params: pageNum={}, pageSize={}, userId={}, name={}, alias={}, ip={}",
                query.getPageIndex(), query.getPageSize(), query.getUserId(), query.getName(), query.getAlias(), query.getIp());

        // Query total count
        long totalCount = hostDeviceGateway.count(query);

        // Query data list
        List<HostDeviceDTO> dataList = hostDeviceGateway.queryByPage(query);

        log.info("Host device query returned {} records, total count: {}", dataList.size(), totalCount);

        // Build PageResponse
        return PageResponse.of(dataList, (int) totalCount, query.getPageIndex(), query.getPageSize());
    }
}