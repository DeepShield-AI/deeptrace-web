package cn.edu.qcl.trace.strategy;

import cn.edu.qcl.dto.data.FieldOptionsDTO;
import cn.edu.qcl.dto.param.FieldOptionQueryParam;

/**
 * Field Options Query Strategy Interface
 * Defines the contract for different query methods
 * 
 * Using Strategy Pattern to avoid if-else logic for method dispatch
 */
public interface FieldOptionsQueryStrategy {

    /**
     * Get the strategy name (e.g., "query", "sql")
     * Used for strategy lookup
     *
     * @return the strategy name
     */
    String getStrategyName();

    /**
     * Execute the query and return field options
     *
     * @param queryParam the query parameters
     * @return FieldOptionsDTO containing query data and enum mappings
     */
    FieldOptionsDTO query(FieldOptionQueryParam queryParam);
}