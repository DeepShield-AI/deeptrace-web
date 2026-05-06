package cn.edu.qcl.trace.strategy;

import cn.edu.qcl.dto.data.FieldOptionsDTO;
import cn.edu.qcl.dto.param.FieldOptionQueryParam;
import cn.edu.qcl.external.TraceByAPIQueryGateway;
import cn.edu.qcl.trace.gateway.FieldIdNameMappingGateway;
import cn.edu.qcl.utils.MapToEntity;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Query Method Strategy
 * Handles queries using TraceByAPIQueryGateway (method="query")
 * 
 * This strategy queries via HTTP API and builds SQL with GROUP BY
 */
@Slf4j
@Component
public class QueryMethodStrategy implements FieldOptionsQueryStrategy {

    private static final String STRATEGY_NAME = "query";

    @Resource
    private TraceByAPIQueryGateway traceByAPIQueryGateway;

    @Resource
    private FieldIdNameMappingGateway idNameMappingGateway;

    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }

    @Override
    public FieldOptionsDTO query(FieldOptionQueryParam queryParam) {
        log.info("Executing query strategy for field: {}", queryParam.getField());

        /*
         * 构建SQL查询语句，格式为：
         * SELECT ${field} FROM `${database}`.`${tableName}` WHERE ${filter} GROUP BY ${field}
         */
        String sql = buildFieldOptionsQuerySql(queryParam);
        log.debug("Built SQL: {}", sql);

        // 执行ClickHouse查询
        List<Map<String, Object>> queryData = traceByAPIQueryGateway.executeQuery(queryParam.getDatabase(), sql);
        log.info("Query returned {} records", queryData.size());

        // 从查询结果中提取分组后的字段值列表
        List<Object> fieldValues = extractGroupByFieldValues(queryData);

        /*
         * 获取字段的枚举映射关系并转换为FieldMapping对象列表
         * 映射关系用于将ID转换为可读的名称，提升前端展示效果
         */
        List<Map<String, Object>>  idNameMappings = idNameMappingGateway.getEnumMapping(queryParam.getField());
        List<FieldOptionsDTO.FieldMapping> fieldMappings = MapToEntity.mapListToEntityList(idNameMappings, FieldOptionsDTO.FieldMapping.class);


        return FieldOptionsDTO.builder()
                .data(fieldValues)
                .enumMappings(fieldMappings)
                .build();
    }

    /**
     * 从Group by查询结果中提取分组后的字段值列表
     * @param queryData ClickHouse查询返回的结果集，每个元素为一行记录的Map表示，
     *                  key为字段名，value为字段的所有值；如果为null或空集合则返回空列表
     * @return 包含第一条记录所有字段值的列表；如果输入为空则返回空列表
     */
    private List<Object> extractGroupByFieldValues(List<Map<String, Object>> queryData) {
        List<Object> fieldValues = new ArrayList<>();
        
        if (queryData == null || queryData.isEmpty()) {
            return fieldValues;
        }

        /*
         * 提取第一条记录的所有字段值（.values()返回Collection<V>）
         * 注意：这里只处理第一条记录，将所有字段值作为一个元素添加到列表中
         */
        fieldValues.addAll(queryData.get(0).values());

        return fieldValues;
    }

    /**
     * Build SQL query from parameters
     * Format: SELECT ${field} FROM `${database}`.`${tableName}` WHERE ${filter} GROUP BY ${field}
     *
     */
    private String buildFieldOptionsQuerySql(FieldOptionQueryParam queryParam) {
        StringBuilder sql = new StringBuilder();

        String field = idNameMappingGateway.parseResourceName(queryParam.getField());
        // SELECT clause
        sql.append("SELECT ").append(field);
        
        // FROM clause
        sql.append(" FROM ").append("`").append(queryParam.getTableName()).append("`");
        
        // WHERE clause
        if (StringUtils.hasText(queryParam.getFilter())) {
            sql.append(" WHERE ").append(queryParam.getFilter());
        }
        
        // GROUP BY clause
        sql.append(" GROUP BY ").append(field);
        
        return sql.toString();
    }

}