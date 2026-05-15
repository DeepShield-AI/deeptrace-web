package cn.edu.qcl.dto.param;

import com.alibaba.cola.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Agent Configuration Page Query Parameter
 * Used for paginated queries with filtering by agent_lcuuid, user_id
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class AgentConfigurationPageQuery extends PageQuery {
    /**
     * agent的唯一标识 filter (exact match)
     */
    private String agentLcuuid;

    /**
     * 用户ID filter (exact match)
     */
    private Long userId;
}