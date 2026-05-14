package cn.edu.qcl.web;

import cn.edu.qcl.api.HostServiceI;
import cn.edu.qcl.dto.data.HostMetricTimeSeriesDTO;
import cn.edu.qcl.dto.param.HostMetricQueryParam;
import com.alibaba.cola.dto.MultiResponse;
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
 */
@Slf4j
@RestController
@RequestMapping("/api/host/metric")
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
    @GetMapping("/time_series")
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
}