package cn.edu.qcl.dto.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 指标
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricDTO {
    /**
     * Total number of requests
     */
    private Long totalRequests;

    /**
     * Total number of errors
     */
    private Long totalErrors;

    /**
     * Total number of responses
     */
    private Long totalResponses;

    /**
     * Error rate (0-1, e.g., 0.0123 = 1.23%)
     */
    private Double errorRate;

    /**
     * Average RTT in microseconds
     */
    private Double avgRttMicroseconds;

    /**
     * Average RTT in milliseconds (3 decimal places)
     */
    private Double avgRttMs;
}
