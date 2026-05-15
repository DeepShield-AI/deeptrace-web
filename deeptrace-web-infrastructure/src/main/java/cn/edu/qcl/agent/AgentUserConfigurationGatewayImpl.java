package cn.edu.qcl.agent;

import cn.edu.qcl.agent.gateway.AgentUserConfigurationGateway;
import cn.edu.qcl.dto.data.AgentUserConfigurationDTO;
import cn.edu.qcl.dto.param.AgentUserConfigurationQuery;
import cn.edu.qcl.mapper.mysql.AgentUserConfigurationMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

/**
 * Agent User Configuration Gateway Implementation
 * Implements data access operations for agent_user_configuration
 * Note: Status is fixed to 'pending' or 'success' in the SQL query
 */
@Repository
public class AgentUserConfigurationGatewayImpl implements AgentUserConfigurationGateway {

    @Resource
    private AgentUserConfigurationMapper agentUserConfigurationMapper;

    @Override
    public AgentUserConfigurationDTO queryLatest(AgentUserConfigurationQuery query) {
        // Status is fixed to 'pending' or 'success' in the SQL query
        // No need to pass status parameter
        AgentUserConfigurationDO agentUserConfigurationDO = new AgentUserConfigurationDO();
        agentUserConfigurationDO.setAgentLcuuid(query.getAgentLcuuid());
        agentUserConfigurationDO.setUserId(query.getUserId());

        AgentUserConfigurationDO result = agentUserConfigurationMapper.queryLatest(agentUserConfigurationDO);
        return convertToDTO(result);
    }

    /**
     * Convert DO to DTO
     *
     * @param agentUserConfigurationDO database entity
     * @return DTO object
     */
    private AgentUserConfigurationDTO convertToDTO(AgentUserConfigurationDO agentUserConfigurationDO) {
        if (agentUserConfigurationDO == null) {
            return null;
        }
        AgentUserConfigurationDTO dto = new AgentUserConfigurationDTO();
        BeanUtils.copyProperties(agentUserConfigurationDO, dto);
        return dto;
    }
}