package cn.edu.qcl.clickhouse;

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
}