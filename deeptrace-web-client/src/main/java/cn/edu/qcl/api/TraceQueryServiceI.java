package cn.edu.qcl.api;

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
import com.alibaba.cola.dto.PageResponse;

import java.util.List;

/**
 * Metric Query Service Interface
 * Provides metric query functionality with enum value mapping
 */
public interface TraceQueryServiceI {

    /**
     * Query metrics from ClickHouse with enum value mapping
     *
     * @param queryParam the query parameters including database, table, field, and filter
     * @return MetricQueryResult containing query data and enum mappings
     */
    FieldOptionsDTO queryFieldOptions(FieldOptionQueryParam queryParam);

    /**
     * Get filter fields configuration for a specific table
     * Returns fixed configuration based on database and table name
     *
     * @param database Database name
     * @param tableName Table name
     * @return TableFilterFieldsDTO containing filter fields configuration
     */
    FilterFieldsDTO getTableFilterFields(String database, String tableName);

    /**
     * Query graph node metrics from ClickHouse
     * Returns metrics for each node in the graph
     *
     * @param queryParam the query parameters including database, table, filter, and teamId
     * @return GraphNodeMetricsDTO containing node metrics
     */
    List<GraphNodeMetricsDTO> queryGraphNodeMetrics(TraceQueryParam queryParam);

    /**
     * 查询服务节点列表及其指标
     * <p>
     * 根据查询参数查询服务节点的详细信息，包括各资源名称、IP地址、端口及请求指标。
     * </p>
     *
     * @param queryParam 查询参数对象，包含database、tableName、filter、teamId
     * @return 服务节点指标列表
     */
    List<TableNodeMetricsDTO> queryTableNodeMetrics(TraceQueryParam queryParam);

    /**
     * 查询拓扑图边及其指标
     * <p>
     * 根据查询参数查询拓扑图中每条边的指标数据，包括请求数、错误数、错误率、平均时延等。
     * </p>
     *
     * @param queryParam 查询参数对象，包含database、tableName、filter、teamId
     * @return 拓扑图边指标列表
     */
    List<GraphEdgeMetricsDTO> queryGraphEdgeMetrics(TraceQueryParam queryParam);

    /**
     * 查询单个节点的时间序列请求数量
     * <p>
     * 根据查询参数查询单个节点的时间序列请求数量，以分钟为单位聚合统计。
     * </p>
     *
     * @param queryParam 查询参数对象，包含database、tableName、filter、teamId
     * @return 时间序列请求数量列表
     */
    List<TimeSeriesDTO> queryNodeCountTimeSeries(TraceQueryParam queryParam);

    /**
     * 查询单个节点的时间序列请求错误数
     * <p>
     * 根据查询参数查询单个节点的时间序列请求错误数，以分钟为单位聚合统计。
     * </p>
     *
     * @param queryParam 查询参数对象，包含database、tableName、filter、teamId
     * @return 时间序列请求错误数列表
     */
    List<TimeSeriesDTO> queryNodeErrorTimeSeries(TraceQueryParam queryParam);

    /**
     * 查询单个节点的时间序列响应时延
     * <p>
     * 根据查询参数查询单个节点的时间序列响应时延，以分钟为单位聚合统计。
     * </p>
     *
     * @param queryParam 查询参数对象，包含database、tableName、filter、teamId
     * @return 时间序列响应时延列表
     */
    List<TimeSeriesDTO> queryNodeLatencyTimeSeries(TraceQueryParam queryParam);

    /**
     * 查询Trace时间序列响应时延
     * <p>
     * 根据查询参数查询Trace维度列表的时间序列响应时延，以分钟为单位聚合统计。
     * </p>
     *
     * @param queryParam 查询参数对象，包含database、tableName、filter、teamId
     * @return Trace时间序列响应时延列表
     */
    List<TimeSeriesDTO> queryTraceLatencyTimeSeries(TraceQueryParam queryParam);

    /**
     * 查询Trace时间序列请求错误数
     * <p>
     * 根据查询参数查询Trace维度列表的时间序列请求错误数，以分钟为单位聚合统计。
     * </p>
     *
     * @param queryParam 查询参数对象，包含database、tableName、filter、teamId
     * @return Trace时间序列请求错误数列表
     */
    List<TimeSeriesDTO> queryTraceErrorTimeSeries(TraceQueryParam queryParam);

    /**
     * 查询Trace时间序列请求数量
     * <p>
     * 根据查询参数查询Trace维度列表的时间序列请求数量，以分钟为单位聚合统计。
     * </p>
     *
     * @param queryParam 查询参数对象，包含database、tableName、filter、teamId
     * @return Trace时间序列请求数量列表
     */
    List<TimeSeriesDTO> queryTraceCountTimeSeries(TraceQueryParam queryParam);

    /**
     * 查询单个节点的span明细（分页）
     * <p>
     * 根据查询参数查询指定节点的span详细信息。
     * </p>
     *
     * @param queryParam 查询参数对象，包含database、tableName、filter、teamId及分页信息
     * @return span明细分页结果
     */
    PageResponse<SpanDTO> querySpanDetails(TracePageQueryParam queryParam);

    /**
     * 查询Trace维度列表（分页）
     * <p>
     * 根据查询参数查询Trace维度列表信息。
     * </p>
     *
     * @param queryParam 查询参数对象，包含filter、teamId及分页信息
     * @return Trace维度列表分页结果
     */
    PageResponse<TraceInfoDTO> queryTraceList(TracePageQueryParam queryParam);
}