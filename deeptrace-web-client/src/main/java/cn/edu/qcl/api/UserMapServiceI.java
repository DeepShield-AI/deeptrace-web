package cn.edu.qcl.api;

import cn.edu.qcl.dto.data.UserMapDTO;

import java.util.List;

/**
 * UserMap 服务接口
 */
public interface UserMapServiceI {

    /**
     * 查询所有用户映射
     * @return 用户映射列表
     */
    List<UserMapDTO> listAll();

    /**
     * 根据ID查询用户映射
     * @param id 用户ID
     * @return 用户映射实体
     */
    UserMapDTO getById(Long id);

    /**
     * 根据名称模糊查询用户映射
     * @param name 用户名称
     * @return 用户映射列表
     */
    List<UserMapDTO> listByName(String name);

    /**
     * 分页查询用户映射
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 用户映射列表
     */
    List<UserMapDTO> listByPage(int page, int pageSize);

    /**
     * 统计用户映射总数
     * @return 总数
     */
    long count();
}