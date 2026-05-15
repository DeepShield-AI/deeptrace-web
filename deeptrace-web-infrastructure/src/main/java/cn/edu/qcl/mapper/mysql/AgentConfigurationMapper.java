package cn.edu.qcl.mapper.mysql;

import cn.edu.qcl.agent.AgentConfigurationDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Agent Configuration Mapper Interface
 * Database access layer for agent_configuration table
 */
public interface AgentConfigurationMapper {

    /**
     * Query agent configurations with pagination and filtering
     *
     * @param agentConfigurationDO filter conditions
     * @param pageable             pagination parameters
     * @return list of agent configurations
     */
    List<AgentConfigurationDO> queryByPage(@Param("agentConfigurationDO") AgentConfigurationDO agentConfigurationDO, @Param("pageable") Pageable pageable);

    /**
     * Count total rows with filter conditions
     *
     * @param agentConfigurationDO filter conditions
     * @return total count
     */
    long count(@Param("agentConfigurationDO") AgentConfigurationDO agentConfigurationDO);

    /**
     * Query agent configuration by agent lcuuid
     *
     * @param agentLcuuid agent unique identifier
     * @return AgentConfigurationDO or null if not found
     */
    AgentConfigurationDO queryByAgentLcuuid(@Param("agentLcuuid") String agentLcuuid);
}