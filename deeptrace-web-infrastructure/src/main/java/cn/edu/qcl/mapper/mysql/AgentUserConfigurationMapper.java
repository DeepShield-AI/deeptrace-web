package cn.edu.qcl.mapper.mysql;

import cn.edu.qcl.agent.AgentUserConfigurationDO;
import org.apache.ibatis.annotations.Param;

/**
 * Agent User Configuration Mapper Interface
 * Database access layer for agent_user_configuration table
 */
public interface AgentUserConfigurationMapper {

    /**
     * Query the latest agent user configuration matching the filter criteria
     *
     * @param agentUserConfigurationDO filter conditions
     * @return the latest AgentUserConfigurationDO or null if not found
     */
    AgentUserConfigurationDO queryLatest(@Param("agentUserConfigurationDO") AgentUserConfigurationDO agentUserConfigurationDO);
}