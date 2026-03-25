package cn.edu.qcl.user.entity;

import cn.edu.qcl.enums.TokenTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Token实体类
 * 支持多种类型的Token，包括API Key、JWT刷新令牌、访问令牌等
 */
@Data
public class TokenEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * Token类型
     */
    private TokenTypeEnum tokenType;

    /**
     * Token值
     */
    private String tokenValue;

    /**
     * Token名称，用于标识用途
     */
    private String tokenName;

    /**
     * 读权限：true-有读权限, false-无读权限
     */
    private Boolean readPermission;

    /**
     * 写权限：true-有写权限, false-无写权限
     */
    private Boolean writePermission;

    /**
     * 状态：0-已删除, 1-正常
     */
    private Integer status;

    /**
     * 过期时间，NULL表示永久有效
     */
    private Date expireTime;

    /**
     * 最后使用时间
     */
    private Date lastUsedTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除时间
     */
    private Date deletedAt;

    /**
     * 判断Token是否有效
     */
    public boolean isValid() {
        // 检查状态
        if (status == null || status != 1) {
            return false;
        }
        // 检查过期时间（null表示永久有效）
        if (expireTime != null && expireTime.before(new Date())) {
            return false;
        }
        return true;
    }

    /**
     * 判断Token是否永久有效
     */
    public boolean isPermanent() {
        return expireTime == null;
    }

    /**
     * 判断是否有读权限
     */
    public boolean hasReadPermission() {
        return Boolean.TRUE.equals(readPermission);
    }

    /**
     * 判断是否有写权限
     */
    public boolean hasWritePermission() {
        return Boolean.TRUE.equals(writePermission);
    }
}