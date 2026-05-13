package cn.edu.qcl.dto.param;

import lombok.Data;

/**
 * Span明细查询参数
 * 根据traceId和时间范围查询span明细列表
 */
@Data
public class SingleTraceDetailQueryParam {
    /**
     * 团队ID，用于多租户数据隔离
     */
    private Long teamId;

    /**
     * Trace ID
     */
    private String traceId;

    /**
     * 开始时间（秒级时间戳）
     */
    private Long startTime;

    /**
     * 结束时间（秒级时间戳）
     */
    private Long endTime;
}