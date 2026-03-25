package cn.edu.qcl.dto.data;

import lombok.Data;

/**
 * UserMap 数据传输对象
 * 对应 ClickHouse 表 flow_tag.user_map
 */
@Data
public class UserMapDTO {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名称
     */
    private String name;
}