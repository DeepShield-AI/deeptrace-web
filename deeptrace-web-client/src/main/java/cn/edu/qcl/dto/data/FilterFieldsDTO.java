package cn.edu.qcl.dto.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 数据表中支持过滤的字段及类型
 * Table Filter Fields Configuration
 * Contains the filter fields configuration for a specific table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterFieldsDTO {
    /**
     * Database name
     */
    private String database;

    /**
     * Table name
     */
    private String tableName;

    /**
     * List of filter fields configuration
     */
    private List<FilterFieldConfig> filterFields;

    /**
     * Filter field configuration
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilterFieldConfig {
        /**
         * Node field name (for map tables, e.g., "app_service")
         */
        private String nodeField;

        /**
         * Field name
         */
        private String field;

        /**
         * Field type (e.g., "String", "Int")
         */
        private String type;

        /**
         * Supported operators (e.g., ["=", "IN"])
         */
        private List<String> operator;

        /**
         * Constructor without nodeField (for non-map tables)
         */
        public FilterFieldConfig(String field, String type, List<String> operator) {
            this.field = field;
            this.type = type;
            this.operator = operator;
        }
    }
}
