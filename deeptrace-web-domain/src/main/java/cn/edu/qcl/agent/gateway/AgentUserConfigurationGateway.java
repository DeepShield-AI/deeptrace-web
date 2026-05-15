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

    /**
     * Insert or update a pending configuration record for an agent
     * <p>
     * If a pending record exists for the agent, update it with the new YAML configuration.
     * Otherwise, insert a new pending record.
     * This ensures only one record with status='pending' exists for each agent.
     * </p>
     *
     * @param agentLcuuid agent unique identifier
     * @param yaml        merged YAML configuration content
     * @param userId      user ID who made the change
     */
    void insertOrUpdatePending(String agentLcuuid, String yaml, Long userId);
}