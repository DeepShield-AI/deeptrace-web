/*
package cn.edu.qcl.web;

import cn.edu.qcl.clickhouse.FlowLogService;
import cn.edu.qcl.dto.data.SpanDTO;
import cn.edu.qcl.dto.param.SpanQueryParam;
import cn.edu.qcl.utils.UserSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

*/
/**
 * Flow Log 查询控制器
 * 使用 ClickHouse Java Client 查询数据，通过 HTTP 协议 连接 ClickHouse。
 *//*

@RestController
@RequestMapping("/api/flowlog")
public class FlowLogController {

    @Autowired
    private FlowLogService flowLogService;

    */
/**
     * 查询 L4 Flow Log 数据
     * @param limit 返回条数限制，默认 5
     * @return 查询结果
     *//*

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

    */
/**
     * 查询 Span 数据
     * @param traceId TraceID（可选）
     * @param startDate 开始日期（可选，格式：yyyy-MM-dd）
     * @param endDate 结束日期（可选，格式：yyyy-MM-dd）
     * @param limit 返回条数限制，默认 100
     * @param orderBy 排序字段，默认 time
     * @param orderDirection 排序方向，默认 DESC
     * @return 查询结果
     *//*

    @GetMapping("/l7")
    public Map<String, Object> querySpan(
            @RequestParam(value = "traceId", required = false) String traceId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(value = "limit", defaultValue = "100") Integer limit,
            @RequestParam(value = "orderBy", defaultValue = "time") String orderBy,
            @RequestParam(value = "orderDirection", defaultValue = "DESC") String orderDirection) {

        Map<String, Object> result = new HashMap<>();
        try {
            // 构建查询参数
            SpanQueryParam queryParam = new SpanQueryParam();
            // 从当前登录用户获取租户ID
            queryParam.setUserId(UserSessionUtils.getCurrentUserId());
            queryParam.setTraceId(traceId);
            queryParam.setStartDate(startDate);
            queryParam.setEndDate(endDate);
            queryParam.setLimit(limit);
            queryParam.setOrderBy(orderBy);
            queryParam.setOrderDirection(orderDirection);

            List<SpanDTO> data = flowLogService.querySpan(queryParam);
            result.put("success", true);
            result.put("data", data);
            result.put("count", data.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}*/
