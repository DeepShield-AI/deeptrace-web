package cn.edu.qcl.usermap;

import lombok.Data;

/**
 * UserMap 实体类
 * 对应 ClickHouse 表 flow_tag.user_map
 */
@Data
public class UserMapEntity {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名称
     */
    private String name;
}