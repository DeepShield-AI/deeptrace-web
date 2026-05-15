package cn.edu.qcl.web;

import cn.edu.qcl.api.HostServiceI;
import cn.edu.qcl.dto.data.HostDeviceDTO;
import cn.edu.qcl.dto.data.HostMetricTimeSeriesDTO;
import cn.edu.qcl.dto.param.HostDevicePageQuery;
import cn.edu.qcl.dto.param.HostMetricQueryParam;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Host Metric Query Controller
 * Provides API endpoint for querying host metrics with different dimensions and aggregation types
 * Also provides API for querying host devices with pagination
 */
@Slf4j
@RestController
@RequestMapping("/api/host")
public class HostController {

    @Resource
    private HostServiceI hostServiceI;

    /**
     * Query host metric time series data
     * <p>
     * Query metrics for a specific host with the specified aggregation type (avg, max, min).
     * The query supports different metric dimensions (e.g., cpu_usage, memory_usage).
     * </p>
     *
     * @param metricCategory  Virtual table name (e.g., 'zerotrace_agent_host_cpu')
     * @param host            Host identifier (e.g., 'auto-vm-202.112.237.33-W16')
     * @param metricName      Metric name to query (e.g., 'cpu_usage')
     * @param aggregationType Aggregation type: avg, max, min
     * @param startTime       Time range start (seconds timestamp)
     * @param endTime         Time range end (seconds timestamp)
     * @param teamId          Team ID for multi-tenant filtering
     * @return MultiResponse containing list of HostMetricTimeSeriesDTO with time series data
     */
    @GetMapping("/metric/time_series")
    public MultiResponse<HostMetricTimeSeriesDTO> queryHostMetricTimeSeries(
            @RequestParam(value = "metricCategory", required = true) String metricCategory,
            @RequestParam(value = "host", required = true) String host,
            @RequestParam(value = "metricName", required = true) String metricName,
            @RequestParam(value = "aggregationType", required = true) String aggregationType,
            @RequestParam(value = "startTime", required = false) Long startTime,
            @RequestParam(value = "endTime", required = false) Long endTime,
            @RequestParam(value = "teamId", required = false) Long teamId) {

        log.info("Received host metric time series query request: metricCategory={}, host={}, metricName={}, aggregationType={}, startTime={}, endTime={}, teamId={}",
                 metricCategory, host, metricName, aggregationType, startTime, endTime, teamId);

        HostMetricQueryParam queryParam = new HostMetricQueryParam();
        queryParam.setHost(host);
        queryParam.setMetricCategory(metricCategory);
        queryParam.setMetricName(metricName);
        queryParam.setAggregationType(aggregationType);
        queryParam.setStartTime(startTime);
        queryParam.setEndTime(endTime);
        queryParam.setTeamId(teamId);

        List<HostMetricTimeSeriesDTO> res = hostServiceI.queryHostMetricTimeSeries(queryParam);
        return MultiResponse.of(res);
    }

    /**
     * Query host devices with pagination and filtering
     * <p>
     * Supports filtering by user_id, name, alias, ip with fuzzy matching for string fields.
     * Returns paginated results with total count.
     * </p>
     *
     * @param pageIndex  Page number (0-based, default 0)
     * @param pageSize Page size (default 10)
     * @param userId   Filter by user ID (optional)
     * @param name     Filter by host name - fuzzy match (optional)
     * @param alias    Filter by alias - fuzzy match (optional)
     * @param ip       Filter by IP - fuzzy match (optional)
     * @return PageResponse containing list of HostDeviceDTO with pagination info
     */
    @GetMapping("/device/list")
    public PageResponse<HostDeviceDTO> queryHostDevicePage(
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "alias", required = false) String alias,
            @RequestParam(value = "ip", required = false) String ip) {

        log.info("Received host device page query request: pageIndex={}, pageSize={}, userId={}, name={}, alias={}, ip={}",
                pageIndex, pageSize, userId, name, alias, ip);

        HostDevicePageQuery query = new HostDevicePageQuery();
        query.setPageIndex(pageIndex);
        query.setPageSize(pageSize);
        query.setUserId(userId);
        query.setName(name);
        query.setAlias(alias);
        query.setIp(ip);

        return hostServiceI.queryHostDevicePage(query);
    }
}