package cn.edu.qcl.mapper.clickhouse;

import cn.edu.qcl.usermap.UserMapEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * ClickHouse Mapper 接口
 * 用于查询 ClickHouse 数据
 */
@Mapper
public interface ClickHouseMapper {


    /**
     * 执行自定义查询
     * @param sql 查询SQL
     * @return 查询结果
     */
    @Select("${sql}")
    List<Map<String, Object>> executeQuery(@Param("sql") String sql);

    // ==================== user_map 表相关查询 ====================

    /**
     * 查询所有用户映射
     * @return 用户映射列表
     */
//    @Select("SELECT `_id` as id, eth_type as name FROM flow_log.l4_flow_log limit 5")
    List<UserMapEntity> listAllUserMaps();

}