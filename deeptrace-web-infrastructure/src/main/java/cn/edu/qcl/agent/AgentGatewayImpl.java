package cn.edu.qcl.agent;

import cn.edu.qcl.agent.gateway.AgentGateway;
import cn.edu.qcl.dto.data.AgentDTO;
import cn.edu.qcl.dto.param.AgentPageQuery;
import cn.edu.qcl.mapper.mysql.AgentMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Agent Gateway Implementation
 * Implements data access operations for agent
 */
@Repository
public class AgentGatewayImpl implements AgentGateway {

    @Resource
    private AgentMapper agentMapper;

    @Override
    public List<AgentDTO> queryByPage(AgentPageQuery query) {
        AgentDO agentDO = new AgentDO();
        agentDO.setLcuuid(query.getLcuuid());
        agentDO.setName(query.getName());
        agentDO.setState(query.getState());
        agentDO.setUserId(query.getUserId());

        PageRequest pageRequest = PageRequest.of(query.getPageIndex(), query.getPageSize());
        List<AgentDO> agentDOList = agentMapper.queryByPage(agentDO, pageRequest);

        if (agentDOList == null || agentDOList.isEmpty()) {
            return Collections.emptyList();
        }

        return agentDOList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long count(AgentPageQuery query) {
        AgentDO agentDO = new AgentDO();
        agentDO.setLcuuid(query.getLcuuid());
        agentDO.setName(query.getName());
        agentDO.setState(query.getState());
        agentDO.setUserId(query.getUserId());

        return agentMapper.count(agentDO);
    }

    /**
     * Convert DO to DTO
     *
     * @param agentDO database entity
     * @return DTO object
     */
    private AgentDTO convertToDTO(AgentDO agentDO) {
        if (agentDO == null) {
            return null;
        }
        AgentDTO dto = new AgentDTO();
        BeanUtils.copyProperties(agentDO, dto);
        return dto;
    }
}