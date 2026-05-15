package cn.edu.qcl.dto.data;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Agent Configuration DTO
 * Represents an agent_configuration entity for API responses
 */
@Data
@ToString
public class AgentConfigurationDTO {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 配置记录的唯一标识
     */
    private String lcuuid;

    /**
     * agent的唯一标识
     */
    private String agentLcuuid;

    /**
     * 完整的 YAML 配置文档
     */
    private String yaml;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}