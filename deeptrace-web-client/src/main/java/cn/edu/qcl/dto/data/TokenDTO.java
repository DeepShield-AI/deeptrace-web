package cn.edu.qcl.dto.data;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Token数据传输对象
 */
@Data
@ToString(exclude = "tokenValue")  // 不打印tokenValue，避免日志泄露
public class TokenDTO {
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
    private String tokenType;

    /**
     * Token值（仅在创建时返回，之后不再返回）
     */
    private String tokenValue;

    /**
     * Token名称
     */
    private String tokenName;

    /**
     * 读权限
     */
    private Boolean readPermission;

    /**
     * 写权限
     */
    private Boolean writePermission;

    /**
     * 状态：0-已删除, 1-正常
     */
    private Integer status;

    /**
     * 过期时间
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