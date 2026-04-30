package cn.edu.qcl.trace.strategy;

import cn.edu.qcl.dto.data.FieldOptionsDTO;
import cn.edu.qcl.dto.param.FieldOptionQueryParam;
import cn.edu.qcl.enums.L7ProtocolEnum;
import cn.edu.qcl.enums.ResponseStatusEnum;
import cn.edu.qcl.mapper.clickhouse.ClickHouseMapper;
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
 * SQL Method Strategy
 * Handles queries using direct ClickHouse mapper (method="sql")
 * 
 * This strategy queries via MyBatis ClickHouseMapper with groupUniqArray
 * SQL: SELECT groupUniqArray(${field}) FROM `${database}`.`${tableName}` WHERE ${filter}
 */
@Slf4j
@Component
public class SqlMethodStrategy implements FieldOptionsQueryStrategy {

    private static final String STRATEGY_NAME = "sql";

    @Resource
    private ClickHouseMapper clickHouseMapper;

    @Resource
    private FieldIdNameMappingGateway idNameMappingGateway;

    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }

    @Override
    public FieldOptionsDTO query(FieldOptionQueryParam queryParam) {
        log.info("Executing sql strategy for field: {}", queryParam.getField());

        String sql = buildFieldOptionsQuerySql(queryParam);
        log.info("Executing ClickHouse direct query: {}", sql);
        List<Map<String, Object>> queryData = clickHouseMapper.executeQuery(sql);
        log.info("SQL query returned {} records", queryData.size());

        // Extract field values from query results (groupUniqArray returns array in first row)
        List<Object> fieldValues = extractFieldValues(queryData);

        // Extract enum mappings based on field name
        List<FieldOptionsDTO.FieldMapping> fieldMappings = getEnumMappingsFromField(queryParam.getField());


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

        // Flatten values from Map<String, Object> into List<Object>
        // groupUniqArray returns an array in the first column
        Map<String, Object> firstRow = queryData.get(0);
        if (firstRow != null && !firstRow.isEmpty()) {
            // Get the first value (the array from groupUniqArray)
            Object value = firstRow.values().iterator().next();
            if (value instanceof List<?>) {
                // Flatten the list elements into fieldValues
                fieldValues.addAll((List<?>) value);
            } else if (value instanceof Object[]) {
                // Handle array case
                for (Object item : (Object[]) value) {
                    fieldValues.add(item);
                }
            } else {
                // Single value case
                fieldValues.add(value);
            }
        }

        return fieldValues;
    }

    /**
     * Get enum mappings based on field name
     * Supports L7_Protocol and Response_Status fields
     *
     * @param fieldName the field name to check
     * @return List of field mappings from enum values
     */
    private List<FieldOptionsDTO.FieldMapping> getEnumMappingsFromField(String fieldName) {
        List<FieldOptionsDTO.FieldMapping> mappings = new ArrayList<>();
        
        if (fieldName == null || fieldName.isEmpty()) {
            return mappings;
        }
        
        // Check if field contains L7_Protocol
        if (fieldName.toLowerCase().contains("l7_protocol")) {
            for (L7ProtocolEnum protocol : L7ProtocolEnum.values()) {
                FieldOptionsDTO.FieldMapping mapping = new FieldOptionsDTO.FieldMapping();
                mapping.setId(protocol.getCode());
                mapping.setName(protocol.getDescription());
                mappings.add(mapping);
            }
        }
        // Check if field contains Response_Status
        else if (fieldName.toLowerCase().contains("response_status")) {
            for (ResponseStatusEnum status : ResponseStatusEnum.values()) {
                FieldOptionsDTO.FieldMapping mapping = new FieldOptionsDTO.FieldMapping();
                mapping.setId(status.getCode());
                mapping.setName(status.getDescription());
                mappings.add(mapping);
            }
        }
        
        return mappings;
    }

    /**
     * Build SQL: SELECT groupUniqArray(${field}) FROM `${database}`.`${tableName}` WHERE ${filter}
     */
    private String buildFieldOptionsQuerySql(FieldOptionQueryParam queryParam) {
        StringBuilder sql = new StringBuilder();

        // SELECT clause with groupUniqArray
        sql.append("SELECT groupUniqArray(").append(queryParam.getField()).append(")");

        // FROM clause
        sql.append(" FROM ").append(queryParam.getDatabase()).append(".`").append(queryParam.getTableName()).append("`");

        // WHERE clause
        String filter = queryParam.getFilter();
        if (StringUtils.hasText(filter)) {
            sql.append(" WHERE ").append(filter);
        }

        return sql.toString();
    }

}
