package cn.edu.qcl.agent;

import cn.edu.qcl.agent.gateway.AgentConfigurationGateway;
import cn.edu.qcl.dto.data.AgentConfigurationDTO;
import cn.edu.qcl.dto.param.AgentConfigurationPageQuery;
import cn.edu.qcl.mapper.mysql.AgentConfigurationMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Agent Configuration Gateway Implementation
 * Implements data access operations for agent_configuration
 */
@Repository
public class AgentConfigurationGatewayImpl implements AgentConfigurationGateway {

    @Resource
    private AgentConfigurationMapper agentConfigurationMapper;

    @Override
    public List<AgentConfigurationDTO> queryByPage(AgentConfigurationPageQuery query) {
        AgentConfigurationDO agentConfigurationDO = new AgentConfigurationDO();
        agentConfigurationDO.setAgentLcuuid(query.getAgentLcuuid());
        agentConfigurationDO.setUserId(query.getUserId());

        PageRequest pageRequest = PageRequest.of(query.getPageIndex(), query.getPageSize());
        List<AgentConfigurationDO> agentConfigurationDOList = agentConfigurationMapper.queryByPage(agentConfigurationDO, pageRequest);

        if (agentConfigurationDOList == null || agentConfigurationDOList.isEmpty()) {
            return Collections.emptyList();
        }

        return agentConfigurationDOList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long count(AgentConfigurationPageQuery query) {
        AgentConfigurationDO agentConfigurationDO = new AgentConfigurationDO();
        agentConfigurationDO.setAgentLcuuid(query.getAgentLcuuid());
        agentConfigurationDO.setUserId(query.getUserId());

        return agentConfigurationMapper.count(agentConfigurationDO);
    }

    /**
     * Convert DO to DTO
     *
     * @param agentConfigurationDO database entity
     * @return DTO object
     */
    private AgentConfigurationDTO convertToDTO(AgentConfigurationDO agentConfigurationDO) {
        if (agentConfigurationDO == null) {
            return null;
        }
        AgentConfigurationDTO dto = new AgentConfigurationDTO();
        BeanUtils.copyProperties(agentConfigurationDO, dto);
        return dto;
    }
}