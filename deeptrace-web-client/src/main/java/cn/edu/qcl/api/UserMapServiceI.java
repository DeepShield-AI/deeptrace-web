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


}