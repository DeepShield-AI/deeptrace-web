package cn.edu.qcl.dto.data;

import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Data
@ToString
public class UserDTO {
    /**
     * 用户ID，主键，自增
     */
    private Long userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 电话号码
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 帐号启用状态：0->禁用；1->启用
     */
    private Integer status;
    /**
     * 用户角色 admin：管理员  user：普通用户
     */
    private String role;
    /**
     * 备注信息
     */
    private String note;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 最后登录时间
     */
    private Date loginTime;
    //用户的所有权限，设置为固定值ROLE_+当前用户角色（USER ADMIN）
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 根据用户角色返回相应的权限
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role.toUpperCase()));
    }
}
