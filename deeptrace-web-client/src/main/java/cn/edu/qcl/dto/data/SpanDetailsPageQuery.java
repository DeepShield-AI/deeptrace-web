package cn.edu.qcl.dto.data;

import com.alibaba.cola.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Span明细分页查询参数
 * 继承PageQuery，包含分页信息和查询条件
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpanDetailsPageQuery extends PageQuery {
    /**
     * Database name (e.g., "flow_log")
     */
    private String database;

    /**
     * Table name (e.g., "l7_flow_log")
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