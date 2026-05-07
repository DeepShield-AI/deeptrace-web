package cn.edu.qcl.dto.data;

import lombok.*;

import java.util.List;

/**
 * Graph Node Metrics Result
 * Contains the metrics for graph nodes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphNodeMetricsDTO extends MetricDTO{
    /**
     * Application service name
     */
    private String appService;

    /**
     * Service ID
     */
    private String serviceId;

}