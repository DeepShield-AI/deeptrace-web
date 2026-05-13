package cn.edu.qcl.dto.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Host Metric Time Series Result
 * Contains the time series metrics for a single host with different aggregation types
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HostMetricTimeSeriesDTO {
    /**
     * Time point (minute level)
     */
    private LocalDateTime minute;

    /**
     * Average value of the metric at this time point
     */
    private Double value;

}