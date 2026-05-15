package cn.edu.qcl.dto;

import cn.edu.qcl.dto.data.YamlConfigItem;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Save Agent User Configuration Command
 * Used for saving user-modified YAML configuration
 * 
 * The user-modified configuration items will be merged with
 * the base configuration from agent_configuration table,
 * and the merged full YAML will be saved to agent_user_configuration table.
 */
@Getter
@Setter
public class SaveAgentUserConfigurationCmd implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Agent unique identifier (required)
     * Used to find the base configuration from agent_configuration table
     */
    private String agentLcuuid;

    /**
     * User-modified configuration items (required)
     * Each item contains a key (dot notation path) and value
     * 
     * Example:
     * [
     *   { "key": "static_config.profiler", "value": false },
     *   { "key": "log_level", "value": "DEBUG" }
     * ]
     */
    private List<YamlConfigItem> configItems;

    @Override
    public String toString() {
        return "SaveAgentUserConfigurationCmd{" +
                "agentLcuuid='" + agentLcuuid + '\'' +
                ", configItems=" + configItems +
                '}';
    }
}