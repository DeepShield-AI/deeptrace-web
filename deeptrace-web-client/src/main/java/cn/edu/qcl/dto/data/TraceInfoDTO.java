package cn.edu.qcl.dto.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Trace Info Data Transfer Object
 * Contains trace summary information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraceInfoDTO {
    /**
     * Team ID
     */
    private Long teamId;

    /**
     * Trace ID
     */
    private String traceId;

    /**
     * Start time
     */
    private LocalDateTime startTime;

    /**
     * End time
     */
    private LocalDateTime endTime;

    /**
     * Duration in microseconds
     */
    private Long durationUs;

    /**
     * Root application service name
     */
    private String rootAppService;

    /**
     * Root request domain
     */
    private String rootRequestDomain;

    /**
     * Root request resource
     */
    private String rootRequestResource;

    /**
     * Root endpoint
     */
    private String rootEndpoint;

    /**
     * Root response status
     */
    private Integer rootResponseStatus;

    /**
     * Root L7 protocol
     */
    private Integer rootL7Protocol;

    /**
     * Root biz type
     */
    private Integer rootBizType;

    /**
     * Span count
     */
    private Integer spanCount;

    /**
     * Error span count
     */
    private Integer errorSpanCount;

    /**
     * Has error flag
     */
    private Integer hasError;

    /**
     * Application services involved in this trace
     */
    private String appServices;
}