package cn.edu.qcl.dto.data;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Agent DTO
 * Represents an agent_basic entity for API responses
 */
@Data
@ToString
public class AgentDTO {
    /**
     * 唯一标识 (Primary Key)
     */
    private String lcuuid;

    /**
     * 分析器IP
     */
    private String analyzerIp;

    /**
     * 架构
     */
    private String arch;

    /**
     * 架构类型
     */
    private Long archType;

    /**
     * 可用区
     */
    private String az;

    /**
     * 可用区名称
     */
    private String azName;

    /**
     * 完整版本号
     */
    private String completeRevision;

    /**
     * 控制器IP
     */
    private String controllerIp;

    /**
     * CPU数量
     */
    private Long cpuNum;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 控制IP
     */
    private String ctrlIp;

    /**
     * 控制MAC地址
     */
    private String ctrlMac;

    /**
     * 当前分析器IP
     */
    private String curAnalyzerIp;

    /**
     * 当前控制器IP
     */
    private String curControllerIp;

    /**
     * 当前K8s镜像
     */
    private String currentK8sImage;

    /**
     * 内核版本
     */
    private String kernelVersion;

    /**
     * 启动服务器
     */
    private String launchServer;

    /**
     * 许可证类型
     */
    private String licenseType;

    /**
     * 内存大小
     */
    private Float memorySize;

    /**
     * 名称
     */
    private String name;

    /**
     * 操作系统
     */
    private String os;

    /**
     * Pod集群名称
     */
    private String podClusterName;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 版本号
     */
    private String revision;

    /**
     * 状态
     */
    private String state;

    /**
     * 采集模式
     */
    private Long tapMode;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 虚拟采集组UUID
     */
    private String vtapGroupLcuuid;

    /**
     * 虚拟采集组名称
     */
    private String vtapGroupName;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 错误信息
     */
    private String errorInfo;

    /**
     * 许可证功能
     */
    private String licenseFunctions;

    /**
     * 同步分析器时间
     */
    private Date syncedAnalyzerAt;

    /**
     * 同步控制器时间
     */
    private Date syncedControllerAt;
}