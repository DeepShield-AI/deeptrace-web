package cn.edu.qcl.trace;

import cn.edu.qcl.api.TraceQueryServiceI;
import cn.edu.qcl.dto.data.FieldOptionsDTO;
import cn.edu.qcl.dto.data.FilterFieldsDTO;
import cn.edu.qcl.dto.param.FieldOptionQueryParam;
import cn.edu.qcl.trace.strategy.FieldOptionsQueryStrategy;
import cn.edu.qcl.trace.strategy.FieldOptionsQueryStrategyFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Metric Query Service Implementation
 * Handles metric queries with enum value mapping support
 * 
 * Uses Strategy Pattern to dispatch queries based on method parameter
 * Avoids if-else logic for better extensibility
 */
@Slf4j
@Service
public class TraceQueryServiceImpl implements TraceQueryServiceI {

    @Resource
    private FieldOptionsQueryStrategyFactory strategyFactory;

    /**
     * Fixed filter fields configuration for different tables
     * Key: "database.tableName"
     * Value: List of filter field configurations
     */
    private static final Map<String, List<FilterFieldsDTO.FilterFieldConfig>> TABLE_FILTER_FIELDS_MAP = new HashMap<>();

    static {
        // flow_log.l7_flow_log
        List<FilterFieldsDTO.FilterFieldConfig> l7FlowLogFields = new ArrayList<>();
        l7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("app_service", "String", Arrays.asList("=", "IN")));
        l7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("endpoint", "String", Arrays.asList("=", "IN")));
        l7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("response_status", "Int", Arrays.asList("=", "IN")));
        l7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("l7_protocol", "Int", Arrays.asList("=", "IN")));
//        l7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("trace_id", "String", Arrays.asList("=", "IN")));
        TABLE_FILTER_FIELDS_MAP.put("flow_log.l7_flow_log", l7FlowLogFields);

        // apm.traces_view
        List<FilterFieldsDTO.FilterFieldConfig> tracesViewFields = new ArrayList<>();
        tracesViewFields.add(new FilterFieldsDTO.FilterFieldConfig("app_service", "String", Arrays.asList("=", "IN")));
        tracesViewFields.add(new FilterFieldsDTO.FilterFieldConfig("root_endpoint", "String", Arrays.asList("=", "IN")));
        tracesViewFields.add(new FilterFieldsDTO.FilterFieldConfig("root_response_status", "Int", Arrays.asList("=", "IN")));
        tracesViewFields.add(new FilterFieldsDTO.FilterFieldConfig("root_l7_protocol", "Int", Arrays.asList("=", "IN")));
        tracesViewFields.add(new FilterFieldsDTO.FilterFieldConfig("has_error", "Int", Arrays.asList("=", "IN")));
        TABLE_FILTER_FIELDS_MAP.put("apm.traces_view", tracesViewFields);

        // flow_metrics.application.1m (also applies to application.1s and application.1h)
        List<FilterFieldsDTO.FilterFieldConfig> applicationFields = new ArrayList<>();
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("app_service", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("app_instance", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("region_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("az_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("host_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_cluster_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_ns_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_group_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_node_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("service_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("l3_epc_id", "String", Arrays.asList("=", "IN")));
//        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("epc_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("subnet_id", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("ip4", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("ip6", "String", Arrays.asList("=", "IN")));
        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("server_port", "String", Arrays.asList("=", "IN")));
//        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("l3_device_id", "String", Arrays.asList("=", "IN")));
//        applicationFields.add(new FilterFieldsDTO.FilterFieldConfig("l3_device_type", "String", Arrays.asList("=", "IN")));
        TABLE_FILTER_FIELDS_MAP.put("flow_metrics.application.1m", applicationFields);
        TABLE_FILTER_FIELDS_MAP.put("flow_metrics.application.1s", applicationFields);
        TABLE_FILTER_FIELDS_MAP.put("flow_metrics.application.1h", applicationFields);

        // flow_metrics.application_map.1m (also applies to application_map.1s and application_map.1h)
        List<FilterFieldsDTO.FilterFieldConfig> applicationMapFields = new ArrayList<>();
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("app_service", "app_service", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("app_instance", "app_instance", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("server_port", "server_port", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("region_id", "region_id_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("az_id", "az_id_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("host_id", "host_id_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_cluster_id", "pod_cluster_id_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_ns_id", "pod_ns_id_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_group_id", "pod_group_id_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_id", "pod_id_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_node_id", "pod_node_id_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("service_id", "service_id_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("l3_epc_id", "l3_epc_id_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("subnet_id", "subnet_id_1", "String", Arrays.asList("=", "IN")));
//        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("l3_device_id", "l3_device_id_1", "String", Arrays.asList("=", "IN")));
//        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("l3_device_type", "l3_device_type_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("ip4", "ip4_1", "String", Arrays.asList("=", "IN")));
        applicationMapFields.add(new FilterFieldsDTO.FilterFieldConfig("ip6", "ip6_1", "String", Arrays.asList("=", "IN")));
        TABLE_FILTER_FIELDS_MAP.put("flow_metrics.application_map.1m", applicationMapFields);
        TABLE_FILTER_FIELDS_MAP.put("flow_metrics.application_map.1s", applicationMapFields);
        TABLE_FILTER_FIELDS_MAP.put("flow_metrics.application_map.1h", applicationMapFields);


        // flow_log.graph_l7_flow_log
        List<FilterFieldsDTO.FilterFieldConfig> graphL7FlowLogFields = new ArrayList<>();
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("app_service", "app_service", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("app_instance", "app_instance", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("server_port", "server_port", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("region_id", "region_id_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("az_id", "az_id_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("host_id", "host_id_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_cluster_id", "pod_cluster_id_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_ns_id", "pod_ns_id_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_group_id", "pod_group_id_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_id", "pod_id_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("pod_node_id", "pod_node_id_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("service_id", "service_id_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("l3_epc_id", "l3_epc_id_1", "String", Arrays.asList("=", "IN")));
//        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("epc_id", "epc_id_1","String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("subnet_id", "subnet_id_1", "String", Arrays.asList("=", "IN")));
//        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("l3_device_id", "l3_device_id_1", "String", Arrays.asList("=", "IN")));
//        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("l3_device_type", "l3_device_type_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("ip4", "ip4_1", "String", Arrays.asList("=", "IN")));
        graphL7FlowLogFields.add(new FilterFieldsDTO.FilterFieldConfig("ip6", "ip6_1", "String", Arrays.asList("=", "IN")));
        TABLE_FILTER_FIELDS_MAP.put("flow_log.graph_l7_flow_log", graphL7FlowLogFields);
    }

    @Override
    public FieldOptionsDTO queryFieldOptions(FieldOptionQueryParam queryParam) {
        // Validate input parameters
        validateQueryParam(queryParam);

        log.info("Querying metrics with params: database={}, tableName={}, method={}, field={}, filter={}",
                queryParam.getDatabase(), queryParam.getTableName(), queryParam.getMethod(), 
                queryParam.getField(), queryParam.getFilter());

        // Get strategy based on method parameter (no if-else needed)
        FieldOptionsQueryStrategy strategy = getStrategy(queryParam.getMethod());

        // Execute query using the selected strategy
        return strategy.query(queryParam);
    }

    /**
     * Validate query parameters
     */
    private void validateQueryParam(FieldOptionQueryParam queryParam) {
        if (queryParam == null) {
            throw new IllegalArgumentException("Query parameters cannot be null");
        }
        if (!StringUtils.hasText(queryParam.getDatabase())) {
            throw new IllegalArgumentException("Database name is required");
        }
        if (!StringUtils.hasText(queryParam.getTableName())) {
            throw new IllegalArgumentException("Table name is required");
        }
        if (!StringUtils.hasText(queryParam.getField())) {
            throw new IllegalArgumentException("Field is required");
        }
    }

    /**
     * Get strategy based on method parameter
     * Returns default strategy if method is null or empty
     * 
     * No if-else needed - uses strategy factory for dispatch
     */
    private FieldOptionsQueryStrategy getStrategy(String method) {
        if (!StringUtils.hasText(method)) {
            return strategyFactory.getDefaultStrategy();
        }
        return strategyFactory.getStrategy(method);
    }

    @Override
    public FilterFieldsDTO getTableFilterFields(String database, String tableName) {
        log.info("Getting filter fields configuration for database={}, tableName={}", database, tableName);

        if (!StringUtils.hasText(database) || !StringUtils.hasText(tableName)) {
            throw new IllegalArgumentException("Database and table name are required");
        }

        String key = database + "." + tableName;
        List<FilterFieldsDTO.FilterFieldConfig> filterFields = TABLE_FILTER_FIELDS_MAP.get(key);

        if (filterFields == null) {
            log.warn("No filter fields configuration found for database={}, tableName={}", database, tableName);
            // Return empty configuration for unknown tables
            return FilterFieldsDTO.builder()
                    .database(database)
                    .tableName(tableName)
                    .filterFields(new ArrayList<>())
                    .build();
        }

        log.info("Found filter fields configuration for database={}, tableName={}, count={}", 
                database, tableName, filterFields.size());

        return FilterFieldsDTO.builder()
                .database(database)
                .tableName(tableName)
                .filterFields(filterFields)
                .build();
    }
}