package cn.edu.qcl.agent.gateway;

import cn.edu.qcl.dto.data.AgentUserConfigurationDTO;
import cn.edu.qcl.dto.param.AgentUserConfigurationQuery;

/**
 * Agent User Configuration Gateway Interface
 * Defines the data access operations for agent_user_configuration
 */
public interface AgentUserConfigurationGateway {

    /**
     * Query the latest agent user configuration matching the filter criteria
     *
     * @param query filter conditions including agentLcuuid, userId, status
     * @return the latest AgentUserConfigurationDTO or null if not found
     */
    AgentUserConfigurationDTO queryLatest(AgentUserConfigurationQuery query);
}