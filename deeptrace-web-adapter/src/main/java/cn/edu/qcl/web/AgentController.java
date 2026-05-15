package cn.edu.qcl.web;

import cn.edu.qcl.api.AgentServiceI;
import cn.edu.qcl.dto.SaveAgentUserConfigurationCmd;
import cn.edu.qcl.dto.data.AgentConfigurationDTO;
import cn.edu.qcl.dto.data.AgentDTO;
import cn.edu.qcl.dto.data.AgentUserConfigurationDTO;
import cn.edu.qcl.dto.param.AgentConfigurationPageQuery;
import cn.edu.qcl.dto.param.AgentPageQuery;
import cn.edu.qcl.dto.param.AgentUserConfigurationQuery;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Agent Query Controller
 * Provides API endpoint for querying agents with pagination and filtering
 */
@Slf4j
@RestController
@RequestMapping("/api/agent")
public class AgentController {

    @Resource
    private AgentServiceI agentServiceI;

    /**
     * Query agents with pagination and filtering
     * <p>
     * Supports filtering by lcuuid (exact match), name (fuzzy match), state (exact match), user_id (exact match).
     * Returns paginated results with total count.
     * </p>
     *
     * @param pageIndex Page number (1-based, default 1)
     * @param pageSize  Page size (default 10)
     * @param lcuuid    Filter by lcuuid - exact match (optional)
     * @param name      Filter by name - fuzzy match (optional)
     * @param state     Filter by state - exact match (optional)
     * @param userId    Filter by user ID - exact match (optional)
     * @return PageResponse containing list of AgentDTO with pagination info
     */
    @GetMapping("/list")
    public PageResponse<AgentDTO> queryAgentPage(
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "lcuuid", required = false) String lcuuid,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "userId", required = false) Long userId) {

        log.info("Received agent page query request: pageIndex={}, pageSize={}, lcuuid={}, name={}, state={}, userId={}",
                pageIndex, pageSize, lcuuid, name, state, userId);

        AgentPageQuery query = new AgentPageQuery();
        query.setPageIndex(pageIndex);
        query.setPageSize(pageSize);
        query.setLcuuid(lcuuid);
        query.setName(name);
        query.setState(state);
        query.setUserId(userId);

        return agentServiceI.queryAgentPage(query);
    }

    /**
     * Query agent configurations with pagination and filtering
     * <p>
     * Supports filtering by agent_lcuuid (exact match), user_id (exact match).
     * Returns paginated results with total count.
     * </p>
     *
     * @param pageIndex    Page number (1-based, default 1)
     * @param pageSize     Page size (default 10)
     * @param agentLcuuid  Filter by agent lcuuid - exact match (optional)
     * @param userId       Filter by user ID - exact match (optional)
     * @return PageResponse containing list of AgentConfigurationDTO with pagination info
     */
    @GetMapping("/configuration/list")
    public PageResponse<AgentConfigurationDTO> queryAgentConfigurationPage(
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "agentLcuuid", required = false) String agentLcuuid,
            @RequestParam(value = "userId", required = false) Long userId) {

        log.info("Received agent configuration page query request: pageIndex={}, pageSize={}, agentLcuuid={}, userId={}",
                pageIndex, pageSize, agentLcuuid, userId);

        AgentConfigurationPageQuery query = new AgentConfigurationPageQuery();
        query.setPageIndex(pageIndex);
        query.setPageSize(pageSize);
        query.setAgentLcuuid(agentLcuuid);
        query.setUserId(userId);

        return agentServiceI.queryAgentConfigurationPage(query);
    }

    /**
     * Query the latest agent user configuration
     * <p>
     * Query the latest agent user configuration matching the filter criteria.
     * Supports filtering by agent_lcuuid (required), user_id (optional).
     * Only returns records with status 'pending' or 'success' (fixed condition).
     * Returns the most recent configuration record.
     * </p>
     *
     * @param agentLcuuid Filter by agent lcuuid - exact match (required)
     * @param userId      Filter by user ID - exact match (optional)
     * @return SingleResponse containing AgentUserConfigurationDTO or null if not found
     */
    @GetMapping("/user-configuration/latest")
    public SingleResponse<AgentUserConfigurationDTO> queryLatestAgentUserConfiguration(
            @RequestParam(value = "agentLcuuid") String agentLcuuid,
            @RequestParam(value = "userId", required = false) Long userId) {

        log.info("Received latest agent user configuration query request: agentLcuuid={}, userId={}",
                agentLcuuid, userId);

        AgentUserConfigurationQuery query = new AgentUserConfigurationQuery();
        query.setAgentLcuuid(agentLcuuid);
        query.setUserId(userId);

        return agentServiceI.queryLatestAgentUserConfiguration(query);
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
     *                          Example: [{ "key": "static_config.profiler", "value": false }, { "key": "log_level", "value": "DEBUG" }]
     * @return Response indicating success or failure
     */
    @PostMapping("/user-configuration/save")
    public Response saveAgentUserConfiguration(@RequestBody SaveAgentUserConfigurationCmd cmd) {
        log.info("Received save agent user configuration request: agentLcuuid={}, configItems count={}",
                cmd.getAgentLcuuid(), cmd.getConfigItems() != null ? cmd.getConfigItems().size() : 0);

        return agentServiceI.saveAgentUserConfiguration(cmd);
    }
}