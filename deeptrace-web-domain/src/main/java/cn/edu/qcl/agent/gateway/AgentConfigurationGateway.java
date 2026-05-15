package cn.edu.qcl.agent.gateway;

import cn.edu.qcl.dto.data.AgentConfigurationDTO;
import cn.edu.qcl.dto.param.AgentConfigurationPageQuery;

import java.util.List;

/**
 * Agent Configuration Gateway Interface
 * Defines the data access operations for agent_configuration
 */
public interface AgentConfigurationGateway {

    /**
     * Query agent configurations with pagination and filtering
     *
     * @param query query parameters including pagination and filters
     * @return list of agent configurations
     */
    List<AgentConfigurationDTO> queryByPage(AgentConfigurationPageQuery query);

    /**
     * Count total agent configurations with filter conditions
     *
     * @param query filter conditions
     * @return total count
     */
    long count(AgentConfigurationPageQuery query);
}