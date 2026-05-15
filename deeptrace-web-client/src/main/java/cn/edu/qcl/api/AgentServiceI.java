package cn.edu.qcl.api;

import cn.edu.qcl.dto.data.AgentConfigurationDTO;
import cn.edu.qcl.dto.data.AgentDTO;
import cn.edu.qcl.dto.data.AgentUserConfigurationDTO;
import cn.edu.qcl.dto.param.AgentConfigurationPageQuery;
import cn.edu.qcl.dto.param.AgentPageQuery;
import cn.edu.qcl.dto.param.AgentUserConfigurationQuery;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.SingleResponse;

/**
 * Agent Service Interface
 * Provides agent query functionality with pagination and filtering
 */
public interface AgentServiceI {

    /**
     * Query agents with pagination and filtering
     * <p>
     * Query agents with support for filtering by lcuuid, name, state, user_id.
     * Returns paginated results using PageResponse.
     * </p>
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
    PageResponse<AgentDTO> queryAgentPage(AgentPageQuery query);

    /**
     * Query agent configurations with pagination and filtering
     * <p>
     * Query agent configurations with support for filtering by agent_lcuuid, user_id.
     * Returns paginated results using PageResponse.
     * </p>
     *
     * @param query the query parameters including:
     *              pageIndex - page number (1-based)
     *              pageSize - page size
     *              agentLcuuid - filter by agent lcuuid (exact match, optional)
     *              userId - filter by user ID (exact match, optional)
     * @return PageResponse containing list of AgentConfigurationDTO with pagination info
     */
    PageResponse<AgentConfigurationDTO> queryAgentConfigurationPage(AgentConfigurationPageQuery query);

    /**
     * Query the latest agent user configuration
     * <p>
     * Query the latest agent user configuration with support for filtering by agent_lcuuid, user_id, status.
     * Returns the most recent configuration record matching the criteria.
     * </p>
     *
     * @param query the query parameters including:
     *              agentLcuuid - filter by agent lcuuid (exact match, required)
     *              userId - filter by user ID (exact match, optional)
     *              status - filter by status (supports multiple values separated by comma, e.g., "pending,failed")
     * @return SingleResponse containing AgentUserConfigurationDTO or null if not found
     */
    SingleResponse<AgentUserConfigurationDTO> queryLatestAgentUserConfiguration(AgentUserConfigurationQuery query);
}