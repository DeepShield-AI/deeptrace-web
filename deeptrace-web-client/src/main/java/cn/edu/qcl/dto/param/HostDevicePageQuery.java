package cn.edu.qcl.dto.param;

import com.alibaba.cola.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Host Device Page Query Parameter
 * Used for paginated queries with filtering by user_id, name, alias, ip
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class HostDevicePageQuery extends PageQuery {
    /**
     * User ID filter
     */
    private Integer userId;

    /**
     * Host name filter (fuzzy match)
     */
    private String name;

    /**
     * Alias filter (fuzzy match)
     */
    private String alias;

    /**
     * IP filter (fuzzy match)
     */
    private String ip;
}