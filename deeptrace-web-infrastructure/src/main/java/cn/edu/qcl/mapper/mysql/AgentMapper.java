package cn.edu.qcl.mapper.mysql;

import cn.edu.qcl.agent.AgentDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Agent Mapper Interface
 * Database access layer for agent_basic table
 */
public interface AgentMapper {

    /**
     * Query agents with pagination and filtering
     *
     * @param agentDO  filter conditions
     * @param pageable pagination parameters
     * @return list of agents
     */
    List<AgentDO> queryByPage(@Param("agentDO") AgentDO agentDO, @Param("pageable") Pageable pageable);

    /**
     * Count total rows with filter conditions
     *
     * @param agentDO filter conditions
     * @return total count
     */
    long count(@Param("agentDO") AgentDO agentDO);
}