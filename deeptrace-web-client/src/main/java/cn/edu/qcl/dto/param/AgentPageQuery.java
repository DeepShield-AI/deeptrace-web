package cn.edu.qcl.dto.param;

import com.alibaba.cola.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Agent Page Query Parameter
 * Used for paginated queries with filtering by lcuuid, name, state, user_id
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class AgentPageQuery extends PageQuery {
    /**
     * 唯一标识 filter (exact match)
     */
    private String lcuuid;

    /**
     * 名称 filter (fuzzy match)
     */
    private String name;

    /**
     * 状态 filter (exact match)
     */
    private String state;

    /**
     * 用户ID filter (exact match)
     */
    private Long userId;
}