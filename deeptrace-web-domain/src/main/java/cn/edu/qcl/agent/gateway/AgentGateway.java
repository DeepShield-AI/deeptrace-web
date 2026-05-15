package cn.edu.qcl.agent.gateway;

import cn.edu.qcl.dto.data.AgentDTO;
import cn.edu.qcl.dto.param.AgentPageQuery;

import java.util.List;

/**
 * Agent Gateway Interface
 * Defines the data access operations for agent
 */
public interface AgentGateway {

    /**
     * Query agents with pagination and filtering
     *
     * @param query query parameters including pagination and filters
     * @return list of agents
     */
    List<AgentDTO> queryByPage(AgentPageQuery query);

    /**
     * Count total agents with filter conditions
     *
     * @param query filter conditions
     * @return total count
     */
    long count(AgentPageQuery query);
}