package cn.edu.qcl.trace.gateway;

import java.util.List;
import java.util.Map;

/**
 * Enum Mapping Gateway Interface
 * Provides functionality to query enum value mappings from ClickHouse map tables
 */
public interface FieldIdNameMappingGateway {

    /**
     * Get enum value mappings for a specific field
     * 
     * @param fieldName the field name (e.g., "l3_device_id", "auto_instance_id")
     * @return Map of ID to name mappings
     */
    List<Map<String, Object>> getEnumMapping(String fieldName);

    /**
     * Parse resource name from field name
     * Field name pattern: resourceStr + "_id" or resourceStr + "_id_1" or resourceStr + "_id_0"
     * 
     * @param fieldName the field name to parse
     * @return resource name extracted from field name, or null if not a valid pattern
     */
    String parseResourceName(String fieldName);

    /**
     * Get table name for a given resource name
     * Table name pattern: 'flow_tag." + resourceStr + "_map'
     * 
     * @param resourceName the resource name
     * @return full table name for the resource
     */
    String getResourceMapTableName(String resourceName);

    /**
     * Check if field is a special device-related field that needs special handling
     * Special fields: l3_device_id, auto_instance_id, auto_service_id
     * 
     * @param fieldName the field name to check
     * @return true if the field is a special device field
     */
    boolean isSpecialDeviceField(String fieldName);
}