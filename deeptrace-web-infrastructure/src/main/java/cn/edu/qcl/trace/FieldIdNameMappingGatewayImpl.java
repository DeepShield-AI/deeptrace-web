package cn.edu.qcl.trace;

import cn.edu.qcl.mapper.clickhouse.ClickHouseMapper;
import cn.edu.qcl.trace.gateway.FieldIdNameMappingGateway;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Enum Mapping Gateway Implementation
 * Handles the mapping between field names and table names, and queries enum values from ClickHouse
 */
@Repository
public class FieldIdNameMappingGatewayImpl implements FieldIdNameMappingGateway {

    private static final Logger log = LoggerFactory.getLogger(FieldIdNameMappingGatewayImpl.class);

    /**
     * Special device fields that require special SQL handling
     * These fields use devicetype, deviceid, name columns instead of id, name
     */
    private static final Set<String> SPECIAL_DEVICE_FIELDS = new HashSet<>(Arrays.asList(
            "l3_device_id",
            "auto_instance_id",
            "auto_service_id"
    ));

    @Resource
    private ClickHouseMapper clickHouseMapper;

    @Override
    public List<Map<String, Object>> getEnumMapping(String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String resourceName = parseResourceName(fieldName);
        if (resourceName == null) {
            log.debug("Could not parse resource name from field: {}", fieldName);
            return Collections.emptyList();
        }

        String tableName = getResourceMapTableName(resourceName);
        log.info("Getting enum mapping for field: {}, resource: {}, table: {}", fieldName, resourceName, tableName);

        try {
            List<Map<String, Object>> results;
            
            if (isSpecialDeviceField(fieldName)) {
                // Special handling for device fields: SELECT devicetype, deviceid, name
                String sql = "SELECT devicetype as  type, deviceid as id, name FROM flow_tag.device_map";
                log.debug("Executing special device SQL: {}", sql);
                results = clickHouseMapper.executeQuery(sql);
                return results;
            } else {
                // Standard handling: SELECT id, name
                String sql = String.format("SELECT id, name FROM %s", tableName);
                log.debug("Executing standard SQL: {}", sql);
                results = clickHouseMapper.executeQuery(sql);
                return results;
            }
        } catch (Exception e) {
            log.error("Failed to get enum mapping for field: {}", fieldName, e);
            return Collections.emptyList();
        }
    }

    @Override
    public String parseResourceName(String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            return null;
        }

        // Pattern: resourceStr + "_id" or resourceStr + "_id_1" or resourceStr + "_id_0"
        // We need to extract resourceStr from the field name
        
        // Check for "_id_0" or "_id_1" suffix first
        if (fieldName.endsWith("_id_0") || fieldName.endsWith("_id_1")) {
            // Remove the "_id_0" or "_id_1" suffix
            return fieldName.substring(0, fieldName.length() - 5);
        }
        
        // Check for "_id" suffix
        if (fieldName.endsWith("_id")) {
            // Remove the "_id" suffix
            return fieldName.substring(0, fieldName.length() - 3);
        }

        return fieldName;
    }

    @Override
    public String getResourceMapTableName(String resourceName) {
        if (resourceName == null || resourceName.trim().isEmpty()) {
            return null;
        }
        // Table name pattern: flow_tag." + resourceStr + "_map
        return "flow_tag." + resourceName + "_map";
    }

    @Override
    public boolean isSpecialDeviceField(String fieldName) {
        return SPECIAL_DEVICE_FIELDS.contains(fieldName);
    }

    /**
     * Build standard ID to name mapping from query results
     * 
     * @param results query results with "id" and "name" columns
     * @return Map of ID to name
     */
 /*   private Map<String, String> buildStandardMapping(List<Map<String, Object>> results) {
        Map<String, String> mapping = new HashMap<>();
        if (results == null || results.isEmpty()) {
            return mapping;
        }

        for (Map<String, Object> row : results) {
            Object idObj = row.get("id");
            Object nameObj = row.get("name");
            
            if (idObj != null && nameObj != null) {
                String id = String.valueOf(idObj);
                String name = String.valueOf(nameObj);
                mapping.put(id, name);
            }
        }

        log.debug("Built standard mapping with {} entries", mapping.size());
        return mapping;
    }*/

    /**
     * Build device mapping from query results
     * For device fields, we create a composite key from devicetype and deviceid
     * 
     * @param results query results with "devicetype", "deviceid", and "name" columns
     * @return Map of composite key (devicetype_deviceid) to name
     */
   /* private Map<String, String> buildDeviceMapping(List<Map<String, Object>> results) {
        Map<String, String> mapping = new HashMap<>();
        if (results == null || results.isEmpty()) {
            return mapping;
        }

        for (Map<String, Object> row : results) {
            Object deviceTypeObj = row.get("devicetype");
            Object deviceIdObj = row.get("deviceid");
            Object nameObj = row.get("name");
            
            if (deviceTypeObj != null && deviceIdObj != null && nameObj != null) {
                String deviceType = String.valueOf(deviceTypeObj);
                String deviceId = String.valueOf(deviceIdObj);
                String name = String.valueOf(nameObj);
                
                // Create composite key: devicetype_deviceid
                String compositeKey = deviceType + "|" + deviceId;
                mapping.put(compositeKey, name);
            }
        }

        log.debug("Built device mapping with {} entries", mapping.size());
        return mapping;
    }*/
}