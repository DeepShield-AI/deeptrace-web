package cn.edu.qcl.agent;

import cn.edu.qcl.api.AgentServiceI;
import cn.edu.qcl.dto.data.AgentConfigurationDTO;
import cn.edu.qcl.dto.data.AgentDTO;
import cn.edu.qcl.dto.data.AgentUserConfigurationDTO;
import cn.edu.qcl.dto.param.AgentConfigurationPageQuery;
import cn.edu.qcl.dto.param.AgentPageQuery;
import cn.edu.qcl.dto.param.AgentUserConfigurationQuery;
import cn.edu.qcl.agent.gateway.AgentConfigurationGateway;
import cn.edu.qcl.agent.gateway.AgentGateway;
import cn.edu.qcl.agent.gateway.AgentUserConfigurationGateway;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.SingleResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Agent Service Implementation
 * Handles agent queries with pagination and filtering
 */
@Slf4j
@Service
public class AgentServiceImpl implements AgentServiceI {

    @Resource
    private AgentGateway agentGateway;

    @Resource
    private AgentConfigurationGateway agentConfigurationGateway;

    @Resource
    private AgentUserConfigurationGateway agentUserConfigurationGateway;

    /**
     * Query agents with pagination and filtering
     *
     * @param query the query parameters including:
     *              pageIndex - page number (1-based)
     *              pageSize - page size
     *              lcuuid - filter by lcuuid (exact match, optional)
     *              name - filter by name (fuzzy match, optional)
     *              state - filter by state (exact match, optional)
     *              userId - filter by user ID (exact match, optional)
     * @return PageResponse containing list of AgentDTO with pagination info
     */
    @Override
    public PageResponse<AgentDTO> queryAgentPage(AgentPageQuery query) {
        log.info("Querying agents with params: pageIndex={}, pageSize={}, lcuuid={}, name={}, state={}, userId={}",
                query.getPageIndex(), query.getPageSize(), query.getLcuuid(), query.getName(), query.getState(), query.getUserId());

        // Query total count
        long totalCount = agentGateway.count(query);

        // Query data list
        List<AgentDTO> dataList;
        try {
            dataList = agentGateway.queryByPage(query);
        } catch (Exception e) {
            log.error("Failed to query agent list with params: pageIndex={}, pageSize={}, lcuuid={}, name={}, state={}, userId={}",
                    query.getPageIndex(), query.getPageSize(), query.getLcuuid(), query.getName(), query.getState(), query.getUserId(), e);
            throw e;
        }

        log.info("Agent query returned {} records, total count: {}", dataList.size(), totalCount);

        // Build PageResponse
        return PageResponse.of(dataList, (int) totalCount, query.getPageSize(), query.getPageIndex());
    }

    /**
     * Query agent configurations with pagination and filtering
     *
     * @param query the query parameters including:
     *              pageIndex - page number (1-based)
     *              pageSize - page size
     *              agentLcuuid - filter by agent lcuuid (exact match, optional)
     *              userId - filter by user ID (exact match, optional)
     * @return PageResponse containing list of AgentConfigurationDTO with pagination info
     */
    @Override
    public PageResponse<AgentConfigurationDTO> queryAgentConfigurationPage(AgentConfigurationPageQuery query) {
        log.info("Querying agent configurations with params: pageIndex={}, pageSize={}, agentLcuuid={}, userId={}",
                query.getPageIndex(), query.getPageSize(), query.getAgentLcuuid(), query.getUserId());

        // Query total count
        long totalCount = agentConfigurationGateway.count(query);

        // Query data list
        List<AgentConfigurationDTO> dataList;
        try {
            dataList = agentConfigurationGateway.queryByPage(query);
        } catch (Exception e) {
            log.error("Failed to query agent configuration list with params: pageIndex={}, pageSize={}, agentLcuuid={}, userId={}",
                    query.getPageIndex(), query.getPageSize(), query.getAgentLcuuid(), query.getUserId(), e);
            throw e;
        }

        log.info("Agent configuration query returned {} records, total count: {}", dataList.size(), totalCount);

        // Build PageResponse
        return PageResponse.of(dataList, (int) totalCount, query.getPageSize(), query.getPageIndex());
    }

    /**
     * Query the latest agent user configuration
     *
     * @param query the query parameters including:
     *              agentLcuuid - filter by agent lcuuid (exact match, required)
     *              userId - filter by user ID (exact match, optional)
     *              status - filter by status (supports multiple values separated by comma)
     * @return SingleResponse containing AgentUserConfigurationDTO or null if not found
     */
    @Override
    public SingleResponse<AgentUserConfigurationDTO> queryLatestAgentUserConfiguration(AgentUserConfigurationQuery query) {
        log.info("Querying latest agent user configuration with params: agentLcuuid={}, userId={}",
                query.getAgentLcuuid(), query.getUserId());

        AgentUserConfigurationDTO result;
        try {
            result = agentUserConfigurationGateway.queryLatest(query);
        } catch (Exception e) {
            log.error("Failed to query latest agent user configuration with params: agentLcuuid={}, userId={}",
                    query.getAgentLcuuid(), query.getUserId(), e);
            throw e;
        }

        if (result != null) {
            log.info("Found latest agent user configuration: id={}, lcuuid={}", result.getId(), result.getLcuuid());
        } else {
            log.info("No agent user configuration found matching the criteria");
        }

        return SingleResponse.of(result);
    }
}