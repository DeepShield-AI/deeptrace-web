package cn.edu.qcl.trace;

import cn.edu.qcl.api.TraceQueryServiceI;
import cn.edu.qcl.dto.data.FieldOptionsDTO;
import cn.edu.qcl.dto.param.FieldOptionQueryParam;
import cn.edu.qcl.trace.strategy.FieldOptionsQueryStrategy;
import cn.edu.qcl.trace.strategy.FieldOptionsQueryStrategyFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Metric Query Service Implementation
 * Handles metric queries with enum value mapping support
 * 
 * Uses Strategy Pattern to dispatch queries based on method parameter
 * Avoids if-else logic for better extensibility
 */
@Slf4j
@Service
public class TraceQueryServiceImpl implements TraceQueryServiceI {

    @Resource
    private FieldOptionsQueryStrategyFactory strategyFactory;

    @Override
    public FieldOptionsDTO queryFieldOptions(FieldOptionQueryParam queryParam) {
        // Validate input parameters
        validateQueryParam(queryParam);

        log.info("Querying metrics with params: database={}, tableName={}, method={}, field={}, filter={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getMethod(), 
                queryParam.getField(), queryParam.getFilter());

        // Get strategy based on method parameter (no if-else needed)
        FieldOptionsQueryStrategy strategy = getStrategy(queryParam.getMethod());

        // Execute query using the selected strategy
        return strategy.query(queryParam);
    }

    /**
     * Validate query parameters
     */
    private void validateQueryParam(FieldOptionQueryParam queryParam) {
        if (queryParam == null) {
            throw new IllegalArgumentException("Query parameters cannot be null");
        }
        if (!StringUtils.hasText(queryParam.getDatabase())) {
            throw new IllegalArgumentException("Database name is required");
        }
        if (!StringUtils.hasText(queryParam.getTableName())) {
            throw new IllegalArgumentException("Table name is required");
        }
        if (!StringUtils.hasText(queryParam.getField())) {
            throw new IllegalArgumentException("Field is required");
        }
    }

    /**
     * Get strategy based on method parameter
     * Returns default strategy if method is null or empty
     * 
     * No if-else needed - uses strategy factory for dispatch
     */
    private FieldOptionsQueryStrategy getStrategy(String method) {
        if (!StringUtils.hasText(method)) {
            return strategyFactory.getDefaultStrategy();
        }
        return strategyFactory.getStrategy(method);
    }
}