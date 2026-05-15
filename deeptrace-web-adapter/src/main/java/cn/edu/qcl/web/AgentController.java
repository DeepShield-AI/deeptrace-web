package cn.edu.qcl.web;

import cn.edu.qcl.api.AgentServiceI;
import cn.edu.qcl.dto.data.AgentDTO;
import cn.edu.qcl.dto.param.AgentPageQuery;
import com.alibaba.cola.dto.PageResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
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
}