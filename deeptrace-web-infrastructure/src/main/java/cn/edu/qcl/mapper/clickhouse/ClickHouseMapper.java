package cn.edu.qcl.mapper.clickhouse;

import cn.edu.qcl.dto.data.GraphEdgeMetricsDTO;
import cn.edu.qcl.dto.data.GraphNodeMetricsDTO;
import cn.edu.qcl.dto.data.NodeTimeSeriesDTO;
import cn.edu.qcl.dto.data.SpanDTO;
import cn.edu.qcl.dto.data.SpanDetailsPageQuery;
import cn.edu.qcl.dto.data.TableNodeMetricsDTO;
import cn.edu.qcl.dto.param.GraphMetricsQueryParam;
import cn.edu.qcl.usermap.UserMapEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * ClickHouse Mapper 接口
 * 用于查询 ClickHouse 数据
 */
@Mapper
public interface ClickHouseMapper {


    /**
     * 执行自定义查询
     * @param sql 查询SQL
     * @return 查询结果
     */
    @Select("${sql}")
    List<Map<String, Object>> executeQuery(@Param("sql") String sql);

    // ==================== user_map 表相关查询 ====================

    /**
     * 查询所有用户映射
     * @return 用户映射列表
     */
//    @Select("SELECT `_id` as id, eth_type as name FROM flow_log.l4_flow_log limit 5")
    List<UserMapEntity> listAllUserMaps();

    // ==================== graph node metrics 查询 ====================

    /**
     * 查询图节点指标数据
     *
     * @param queryParam 查询参数对象
     * @return 图节点指标数据列表
     */
    List<GraphNodeMetricsDTO> queryGraphNodeMetrics(GraphMetricsQueryParam queryParam);

    /**
     * 查询服务节点列表及其指标
     *
     * @param queryParam 查询参数对象
     * @return 服务节点指标数据列表
     */
    List<TableNodeMetricsDTO> queryTableNodeMetrics(GraphMetricsQueryParam queryParam);

    /**
     * 查询拓扑图边及其指标
     *
     * @param queryParam 查询参数对象
     * @return 拓扑图边指标数据列表
     */
    List<GraphEdgeMetricsDTO> queryGraphEdgeMetrics(GraphMetricsQueryParam queryParam);

    /**
     * 查询单个节点的span明细
     *
     * @param queryParam 查询参数对象
     * @return span明细数据列表
     */
    List<SpanDTO> querySpanDetails(SpanDetailsPageQuery queryParam);

    /**
     * 统计单个节点的span明细总数（用于分页）
     *
     * @param queryParam 查询参数对象
     * @return span明细总数
     */
    int countSpanDetails(SpanDetailsPageQuery queryParam);

    /**
     * 查询单个节点的时间序列请求数量
     *
     * @param queryParam 查询参数对象
     * @return 时间序列请求数量列表
     */
    List<NodeTimeSeriesDTO> queryNodeTimeSeries(GraphMetricsQueryParam queryParam);

    /**
     * 查询单个节点的时间序列请求错误数
     *
     * @param queryParam 查询参数对象
     * @return 时间序列请求错误数列表
     */
    List<NodeTimeSeriesDTO> queryNodeErrorTimeSeries(GraphMetricsQueryParam queryParam);

    /**
     * 查询单个节点的时间序列响应时延
     *
     * @param queryParam 查询参数对象
     * @return 时间序列响应时延列表
     */
    List<NodeTimeSeriesDTO> queryNodeLatencyTimeSeries(GraphMetricsQueryParam queryParam);

}