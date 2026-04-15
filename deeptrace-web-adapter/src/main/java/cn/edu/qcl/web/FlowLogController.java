package cn.edu.qcl.web;

import cn.edu.qcl.clickhouse.FlowLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Flow Log 查询控制器
 * 使用 ClickHouse Java Client 查询数据
 */
@RestController
@RequestMapping("/api/flowlog")
public class FlowLogController {

    @Autowired
    private FlowLogService flowLogService;

    /**
     * 查询 L4 Flow Log 数据
     * @param limit 返回条数限制，默认 5
     * @return 查询结果
     */
    @GetMapping("/l4")
    public Map<String, Object> queryL4FlowLog(
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> data = flowLogService.queryL4FlowLog(limit);
            result.put("success", true);
            result.put("data", data);
            result.put("count", data.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}