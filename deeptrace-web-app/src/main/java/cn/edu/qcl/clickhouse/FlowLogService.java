package cn.edu.qcl.clickhouse;

import cn.edu.qcl.dto.data.SpanDTO;
import cn.edu.qcl.dto.param.SpanQueryParam;

import java.util.List;
import java.util.Map;

/**
 * Flow Log 查询服务接口
 */
public interface FlowLogService {
    
    /**
     * 查询 L4 Flow Log 数据
     * @param limit 返回条数限制
     * @return 查询结果列表
     */
    List<Map<String, Object>> queryL4FlowLog(int limit);

    /**
     * 查询 Span 数据
     * @param queryParam 查询参数
     * @return Span 数据列表
     */
    List<SpanDTO> querySpan(SpanQueryParam queryParam);
}