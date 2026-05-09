package cn.edu.qcl.dto.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Node Time Series Result
 * Contains the time series metrics for a single node
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeriesDTO {
    /**
     * Time point (minute level)
     */
    private LocalDateTime minute;

    /**
     * Total requests at this time point
     */
    private Long totalRequests;

    /**
     * Error requests at this time point
     */
    private Long errorRequests;

    /**
     * Average latency in seconds
     */
    private Double avgLatencySeconds;

    /**
     * P50 latency in seconds
     */
    private Double p50LatencySeconds;

    /**
     * P75 latency in seconds
     */
    private Double p75LatencySeconds;

    /**
     * P99 latency in seconds
     */
    private Double p99LatencySeconds;
}