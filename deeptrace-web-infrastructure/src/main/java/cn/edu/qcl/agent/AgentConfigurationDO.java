package cn.edu.qcl.agent;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Agent Configuration Database Entity
 * Maps to the agent_configuration table in MySQL
 */
@Getter
@Setter
public class AgentConfigurationDO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID (Primary Key)
     */
    private Long id;

    /**
     * 唯一标识
     */
    private String lcuuid;

    /**
     * Agent唯一标识
     */
    private String agentLcuuid;

    /**
     * 配置内容(YAML格式)
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

    @Override
    public String toString() {
        return "AgentConfigurationDO{" +
                "id=" + id +
                ", lcuuid='" + lcuuid + '\'' +
                ", agentLcuuid='" + agentLcuuid + '\'' +
                ", yaml='" + yaml + '\'' +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}