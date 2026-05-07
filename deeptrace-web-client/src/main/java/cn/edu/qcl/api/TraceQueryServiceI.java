package cn.edu.qcl.api;

import cn.edu.qcl.dto.data.FieldOptionsDTO;
import cn.edu.qcl.dto.data.FilterFieldsDTO;
import cn.edu.qcl.dto.data.GraphNodeMetricsDTO;
import cn.edu.qcl.dto.data.TableNodeMetricsDTO;
import cn.edu.qcl.dto.param.FieldOptionQueryParam;
import cn.edu.qcl.dto.param.GraphMetricsQueryParam;

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
    List<GraphNodeMetricsDTO> queryGraphNodeMetrics(GraphMetricsQueryParam queryParam);

    /**
     * 查询服务节点列表及其指标
     * <p>
     * 根据查询参数查询服务节点的详细信息，包括各资源名称、IP地址、端口及请求指标。
     * </p>
     *
     * @param queryParam 查询参数对象，包含database、tableName、filter、teamId
     * @return 服务节点指标列表
     */
    List<TableNodeMetricsDTO> queryTableNodeMetrics(GraphMetricsQueryParam queryParam);
}