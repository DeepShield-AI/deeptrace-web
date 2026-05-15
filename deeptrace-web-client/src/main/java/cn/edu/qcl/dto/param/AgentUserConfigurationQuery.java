package cn.edu.qcl.dto.param;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Agent User Configuration Query Parameters
 * Used for querying the latest agent user configuration
 * Note: Status is fixed to 'pending' or 'success' in the query, not a parameter
 */
@Getter
@Setter
public class AgentUserConfigurationQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * agent的唯一标识
     */
    private String agentLcuuid;

    /**
     * 用户ID
     */
    private Long userId;
}