package cn.edu.qcl.agent;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Agent User Configuration Database Entity
 * Maps to the agent_user_configuration table in MySQL
 */
@Getter
@Setter
public class AgentUserConfigurationDO implements Serializable {
    private static final long serialVersionUID = 1L;

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
     * 状态：pending-待执行 success-执行成功 failed-执行失败
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    @Override
    public String toString() {
        return "AgentUserConfigurationDO{" +
                "id=" + id +
                ", lcuuid='" + lcuuid + '\'' +
                ", agentLcuuid='" + agentLcuuid + '\'' +
                ", yaml='" + yaml + '\'' +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}