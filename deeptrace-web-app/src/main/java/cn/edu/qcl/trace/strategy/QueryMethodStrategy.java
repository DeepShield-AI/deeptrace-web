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

        // Build SQL query
        String sql = buildFieldOptionsQuerySql(queryParam);
        log.debug("Built SQL: {}", sql);

        // Execute query via TraceByAPIQueryGateway
        List<Map<String, Object>> queryData = traceByAPIQueryGateway.executeQuery(queryParam.getDatabase(), sql);
        log.info("Query returned {} records", queryData.size());

        // Extract field values from query results and convert to List<String>
        List<Object> fieldValues = extractFieldValues(queryData);

        // Extract enum mappings
        List<Map<String, Object>>  idNameMappings = idNameMappingGateway.getEnumMapping(queryParam.getField());
        List<FieldOptionsDTO.FieldMapping> fieldMappings = MapToEntity.mapListToEntityList(idNameMappings, FieldOptionsDTO.FieldMapping.class);


        return FieldOptionsDTO.builder()
                .data(fieldValues)
                .enumMappings(fieldMappings)
                .build();
    }

    /**
     * Extract field values from query results and convert to List<String>
     * 
     * @param queryData the query results from ClickHouse
     * @return List of field values as strings
     */
    private List<Object> extractFieldValues(List<Map<String, Object>> queryData) {
        List<Object> fieldValues = new ArrayList<>();
        
        if (queryData == null || queryData.isEmpty()) {
            return fieldValues;
        }

        fieldValues.add(queryData.get(0).values());

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