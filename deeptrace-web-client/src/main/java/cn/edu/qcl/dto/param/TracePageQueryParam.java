package cn.edu.qcl.dto.param;

import com.alibaba.cola.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Trace List Page Query Parameter
 * Inherits PageQuery for pagination info
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TracePageQueryParam extends PageQuery {
    /**
      * Database name (e.g., "apm")
     */
    private String database;

    /**
     * Table name (e.g., "traces_view")
     */
    private String tableName;

    /**
     * Filter condition SQL
     */
    private String filter;

    /**
     * Team ID for multi-tenant filtering
     */
    private Long teamId;
}