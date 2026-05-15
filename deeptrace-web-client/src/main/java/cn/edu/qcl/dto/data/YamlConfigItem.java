package cn.edu.qcl.dto.data;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * YAML Configuration Item
 * Represents a single key-value pair in YAML configuration
 * Used for frontend-backend data exchange in JSON format
 * 
 * Example:
 * { "key": "static_config.profiler", "value": false }
 * { "key": "log_level", "value": "DEBUG" }
 */
@Getter
@Setter
public class YamlConfigItem implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Configuration key path
     * Supports dot notation for nested keys (e.g., "static_config.profiler")
     */
    private String key;

    /**
     * Configuration value
     * Can be String, Boolean, Number, etc.
     */
    private Object value;

    @Override
    public String toString() {
        return "YamlConfigItem{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}