package cn.edu.qcl.agent;

import cn.edu.qcl.api.AgentServiceI;
import cn.edu.qcl.dto.SaveAgentUserConfigurationCmd;
import cn.edu.qcl.dto.data.AgentConfigurationDTO;
import cn.edu.qcl.dto.data.AgentDTO;
import cn.edu.qcl.dto.data.AgentUserConfigurationDTO;
import cn.edu.qcl.dto.data.YamlConfigItem;
import cn.edu.qcl.dto.param.AgentConfigurationPageQuery;
import cn.edu.qcl.dto.param.AgentPageQuery;
import cn.edu.qcl.dto.param.AgentUserConfigurationQuery;
import cn.edu.qcl.agent.gateway.AgentConfigurationGateway;
import cn.edu.qcl.agent.gateway.AgentGateway;
import cn.edu.qcl.agent.gateway.AgentUserConfigurationGateway;
import cn.edu.qcl.utils.JsonYamlConverter;
import cn.edu.qcl.utils.UserSessionUtils;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Save agent user configuration
     * <p>
     * Merges user-modified configuration items with the base configuration from agent_configuration table,
     * and saves the merged full YAML configuration to agent_user_configuration table.
     * Ensures only one record with status='pending' exists for the agent.
     * </p>
     *
     * @param cmd the save command containing:
     *            agentLcuuid - agent unique identifier (required)
     *            configItems - list of user-modified configuration items with key-value pairs
     * @return Response indicating success or failure
     */
    @Override
    public Response saveAgentUserConfiguration(SaveAgentUserConfigurationCmd cmd) {
        log.info("Saving agent user configuration for agentLcuuid={}", cmd.getAgentLcuuid());

        // Validate required parameters
        if (!StringUtils.hasText(cmd.getAgentLcuuid())) {
            log.error("agentLcuuid is required");
            return Response.buildFailure("AGENT_LCUUID_REQUIRED", "agentLcuuid is required");
        }

        if (cmd.getConfigItems() == null || cmd.getConfigItems().isEmpty()) {
            log.error("configItems is required and cannot be empty");
            return Response.buildFailure("CONFIG_ITEMS_REQUIRED", "configItems is required and cannot be empty");
        }

        try {
            // 1. Get base configuration from agent_configuration table
            AgentConfigurationDTO baseConfig = agentConfigurationGateway.queryByAgentLcuuid(cmd.getAgentLcuuid());
            if (baseConfig == null) {
                log.error("No base configuration found for agentLcuuid={}", cmd.getAgentLcuuid());
                return Response.buildFailure("BASE_CONFIG_NOT_FOUND", "No base configuration found for the agent");
            }

            // 2. Parse base YAML to Map
            Map<String, Object> configMap;
            if (StringUtils.hasText(baseConfig.getYaml())) {
                String baseJson = JsonYamlConverter.yamlToJson(baseConfig.getYaml());
                ObjectMapper objectMapper = new ObjectMapper();
                configMap = objectMapper.readValue(baseJson, LinkedHashMap.class);
            } else {
                configMap = new LinkedHashMap<>();
            }

            // 3. Apply user modifications
            for (YamlConfigItem item : cmd.getConfigItems()) {
                if (item.getKey() != null) {
                    setNestedValue(configMap, item.getKey(), item.getValue());
                }
            }

            // 4. Convert merged config back to YAML
            ObjectMapper objectMapper = new ObjectMapper();
            String mergedJson = objectMapper.writeValueAsString(configMap);
            String mergedYaml = JsonYamlConverter.jsonToYaml(mergedJson);

            // 5. Get current user ID
            Long userId = UserSessionUtils.getCurrentUserId();

            // 6. Save to agent_user_configuration table
            agentUserConfigurationGateway.insertOrUpdatePending(cmd.getAgentLcuuid(), mergedYaml, userId);

            log.info("Successfully saved agent user configuration for agentLcuuid={}", cmd.getAgentLcuuid());
            return Response.buildSuccess();

        } catch (Exception e) {
            log.error("Failed to save agent user configuration for agentLcuuid={}", cmd.getAgentLcuuid(), e);
            return Response.buildFailure("SAVE_CONFIG_FAILED", "Failed to save agent user configuration: " + e.getMessage());
        }
    }

    /**
     * Set a nested value in a Map using dot notation key
     * Example: key = "static_config.profiler" will set map.get("static_config").put("profiler", value)
     *
     * @param map   the map to modify
     * @param key   the dot notation key
     * @param value the value to set
     */
    @SuppressWarnings("unchecked")
    private void setNestedValue(Map<String, Object> map, String key, Object value) {
        String[] parts = key.split("\\.");
        Map<String, Object> current = map;

        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            Object next = current.get(part);
            if (next == null || !(next instanceof Map)) {
                next = new LinkedHashMap<String, Object>();
                current.put(part, next);
            }
            current = (Map<String, Object>) next;
        }

        current.put(parts[parts.length - 1], value);
    }
}