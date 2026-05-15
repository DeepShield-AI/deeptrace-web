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

    /**
     * Query pending record by agent lcuuid
     *
     * @param agentLcuuid agent unique identifier
     * @return AgentUserConfigurationDO with status='pending' or null if not found
     */
    AgentUserConfigurationDO queryPendingByAgentLcuuid(@Param("agentLcuuid") String agentLcuuid);

    /**
     * Insert a new agent user configuration record
     *
     * @param agentUserConfigurationDO the record to insert
     * @return number of rows affected
     */
    int insert(AgentUserConfigurationDO agentUserConfigurationDO);

    /**
     * Update an existing agent user configuration record
     *
     * @param agentUserConfigurationDO the record to update
     * @return number of rows affected
     */
    int updateById(AgentUserConfigurationDO agentUserConfigurationDO);
}