package cn.edu.qcl.mapper.clickhouse;

import cn.edu.qcl.dto.data.GraphEdgeMetricsDTO;
import cn.edu.qcl.dto.data.GraphNodeMetricsDTO;
import cn.edu.qcl.dto.data.TimeSeriesDTO;
import cn.edu.qcl.dto.data.SpanDTO;
import cn.edu.qcl.dto.data.TableNodeMetricsDTO;
import cn.edu.qcl.dto.data.TraceInfoDTO;
import cn.edu.qcl.dto.param.TracePageQueryParam;
import cn.edu.qcl.dto.param.TraceQueryParam;
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
    List<GraphNodeMetricsDTO> queryGraphNodeMetrics(TraceQueryParam queryParam);

    /**
     * 查询服务节点列表及其指标
     *
     * @param queryParam 查询参数对象
     * @return 服务节点指标数据列表
     */
    List<TableNodeMetricsDTO> queryTableNodeMetrics(TraceQueryParam queryParam);

    /**
     * 查询拓扑图边及其指标
     *
     * @param queryParam 查询参数对象
     * @return 拓扑图边指标数据列表
     */
    List<GraphEdgeMetricsDTO> queryGraphEdgeMetrics(TraceQueryParam queryParam);

    /**
     * 查询单个节点的span明细
     *
     * @param queryParam 查询参数对象
     * @return span明细数据列表
     */
    List<SpanDTO> querySpanDetailsByNode(TracePageQueryParam queryParam);

    /**
     * 统计单个节点的span明细总数（用于分页）
     *
     * @param queryParam 查询参数对象
     * @return span明细总数
     */
    int countSpanDetailsByNode(TracePageQueryParam queryParam);

    /**
     * 查询单个节点的时间序列请求数量
     *
     * @param queryParam 查询参数对象
     * @return 时间序列请求数量列表
     */
    List<TimeSeriesDTO> queryNodeCountTimeSeries(TraceQueryParam queryParam);

    /**
     * 查询单个节点的时间序列请求错误数
     *
     * @param queryParam 查询参数对象
     * @return 时间序列请求错误数列表
     */
    List<TimeSeriesDTO> queryNodeErrorTimeSeries(TraceQueryParam queryParam);

    /**
     * 查询单个节点的时间序列响应时延
     *
     * @param queryParam 查询参数对象
     * @return 时间序列响应时延列表
     */
    List<TimeSeriesDTO> queryNodeLatencyTimeSeries(TraceQueryParam queryParam);

    /**
     * 查询Trace时间序列响应时延
     *
     * @param queryParam 查询参数对象
     * @return Trace时间序列响应时延列表
     */
    List<TimeSeriesDTO> queryTraceLatencyTimeSeries(TraceQueryParam queryParam);

    /**
     * 查询Trace维度列表（分页）
     *
     * @param queryParam 查询参数对象
     * @return Trace维度信息列表
     */
    List<TraceInfoDTO> queryTraceList(TracePageQueryParam queryParam);

    /**
     * 查询Span维度列表（分页）
     *
     * @param queryParam 查询参数对象
     * @return Span维度信息列表
     */
    List<SpanDTO> querySpanList(TracePageQueryParam queryParam);

    /**
     * 统计Span维度列表总数（用于分页）
     *
     * @param queryParam 查询参数对象
     * @return Span维度信息列表总数
     */
    int countSpanList(TracePageQueryParam queryParam);

    /**
     * 查询Trace时间序列请求错误数
     *
     * @param queryParam 查询参数对象
     * @return Trace时间序列请求错误数列表
     */
    List<TimeSeriesDTO> queryTraceErrorTimeSeries(TraceQueryParam queryParam);

    /**
     * 查询Trace时间序列请求数量
     *
     * @param queryParam 查询参数对象
     * @return Trace时间序列请求数量列表
     */
    List<TimeSeriesDTO> queryTraceCountTimeSeries(TraceQueryParam queryParam);

    /**
     * 统计Trace维度列表总数（用于分页）
     *
     * @param queryParam 查询参数对象
     * @return Trace维度列表总数
     */
    int countTraceList(TracePageQueryParam queryParam);

}