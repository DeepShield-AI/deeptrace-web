package cn.edu.qcl.web;

import cn.edu.qcl.api.TraceQueryServiceI;
import cn.edu.qcl.dto.data.FieldOptionsDTO;
import cn.edu.qcl.dto.data.FilterFieldsDTO;
import cn.edu.qcl.dto.data.GraphEdgeMetricsDTO;
import cn.edu.qcl.dto.data.GraphNodeMetricsDTO;
import cn.edu.qcl.dto.data.TimeSeriesDTO;
import cn.edu.qcl.dto.data.SpanDTO;
import cn.edu.qcl.dto.data.TableNodeMetricsDTO;
import cn.edu.qcl.dto.data.TraceInfoDTO;
import cn.edu.qcl.dto.param.SingleTraceDetailQueryParam;
import cn.edu.qcl.dto.param.TracePageQueryParam;
import cn.edu.qcl.dto.param.FieldOptionQueryParam;
import cn.edu.qcl.dto.param.TraceQueryParam;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Metric Query Controller
 * Provides API endpoint for querying metrics with enum value mapping support
 */
@Slf4j
@RestController
@RequestMapping("/api/trace")
public class TraceQueryController {

    @Resource
    private TraceQueryServiceI traceQueryServiceI;

    /**
     * 查询指定字段的可选值（用于下拉框等UI组件）及 字段名称与字段ID的映射
     * 
     * @param database Database name (e.g., "flow_metrics")
     * @param tableName Table name (e.g., "application.1m")
     * @param method Query method (e.g., "query","sql")
     * @param field Field to query (e.g., "XXX")
     * @param filter Filter condition (e.g., "time >= toDateTime('2026-04-27 09:58:26', 'Asia/Shanghai') AND time < toDateTime('2026-04-27 09:59:26', 'Asia/Shanghai')")
     * @return MetricQueryResult containing query data and enum mappings
     */
    @GetMapping("/query/field/options")
    public FieldOptionsDTO queryFieldOptions(
            @RequestParam(value = "database", required = true) String database,
            @RequestParam(value = "tableName", required = true) String tableName,
            @RequestParam(value = "method", required = false) String method,
            @RequestParam(value = "field", required = true) String field,
            @RequestParam(value = "filter", required = false) String filter) {

        log.info("Received metric query request: database={}, tableName={}, method={}, field={}, filter={}",
                database, tableName, method, field, filter);

        FieldOptionQueryParam queryParam = new FieldOptionQueryParam();
        queryParam.setDatabase(database);
        queryParam.setTableName(tableName);
        queryParam.setMethod(method);
        queryParam.setField(field);
        queryParam.setFilter(filter);

        FieldOptionsDTO res =  traceQueryServiceI.queryFieldOptions(queryParam);
        return res;
    }

    /**
     * 数据表中支持过滤的字段及类型
     * Get filter fields configuration for a specific table
     * Returns fixed configuration based on database and table name
     * 
     * @param database Database name (e.g., "flow_log", "apm", "flow_metrics")
     * @param tableName Table name (e.g., "l7_flow_log", "traces_view", "application.1m")
     * @return TableFilterFieldsDTO containing filter fields configuration
     */
    @GetMapping("/query/filter/fields")
    public FilterFieldsDTO getTableFilterFields(
            @RequestParam(value = "database", required = true) String database,
            @RequestParam(value = "tableName", required = true) String tableName) {

        log.info("Received get table filter fields request: database={}, tableName={}", database, tableName);

        return traceQueryServiceI.getTableFilterFields(database, tableName);
    }

    /**
     * 查询图节点及其指标
     * <p>
     * 返回图中每个节点（服务）的指标数据，包括请求数、错误数、错误率、平均响应时间等。
     * </p>
     *
     * @param queryParam 查询参数对象，包含以下字段：
     *                   database - 数据库名称（如 "flow_metrics"）；
     *                   tableName - 表名称（如 "application.1m"）；
     *                   filter - 过滤条件SQL（如 "time >= toDateTime(?, 'Asia/Shanghai') and time < toDateTime(?, 'Asia/Shanghai') and app_service=redis and az_id in (11,22)"）；
     *                   teamId - 团队ID，用于多租户数据隔离。
     * @return 图节点指标数据传输对象，包含节点列表及各节点的详细指标信息（应用服务名、服务ID、总请求数、总错误数、
     *         总响应数、错误率、平均RTT微秒值、平均RTT毫秒值）
     */
    @PostMapping("/query/graph/node/metrics")
    public MultiResponse<GraphNodeMetricsDTO> queryGraphNodeMetrics(@RequestBody TraceQueryParam queryParam) {
        log.info("Received graph node metrics query request: database={}, tableName={}, filter={}, teamId={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getFilter(), queryParam.getTeamId());

        List<GraphNodeMetricsDTO> res = traceQueryServiceI.queryGraphNodeMetrics(queryParam);
        return MultiResponse.of(res);
    }

    /**
     * 查询服务节点列表及其指标
     * <p>
     * 根据查询参数查询服务节点的详细信息，包括各资源名称、IP地址、端口及请求指标。
     * </p>
     *
     * @param queryParam 查询参数对象，包含database、tableName、filter、teamId
     * @return 服务节点指标列表
     */
    @PostMapping("/query/table/node/metrics")
    public MultiResponse<TableNodeMetricsDTO> queryTableNodeMetrics(@RequestBody TraceQueryParam queryParam) {
        log.info("Received service node metrics query request: database={}, tableName={}, filter={}, teamId={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getFilter(), queryParam.getTeamId());

        List<TableNodeMetricsDTO> res = traceQueryServiceI.queryTableNodeMetrics(queryParam);
        return MultiResponse.of(res);
    }

    /**
     * 查询拓扑图边及其指标
     * <p>
     * 根据查询参数查询拓扑图中每条边的指标数据，包括请求数、错误数、错误率、平均时延等。
     * </p>
     *
     * @param queryParam 查询参数对象，包含database、tableName、filter、teamId
     * @return 拓扑图边指标列表
     */
    @PostMapping("/query/graph/edge/metrics")
    public MultiResponse<GraphEdgeMetricsDTO> queryGraphEdgeMetrics(@RequestBody TraceQueryParam queryParam) {
        log.info("Received graph edge metrics query request: database={}, tableName={}, filter={}, teamId={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getFilter(), queryParam.getTeamId());

        List<GraphEdgeMetricsDTO> res = traceQueryServiceI.queryGraphEdgeMetrics(queryParam);
        return MultiResponse.of(res);
    }

    /**
     * 查询单个节点的span明细（分页）
     * <p>
     * 根据查询参数查询指定节点的span详细信息。
     * </p>
     *
     * @param queryParam 查询参数对象，包含database、tableName、filter、teamId及分页信息
     * @return span明细分页结果
     */
    @PostMapping("/query/span/details")
    public PageResponse<SpanDTO> querySpanDetailsByNode(@RequestBody TracePageQueryParam queryParam) {
        log.info("Received span details query request: database={}, tableName={}, filter={}, teamId={}, pageIndex={}, pageSize={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getFilter(), queryParam.getTeamId(),
                queryParam.getPageIndex(), queryParam.getPageSize());

        return traceQueryServiceI.querySpanDetailsByNode(queryParam);
    }


    /**
     * 查询单个节点的时间序列请求数量
     * <p>
     * 根据查询参数查询单个节点的时间序列请求数量，以分钟为单位聚合统计。
     * </p>
     *
     * @param queryParam 查询参数对象，包含database、tableName、filter、teamId
     * @return 时间序列请求数量列表
     */
    @PostMapping("/query/node/count_time_series")
    public MultiResponse<TimeSeriesDTO> queryNodeCountTimeSeries(@RequestBody TraceQueryParam queryParam) {
        log.info("Received node time series query request: database={}, tableName={}, filter={}, teamId={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getFilter(), queryParam.getTeamId());

        List<TimeSeriesDTO> res = traceQueryServiceI.queryNodeCountTimeSeries(queryParam);
        return MultiResponse.of(res);
    }

    /**
     * 查询单个节点的时间序列请求错误数
     * <p>
     * 根据查询参数查询单个节点的时间序列请求错误数，以分钟为单位聚合统计。
     * </p>
     *
     * @param queryParam 查询参数对象，包含database、tableName、filter、teamId
     * @return 时间序列请求错误数列表
     */
    @PostMapping("/query/node/error_time_series")
    public MultiResponse<TimeSeriesDTO> queryNodeErrorTimeSeries(@RequestBody TraceQueryParam queryParam) {
        log.info("Received node error time series query request: database={}, tableName={}, filter={}, teamId={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getFilter(), queryParam.getTeamId());

        List<TimeSeriesDTO> res = traceQueryServiceI.queryNodeErrorTimeSeries(queryParam);
        return MultiResponse.of(res);
    }

    /**
     * 查询单个节点的时间序列响应时延
     * <p>
     * 根据查询参数查询单个节点的时间序列响应时延，以分钟为单位聚合统计。
     * </p>
     *
     * @param queryParam 查询参数对象，包含database、tableName、filter、teamId
     * @return 时间序列响应时延列表
     */
    @PostMapping("/query/node/latency_time_series")
    public MultiResponse<TimeSeriesDTO> queryNodeLatencyTimeSeries(@RequestBody TraceQueryParam queryParam) {
        log.info("Received node latency time series query request: database={}, tableName={}, filter={}, teamId={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getFilter(), queryParam.getTeamId());

        List<TimeSeriesDTO> res = traceQueryServiceI.queryNodeLatencyTimeSeries(queryParam);
        return MultiResponse.of(res);
    }

    /**
     * 根据traceId查询span明细列表
     * <p>
     * 根据traceId和时间范围查询span明细列表。
     * </p>
     *
     * @param teamId 团队ID，用于多租户数据隔离
     * @param traceId Trace ID
     * @param startTime 开始时间（秒级时间戳）
     * @param endTime 结束时间（秒级时间戳）
     * @return span明细列表
     */
    @GetMapping("/query/span/details/by_trace")
    public MultiResponse<SpanDTO> querySpanDetailsByTrace(
            @RequestParam(value = "teamId", required = false) Long teamId,
            @RequestParam(value = "traceId", required = true) String traceId,
            @RequestParam(value = "startTime", required = false) Long startTime,
            @RequestParam(value = "endTime", required = false) Long endTime) {

        log.info("Received span details by trace request: traceId={}, startTime={}, endTime={}, teamId={}",
                traceId, startTime, endTime, teamId);

        SingleTraceDetailQueryParam queryParam = new SingleTraceDetailQueryParam();
        queryParam.setTeamId(teamId);
        queryParam.setTraceId(traceId);
        queryParam.setStartTime(startTime);
        queryParam.setEndTime(endTime);

        List<SpanDTO> res = traceQueryServiceI.querySpanDetailsByTrace(queryParam);
        return MultiResponse.of(res);
    }



    /**
     * 查询Trace维度列表（分页）
     * <p>
     * 根据查询参数查询Trace维度列表信息。
     * </p>
     *
     * @param queryParam 查询参数对象，包含filter、teamId及分页信息
     * @return Trace维度列表分页结果
     */
    @PostMapping("/query/trace/list")
    public PageResponse<TraceInfoDTO> queryTraceList(@RequestBody TracePageQueryParam queryParam) {
        log.info("Received trace list query request: filter={}, teamId={}, pageIndex={}, pageSize={}",
                queryParam.getFilter(), queryParam.getTeamId(),
                queryParam.getPageIndex(), queryParam.getPageSize());

        return traceQueryServiceI.queryTraceList(queryParam);
    }


    /**
     * 查询Trace时间序列响应时延
     * <p>
     * 根据查询参数查询Trace维度列表的时间序列响应时延，以分钟为单位聚合统计。
     * </p>
     *
     * @param queryParam 查询参数对象，包含database、tableName、filter、teamId
     * @return Trace时间序列响应时延列表
     */
    @PostMapping("/query/trace/latency_time_series")
    public MultiResponse<TimeSeriesDTO> queryTraceLatencyTimeSeries(@RequestBody TraceQueryParam queryParam) {
        log.info("Received trace latency time series query request: database={}, tableName={}, filter={}, teamId={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getFilter(), queryParam.getTeamId());

        List<TimeSeriesDTO> res = traceQueryServiceI.queryTraceLatencyTimeSeries(queryParam);
        return MultiResponse.of(res);
    }

    /**
     * 查询Trace时间序列请求错误数
     * <p>
     * 根据查询参数查询Trace维度列表的时间序列请求错误数，以分钟为单位聚合统计。
     * </p>
     *
     * @param queryParam 查询参数对象，包含database、tableName、filter、teamId
     * @return Trace时间序列请求错误数列表
     */
    @PostMapping("/query/trace/error_time_series")
    public MultiResponse<TimeSeriesDTO> queryTraceErrorTimeSeries(@RequestBody TraceQueryParam queryParam) {
        log.info("Received trace error time series query request: database={}, tableName={}, filter={}, teamId={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getFilter(), queryParam.getTeamId());

        List<TimeSeriesDTO> res = traceQueryServiceI.queryTraceErrorTimeSeries(queryParam);
        return MultiResponse.of(res);
    }

    /**
     * 查询Trace时间序列请求数量
     * <p>
     * 根据查询参数查询Trace维度列表的时间序列请求数量，以分钟为单位聚合统计。
     * </p>
     *
     * @param queryParam 查询参数对象，包含database、tableName、filter、teamId
     * @return Trace时间序列请求数量列表
     */
    @PostMapping("/query/trace/count_time_series")
    public MultiResponse<TimeSeriesDTO> queryTraceCountTimeSeries(@RequestBody TraceQueryParam queryParam) {
        log.info("Received trace time series query request: database={}, tableName={}, filter={}, teamId={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getFilter(), queryParam.getTeamId());

        List<TimeSeriesDTO> res = traceQueryServiceI.queryTraceCountTimeSeries(queryParam);
        return MultiResponse.of(res);
    }




    /**
     * 查询Span维度列表（分页）
     * <p>
     * 根据查询参数查询Span维度列表信息。
     * </p>
     *
     * @param queryParam 查询参数对象，包含filter、teamId及分页信息
     * @return Span维度列表分页结果
     */
    @PostMapping("/query/span/list")
    public PageResponse<SpanDTO> querySpanList(@RequestBody TracePageQueryParam queryParam) {
        log.info("Received span list query request: filter={}, teamId={}, pageIndex={}, pageSize={}",
                queryParam.getFilter(), queryParam.getTeamId(),
                queryParam.getPageIndex(), queryParam.getPageSize());

        return traceQueryServiceI.querySpanList(queryParam);
    }


}