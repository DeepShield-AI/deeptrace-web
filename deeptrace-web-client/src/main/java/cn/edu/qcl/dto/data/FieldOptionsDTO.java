package cn.edu.qcl.dto.data;

import cn.edu.qcl.annotation.MapToBeanProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Metric Query Result
 * Contains the query results and enum value mappings for ID fields
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldOptionsDTO {
    /**
     * The raw query results from ClickHouse
     */
    private List<Object> data;

    /**
     * Enum value mappings for ID fields
     * Key: field name (e.g., "l3_device_id")
     * Value: Map of ID to name mappings
     */
    private List<FieldMapping> enumMappings;


    /**
     * Field mapping for enum values
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldMapping {
        @MapToBeanProperty("id")
        private Integer id;

        @MapToBeanProperty("name")
        private String name;

        @MapToBeanProperty("type")
        private String type;
    }
}