package cn.edu.qcl.web;

import cn.edu.qcl.api.TraceQueryServiceI;
import cn.edu.qcl.dto.data.FieldOptionsDTO;
import cn.edu.qcl.dto.param.FieldOptionQueryParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * 查询指定字段的可选值（用于下拉框等UI组件）
     * 
     * @param database Database name (e.g., "flow_metrics")
     * @param tableName Table name (e.g., "application.1m")
     * @param method Query method (e.g., "query")
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
}