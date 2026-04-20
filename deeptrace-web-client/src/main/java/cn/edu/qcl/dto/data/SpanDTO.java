package cn.edu.qcl.dto.data;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Span 数据传输对象
 * 对应 flow_log.span 表
 */
@Data
@ToString
public class SpanDTO {
    /**
     * 时间戳
     */
    private LocalDateTime time;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 响应耗时（纳秒）
     */
    private Long responseDuration;

    /**
     * 应用服务名
     */
    private String appService;

    /**
     * 应用实例名
     */
    private String appInstance;

    /**
     * 请求域名
     */
    private String requestDomain;

    /**
     * 请求资源
     */
    private String requestResource;

    /**
     * 端点
     */
    private String endpoint;

    /**
     * 响应状态
     */
    private String responseStatus;

    /**
     * 响应码
     */
    private Integer responseCode;

    /**
     * 响应异常
     */
    private String responseException;

    /**
     * 响应结果
     */
    private String responseResult;

    /**
     * 请求长度
     */
    private Long requestLength;

    /**
     * 响应长度
     */
    private Long responseLength;

    /**
     * IPv4 地址第一部分
     */
    private Long ip4_0;

    /**
     * IPv4 地址第二部分
     */
    private Long ip4_1;

    /**
     * IPv6 地址第一部分
     */
    private String ip6_0;

    /**
     * IPv6 地址第二部分
     */
    private String ip6_1;

    /**
     * 客户端端口
     */
    private Integer clientPort;

    /**
     * 服务端端口
     */
    private Integer serverPort;

    /**
     * 进程ID第一部分
     */
    private Long processId_0;

    /**
     * 进程ID第二部分
     */
    private Long processId_1;

    /**
     * 属性名称列表
     */
    private List<String> attributeNames;

    /**
     * 属性值列表
     */
    private List<String> attributeValues;
}