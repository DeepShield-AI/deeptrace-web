package cn.edu.qcl.trace;

import cn.edu.qcl.api.TraceQueryServiceI;
import cn.edu.qcl.dto.data.FieldOptionsDTO;
import cn.edu.qcl.dto.data.FilterFieldsDTO;
import cn.edu.qcl.dto.data.GraphEdgeMetricsDTO;
import cn.edu.qcl.dto.data.GraphNodeMetricsDTO;
import cn.edu.qcl.dto.data.TimeSeriesDTO;
import cn.edu.qcl.dto.data.SpanDTO;
import cn.edu.qcl.dto.data.TableNodeMetricsDTO;
import cn.edu.qcl.dto.data.TraceInfoDTO;
import cn.edu.qcl.dto.param.TracePageQueryParam;
import cn.edu.qcl.dto.param.FieldOptionQueryParam;
import cn.edu.qcl.dto.param.TraceQueryParam;
import cn.edu.qcl.mapper.clickhouse.ClickHouseMapper;
import cn.edu.qcl.trace.strategy.FieldOptionsQueryStrategy;
import cn.edu.qcl.trace.strategy.FieldOptionsQueryStrategyFactory;
import com.alibaba.cola.dto.PageResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Metric Query Service Implementation
 * Handles metric queries with enum value mapping support
 * 
 * Uses Strategy Pattern to dispatch queries based on method parameter
 * Avoids if-else logic for better extensibility
 */
@Slf4j
@Service
public class TraceQueryServiceImpl implements TraceQueryServiceI {

    @Resource
    private FieldOptionsQueryStrategyFactory strategyFactory;

    @Resource
    private ClickHouseMapper clickHouseMapper;

    /**
     * Fixed filter fields configuration for different tables
     * Key: "database.tableName"
     * Value: List of filter field configurations
     */
    private static final Map<String, List<FilterFieldsDTO.FilterFieldConfig>> TABLE_FILTER_FIELDS_MAP = new HashMap<>();

    static {
        // flow_log.l7_flow_log
        List<FilterFieldsDTO.FilterFieldConfig> l7FlowLogFields = new ArrayList<>();
        l7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("app_service", "String", Arrays.asList("=", "IN")));
        l7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("endpoint", "String", Arrays.asList("=", "IN")));
        l7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("response_status", "Int", Arrays.asList("=", "IN")));
        l7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("l7_protocol", "Int", Arrays.asList("=", "IN")));
//        l7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("trace_id", "String", Arrays.asList("=", "IN")));
        TABLE_FILTER_FIELDS_MAP.put("flow_log.l7_flow_log", l7FlowLogFields);

        // apm.traces_view
        List<FilterFieldsDTO.FilterFieldConfig> tracesViewFields = new ArrayList<>();
        tracesViewFields.add(new FilterFieldsDTO.FilterFieldConfig("app_service", "String", Arrays.asList("=", "IN")));
        tracesViewFields.add(new FilterFieldsDTO.FilterFieldConfig("root_endpoint", "String", Arrays.asList("=", "IN")));
        tracesViewFields.add(new FilterFieldsDTO.FilterFieldConfig("root_response_status", "Int", Arrays.asList("=", "IN")));
        tracesViewFields.add(new FilterFieldsDTO.FilterFieldConfig("root_l7_protocol", "Int", Arrays.asList("=", "IN")));
        tracesViewFields.add(new FilterFieldsDTO.FilterFieldConfig("has_error", "Int", Arrays.asList("=", "IN")));
        TABLE_FILTER_FIELDS_MAP.put("apm.traces_view", tracesViewFields);

        // flow_metrics.application.1m (also applies to application.1s and application.1h)
        List<FilterFieldsDTO.FilterFieldConfig> applicationFields = new ArrayList<>();
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("app_service", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("app_instance", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("region_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("az_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("host_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_cluster_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_ns_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_group_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_node_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("service_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("l3_epc_id", "String", Arrays.asList("=", "IN")));
//        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("epc_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("subnet_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("ip4", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("ip6", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("server_port", "String", Arrays.asList("=", "IN")));
//        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("l3_device_id", "String", Arrays.asList("=", "IN")));
//        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("l3_device_type", "String", Arrays.asList("=", "IN")));
        TABLE_FILTER_FIELDS_MAP.put("flow_metrics.application.1m", applicationFields);
        TABLE_FILTER_FIELDS_MAP.put("flow_metrics.application.1s", applicationFields);
        TABLE_FILTER_FIELDS_MAP.put("flow_metrics.application.1h", applicationFields);

        // flow_metrics.application_map.1m (also applies to application_map.1s and application_map.1h)
        List<FilterFieldsDTO.FilterFieldConfig> applicationMapFields = new ArrayList<>();
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("app_service", "app_service", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("app_instance", "app_instance", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("server_port", "server_port", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("region_id", "region_id_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("az_id", "az_id_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("host_id", "host_id_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_cluster_id", "pod_cluster_id_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_ns_id", "pod_ns_id_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_group_id", "pod_group_id_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_id", "pod_id_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_node_id", "pod_node_id_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("service_id", "service_id_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("l3_epc_id", "l3_epc_id_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("subnet_id", "subnet_id_1", "String", Arrays.asList("=", "IN")));
//        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("l3_device_id", "l3_device_id_1", "String", Arrays.asList("=", "IN")));
//        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("l3_device_type", "l3_device_type_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("ip4", "ip4_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("ip6", "ip6_1", "String", Arrays.asList("=", "IN")));
        TABLE_FILTER_FIELDS_MAP.put("flow_metrics.application_map.1m", applicationMapFields);
        TABLE_FILTER_FIELDS_MAP.put("flow_metrics.application_map.1s", applicationMapFields);
        TABLE_FILTER_FIELDS_MAP.put("flow_metrics.application_map.1h", applicationMapFields);


        // flow_log.graph_l7_flow_log
        List<FilterFieldsDTO.FilterFieldConfig> graphL7FlowLogFields = new ArrayList<>();
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("app_service", "app_service", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("app_instance", "app_instance", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("server_port", "server_port", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("region_id", "region_id_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("az_id", "az_id_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("host_id", "host_id_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_cluster_id", "pod_cluster_id_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_ns_id", "pod_ns_id_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_group_id", "pod_group_id_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_id", "pod_id_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_node_id", "pod_node_id_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("service_id", "service_id_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("l3_epc_id", "l3_epc_id_1", "String", Arrays.asList("=", "IN")));
//        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("epc_id", "epc_id_1","String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("subnet_id", "subnet_id_1", "String", Arrays.asList("=", "IN")));
//        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("l3_device_id", "l3_device_id_1", "String", Arrays.asList("=", "IN")));
//        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("l3_device_type", "l3_device_type_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("ip4", "ip4_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("ip6", "ip6_1", "String", Arrays.asList("=", "IN")));
        TABLE_FILTER_FIELDS_MAP.put("flow_log.graph_l7_flow_log", graphL7FlowLogFields);
    }

    @Override
    public FieldOptionsDTO queryFieldOptions(FieldOptionQueryParam queryParam) {
        // Validate input parameters
        validateFieldOptionQueryParam(queryParam);

        log.info("Querying metrics with params: database={}, tableName={}, method={}, field={}, filter={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getMethod(), 
                queryParam.getField(), queryParam.getFilter());

        // Get strategy based on method parameter (no if-else needed)
        FieldOptionsQueryStrategy strategy = getStrategy(queryParam.getMethod());

        // Execute query using the selected strategy
        return strategy.query(queryParam);
    }

    /**
     * Validate query parameters
     */
    private void validateFieldOptionQueryParam(FieldOptionQueryParam queryParam) {
        if (queryParam == null) {
            throw new IllegalArgumentException("Query parameters cannot be null");
        }
        if (!StringUtils.hasText(queryParam.getDatabase())) {
            throw new IllegalArgumentException("Database name is required");
        }
        if (!StringUtils.hasText(queryParam.getTableName())) {
            throw new IllegalArgumentException("Table name is required");
        }
        if (!StringUtils.hasText(queryParam.getField())) {
            throw new IllegalArgumentException("Field is required");
        }
    }

    /**
     * Get strategy based on method parameter
     * Returns default strategy if method is null or empty
     * 
     * No if-else needed - uses strategy factory for dispatch
     */
    private FieldOptionsQueryStrategy getStrategy(String method) {
        if (!StringUtils.hasText(method)) {
            return strategyFactory.getDefaultStrategy();
        }
        return strategyFactory.getStrategy(method);
    }

    @Override
    public FilterFieldsDTO getTableFilterFields(String database, String tableName) {
        log.info("Getting filter fields configuration for database={}, tableName={}", database, tableName);

        if (!StringUtils.hasText(database) || !StringUtils.hasText(tableName)) {
            throw new IllegalArgumentException("Database and table name are required");
        }

        String key = database + "." + tableName;
        List<FilterFieldsDTO.FilterFieldConfig> filterFields = TABLE_FILTER_FIELDS_MAP.get(key);

        if (filterFields == null) {
            log.warn("No filter fields configuration found for database={}, tableName={}", database, tableName);
            // Return empty configuration for unknown tables
            return FilterFieldsDTO.builder()
                    .database(database)
                    .tableName(tableName)
                    .filterFields(new ArrayList<>())
                    .build();
        }

        log.info("Found filter fields configuration for database={}, tableName={}, count={}", 
                database, tableName, filterFields.size());

        return FilterFieldsDTO.builder()
                .database(database)
                .tableName(tableName)
                .filterFields(filterFields)
                .build();
    }

    /**
     * 查询图节点指标数据
     * <p>
     * 根据查询参数从ClickHouse数据库中检索图节点的指标信息，包括请求数、错误数、错误率、
     * 平均响应时间等，并将结果转换为DTO对象返回。
     * </p>
     *
     * @param queryParam 查询参数对象，包含以下字段：
     *                   database - 数据库名称（如 "flow_metrics"）；
     *                   tableName - 表名称（如 "application.1m"）；
     *                   filter - 过滤条件SQL（如 "time >= toDateTime(?, 'Asia/Shanghai') and app_service=redis"）；
     *                   teamId - 团队ID，用于多租户数据隔离。
     * @return 图节点指标数据传输对象，包含节点列表及各节点的详细指标信息（应用服务名、服务ID、
     *         总请求数、总错误数、总响应数、错误率、平均RTT微秒值、平均RTT毫秒值）
     */
    @Override
    public List<GraphNodeMetricsDTO> queryGraphNodeMetrics(TraceQueryParam queryParam) {
        // 验证输入参数
        validateTraceQueryParam(queryParam);

        log.info("Querying graph node metrics with params: database={}, tableName={}, filter={}, teamId={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getFilter(), queryParam.getTeamId());

        // 执行SQL查询（SQL定义在ClickHouseMapper.xml中，直接返回NodeMetric对象列表）
        List<GraphNodeMetricsDTO> nodes = clickHouseMapper.queryGraphNodeMetrics(queryParam);
        log.info("Graph node metrics query returned {} records", nodes.size());

        return nodes;
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
    @Override
    public List<TableNodeMetricsDTO> queryTableNodeMetrics(TraceQueryParam queryParam) {
        // 验证输入参数
        validateTraceQueryParam(queryParam);

        log.info("Querying service node metrics with params: database={}, tableName={}, filter={}, teamId={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getFilter(), queryParam.getTeamId());

        // 执行SQL查询
        List<TableNodeMetricsDTO> nodes = clickHouseMapper.queryTableNodeMetrics(queryParam);
        log.info("Service node metrics query returned {} records", nodes.size());

        return nodes;
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
    @Override
    public List<GraphEdgeMetricsDTO> queryGraphEdgeMetrics(TraceQueryParam queryParam) {
        // 验证输入参数
        validateTraceQueryParam(queryParam);

        log.info("Querying graph edge metrics with params: database={}, tableName={}, filter={}, teamId={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getFilter(), queryParam.getTeamId());

        // 执行SQL查询
        List<GraphEdgeMetricsDTO> edges = clickHouseMapper.queryGraphEdgeMetrics(queryParam);
        log.info("Graph edge metrics query returned {} records", edges.size());

        return edges;
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
    @Override
    public List<TimeSeriesDTO> queryNodeCountTimeSeries(TraceQueryParam queryParam) {
        // 验证输入参数
        validateTraceQueryParam(queryParam);

        log.info("Querying node time series with params: database={}, tableName={}, filter={}, teamId={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getFilter(), queryParam.getTeamId());

        // 执行SQL查询
        List<TimeSeriesDTO> timeSeries = clickHouseMapper.queryNodeCountTimeSeries(queryParam);
        log.info("Node time series query returned {} records", timeSeries.size());

        return timeSeries;
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
    @Override
    public List<TimeSeriesDTO> queryNodeErrorTimeSeries(TraceQueryParam queryParam) {
        // 验证输入参数
        validateTraceQueryParam(queryParam);

        log.info("Querying node error time series with params: database={}, tableName={}, filter={}, teamId={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getFilter(), queryParam.getTeamId());

        // 执行SQL查询
        List<TimeSeriesDTO> timeSeries = clickHouseMapper.queryNodeErrorTimeSeries(queryParam);
        log.info("Node error time series query returned {} records", timeSeries.size());

        return timeSeries;
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
    @Override
    public List<TimeSeriesDTO> queryNodeLatencyTimeSeries(TraceQueryParam queryParam) {
        // 验证输入参数
        validateTraceQueryParam(queryParam);

        log.info("Querying node latency time series with params: database={}, tableName={}, filter={}, teamId={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getFilter(), queryParam.getTeamId());

        // 执行SQL查询
        List<TimeSeriesDTO> timeSeries = clickHouseMapper.queryNodeLatencyTimeSeries(queryParam);
        log.info("Node latency time series query returned {} records", timeSeries.size());

        return timeSeries;
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
    @Override
    public List<TimeSeriesDTO> queryTraceLatencyTimeSeries(TraceQueryParam queryParam) {
        log.info("Querying trace latency time series with params: database={}, tableName={}, filter={}, teamId={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getFilter(), queryParam.getTeamId());

        List<TimeSeriesDTO> timeSeries = clickHouseMapper.queryTraceLatencyTimeSeries(queryParam);
        log.info("Trace latency time series query returned {} records", timeSeries.size());

        return timeSeries;
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
    @Override
    public List<TimeSeriesDTO> queryTraceErrorTimeSeries(TraceQueryParam queryParam) {
        log.info("Querying trace error time series with params: database={}, tableName={}, filter={}, teamId={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getFilter(), queryParam.getTeamId());

        List<TimeSeriesDTO> timeSeries = clickHouseMapper.queryTraceErrorTimeSeries(queryParam);
        log.info("Trace error time series query returned {} records", timeSeries.size());

        return timeSeries;
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
    @Override
    public List<TimeSeriesDTO> queryTraceCountTimeSeries(TraceQueryParam queryParam) {
        log.info("Querying trace time series with params: database={}, tableName={}, filter={}, teamId={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getFilter(), queryParam.getTeamId());

        List<TimeSeriesDTO> timeSeries = clickHouseMapper.queryTraceCountTimeSeries(queryParam);
        log.info("Trace time series query returned {} records", timeSeries.size());

        return timeSeries;
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
    @Override
    public PageResponse<TraceInfoDTO> queryTraceList(TracePageQueryParam queryParam) {
        log.info("Querying trace list with params: filter={}, teamId={}, pageIndex={}, pageSize={}",
                queryParam.getFilter(), queryParam.getTeamId(),
                queryParam.getPageIndex(), queryParam.getPageSize());

        List<TraceInfoDTO> traces = clickHouseMapper.queryTraceList(queryParam);
        log.info("Trace list query returned {} records", traces.size());

        int totalCount = clickHouseMapper.countTraceList(queryParam);
        log.info("Trace list total count: {}", totalCount);

        return PageResponse.of(traces, totalCount, queryParam.getPageIndex(), queryParam.getPageSize());
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
    @Override
    public PageResponse<SpanDTO> querySpanDetails(TracePageQueryParam queryParam) {
        // 验证输入参数
        validateTracePageQueryParam(queryParam);

        log.info("Querying span details with params: database={}, tableName={}, filter={}, teamId={}, pageIndex={}, pageSize={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getFilter(), queryParam.getTeamId(),
                queryParam.getPageIndex(), queryParam.getPageSize());

        // 执行SQL查询
        List<SpanDTO> spans = clickHouseMapper.querySpanDetails(queryParam);
        log.info("Span details query returned {} records", spans.size());

        // 查询总数用于分页响应
        int totalCount = clickHouseMapper.countSpanDetails(queryParam);
        log.info("Span details total count: {}", totalCount);

        return PageResponse.of(spans, totalCount, queryParam.getPageIndex(), queryParam.getPageSize());
    }

    /**
     * Validate span details query parameters
     */
    private void validateTraceQueryParam(TraceQueryParam queryParam) {
        if (queryParam == null) {
            throw new IllegalArgumentException("Query parameters cannot be null");
        }
        if (!StringUtils.hasText(queryParam.getDatabase())) {
            throw new IllegalArgumentException("Database name is required");
        }
        if (!StringUtils.hasText(queryParam.getTableName())) {
            throw new IllegalArgumentException("Table name is required");
        }
        // Validate database and table name to prevent SQL injection
        validateIdentifier(queryParam.getDatabase(), "Database");
        validateIdentifier(queryParam.getTableName(), "Table");
    }
    /**
     * Validate span details query parameters
     */
    private void validateTracePageQueryParam(TracePageQueryParam queryParam) {
        if (queryParam == null) {
            throw new IllegalArgumentException("Query parameters cannot be null");
        }
        if (!StringUtils.hasText(queryParam.getDatabase())) {
            throw new IllegalArgumentException("Database name is required");
        }
        if (!StringUtils.hasText(queryParam.getTableName())) {
            throw new IllegalArgumentException("Table name is required");
        }
        // Validate database and table name to prevent SQL injection
        validateIdentifier(queryParam.getDatabase(), "Database");
        validateIdentifier(queryParam.getTableName(), "Table");
    }

    /**
     * Validate identifier (database name or table name) to prevent SQL injection.
     * Only allows alphanumeric characters, underscores, dots, and hyphens.
     *
     * @param identifier the identifier to validate
     * @param fieldName the field name for error message
     * @throws IllegalArgumentException if the identifier contains invalid characters
     */
    private void validateIdentifier(String identifier, String fieldName) {
        // Allow only alphanumeric characters, underscores, dots, and hyphens
        // Pattern: starts with letter or underscore, followed by allowed characters
        String validPattern = "^[a-zA-Z0-9_][a-zA-Z0-9_.-]*$";
        
        // For table names that may contain dots (e.g., "application.1m"), validate each part
        if (identifier.contains(".")) {
            String[] parts = identifier.split("\\.");
            for (String part : parts) {
                if (!part.matches(validPattern)) {
                    throw new IllegalArgumentException(
                            fieldName + " name contains invalid characters: " + identifier);
                }
            }
        } else {
            if (!identifier.matches(validPattern)) {
                throw new IllegalArgumentException(
                        fieldName + " name contains invalid characters: " + identifier);
            }
        }
        
        // Additional check for suspicious patterns
        String lowerIdentifier = identifier.toLowerCase();
        if (lowerIdentifier.contains("--") || lowerIdentifier.contains("/*")
                || lowerIdentifier.contains("*/") || lowerIdentifier.contains(";")
                || lowerIdentifier.contains("'") || lowerIdentifier.contains("\"")
                || lowerIdentifier.contains("=") || lowerIdentifier.contains(" ")
                || lowerIdentifier.contains("\t") || lowerIdentifier.contains("\n")) {
            throw new IllegalArgumentException(
                    fieldName + " name contains forbidden characters: " + identifier);
        }
    }

}