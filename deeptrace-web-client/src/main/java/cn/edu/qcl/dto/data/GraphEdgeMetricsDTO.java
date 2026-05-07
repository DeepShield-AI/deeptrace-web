package cn.edu.qcl.dto.data;

import lombok.*;

/**
 * Graph Edge Metrics Result
 * Contains metrics for edges in the topology graph
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphEdgeMetricsDTO extends MetricDTO{
    /**
     * Source node ID
     */
    private String srcNodeId;

    /**
     * Destination node ID
     */
    private String destNodeId;
}