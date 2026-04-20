package cn.edu.qcl.dto.param;

import lombok.Data;

import java.time.LocalDate;

/**
 * Span 查询参数
 * 用于从 flow_log.span 表查询 Span 数据
 */
@Data
public class SpanQueryParam {
    /**
     * 租户ID（必填）
     */
    private Long userId;

    /**
     * TraceID（必填）
     */
    private String traceId;

    /**
     * 开始时间（可选，按分区键过滤，提升性能）
     */
    private LocalDate startDate;

    /**
     * 结束时间（可选，按分区键过滤，提升性能）
     */
    private LocalDate endDate;

   /* *//**
     * 应用服务名（可选）
     *//*
    private String appService;

    *//**
     * 应用实例名（可选）
     *//*
    private String appInstance;

    *//**
     * 请求域名（可选）
     *//*
    private String requestDomain;

    *//**
     * 请求资源（可选）
     *//*
    private String requestResource;

    *//**
     * 端点（可选）
     *//*
    private String endpoint;

    *//**
     * 响应状态（可选）
     *//*
    private String responseStatus;

    *//**
     * 响应码（可选）
     *//*
    private Integer responseCode;

    *//**
     * 响应异常（可选）
     *//*
    private String responseException;

    *//**
     * 响应结果（可选）
     *//*
    private String responseResult;
*/
    /**
     * 返回条数限制，默认 100
     */
    private Integer limit = 100;

    /**
     * 排序字段，默认 time
     */
    private String orderBy = "time";

    /**
     * 排序方向，默认 DESC
     */
    private String orderDirection = "DESC";
}