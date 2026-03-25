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
     * 测试连接 - 查询当前数据库
     */
    @Select("SELECT currentDatabase()")
    String getCurrentDatabase();

    /**
     * 查询所有表
     */
    @Select("SELECT name, engine, total_rows, total_bytes FROM system.tables WHERE database = currentDatabase()")
    List<Map<String, Object>> listTables();

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
    @Select("SELECT `_id` as id, eth_type as name FROM flow_log.l4_flow_log limit 5")
    List<UserMapEntity> listAllUserMaps();

    /**
     * 根据ID查询用户映射
     * @param id 用户ID
     * @return 用户映射实体
     */
    @Select("SELECT id, name FROM user_map WHERE id = #{id}")
    UserMapEntity getUserMapById(@Param("id") Long id);

    /**
     * 根据名称模糊查询用户映射
     * @param name 用户名称
     * @return 用户映射列表
     */
    @Select("SELECT id, name FROM user_map WHERE name LIKE concat('%', #{name}, '%')")
    List<UserMapEntity> listUserMapsByName(@Param("name") String name);

    /**
     * 分页查询用户映射
     * @param limit 每页数量
     * @param offset 偏移量
     * @return 用户映射列表
     */
    @Select("SELECT id, name FROM user_map ORDER BY id LIMIT #{limit} OFFSET #{offset}")
    List<UserMapEntity> listUserMapsByPage(@Param("limit") int limit, @Param("offset") int offset);

    /**
     * 统计用户映射总数
     * @return 总数
     */
    @Select("SELECT count(*) FROM user_map")
    long countUserMaps();
}