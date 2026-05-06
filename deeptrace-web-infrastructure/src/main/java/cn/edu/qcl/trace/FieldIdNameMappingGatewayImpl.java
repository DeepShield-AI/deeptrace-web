package cn.edu.qcl.trace;

import cn.edu.qcl.mapper.clickhouse.ClickHouseMapper;
import cn.edu.qcl.trace.gateway.FieldIdNameMappingGateway;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
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
            "l3_device_type",
            "auto_instance_id",
            "auto_service_id"
    ));

    private static final Set<String> NORMAL_DEVICE_FIELDS = new HashSet<>(Arrays.asList(
            "region_id",
            "az_id",
            "pod_cluster_id",
            "pod_ns_id",
            "pod_group_id",
            "pod_id",
            "pod_node_id",
            "service_id",
            "l3_epc_id",
            "subnet_id"
    ));


    @Resource
    private ClickHouseMapper clickHouseMapper;

    /**
     * 获取字段的枚举映射关系
     * <p>
     * 根据字段名称解析对应的资源表，从ClickHouse中查询ID到名称的映射关系。
     * 对于特殊设备字段（如l3_device_id、auto_instance_id、auto_service_id）使用特殊的查询逻辑。
     * </p>
     *
     * @param fieldName 字段名称，支持以下格式：
     *                  - resourceStr_id：标准字段格式
     *                  - resourceStr_id_0：带版本后缀的字段格式
     *                  - resourceStr_id_1：带版本后缀的字段格式
     *                  - 特殊设备字段：l3_device_id、auto_instance_id、auto_service_id
     * @return 枚举映射列表，每个元素为包含id和name字段的Map；如果字段名为空或查询失败则返回空列表
     */
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
            
            /*
             * 根据字段类型执行不同的查询策略：
             * - 特殊设备字段：查询devicetype、deviceid和name，从flow_tag.device_map表获取数据
             * - 标准字段：查询id和name，从对应的资源映射表获取数据
             */
            String sql="";
            
            if (isSpecialDeviceField(fieldName)) {
                sql = "SELECT devicetype as  type, deviceid as id, name FROM flow_tag.device_map";
            } else if (StringUtils.equalsIgnoreCase(fieldName, "host_id")) {
                sql = "SELECT id, name FROM flow_tag.chost_map";
            } else if (isNormalDeviceField(fieldName)) {
                // Standard handling: SELECT id, name
                sql = String.format("SELECT id, name FROM %s", tableName);
               
            }else {
                log.info("Unsupported field name: {}", fieldName);
                return Collections.emptyList();
            }
            log.debug("Executing standard SQL: {}", sql);
            results = clickHouseMapper.executeQuery(sql);
            return results;
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
    
    public boolean isNormalDeviceField(String fieldName) {
        return NORMAL_DEVICE_FIELDS.contains(fieldName);
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