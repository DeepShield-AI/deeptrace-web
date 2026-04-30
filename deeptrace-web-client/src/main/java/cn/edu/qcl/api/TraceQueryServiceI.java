package cn.edu.qcl.api;

import cn.edu.qcl.dto.data.FieldOptionsDTO;
import cn.edu.qcl.dto.data.FilterFieldsDTO;
import cn.edu.qcl.dto.param.FieldOptionQueryParam;

/**
 * Metric Query Service Interface
 * Provides metric query functionality with enum value mapping
 */
public interface TraceQueryServiceI {

    /**
     * Query metrics from ClickHouse with enum value mapping
     * 
     * @param queryParam the query parameters including database, table, field, and filter
     * @return MetricQueryResult containing query data and enum mappings
     */
    FieldOptionsDTO queryFieldOptions(FieldOptionQueryParam queryParam);

    /**
     * Get filter fields configuration for a specific table
     * Returns fixed configuration based on database and table name
     * 
     * @param database Database name
     * @param tableName Table name
     * @return TableFilterFieldsDTO containing filter fields configuration
     */
    FilterFieldsDTO getTableFilterFields(String database, String tableName);
}