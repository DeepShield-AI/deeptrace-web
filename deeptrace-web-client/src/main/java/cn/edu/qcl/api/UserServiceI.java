package cn.edu.qcl.api;

import cn.edu.qcl.dto.data.UserDTO;
import cn.edu.qcl.dto.param.UserParam;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


/**
 * 用户个人表(User)表服务接口
 *
 * @author makejava
 * @since 2025-11-27 14:43:07
 */
public interface UserServiceI {

    /**
     * 通过ID查询单条数据
     *
     * @param userId 主键
     * @return 实例对象
     */
    UserDTO queryById(Long userId);

    UserDTO queryByUsername(String username);

    /**
     * 分页查询
     *
     * @param user 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    Page<UserDTO> queryByPage(UserDTO user, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    UserDTO insert(UserDTO user);

    /**
     * 修改数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    UserDTO update(UserDTO user);

    /**
     * 通过主键删除数据
     *
     * @param userId 主键
     * @return 是否成功
     */
    boolean deleteById(Long userId);

    /**
     * 注册功能
     * @param userParam
     * @return
     */
    UserDTO register(UserParam userParam);

    /**
     * 登录功能
     * @param username 用户名
     * @param password 密码
     * @return 生成的JWT的token
     */
    String login(/*@NotEmpty*/ String username, /*@NotEmpty*/ String password);
}
