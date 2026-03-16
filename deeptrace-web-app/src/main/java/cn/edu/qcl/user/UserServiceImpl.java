package cn.edu.qcl.user;

import cn.edu.qcl.api.UserServiceI;
import cn.edu.qcl.dto.data.UserDTO;
import cn.edu.qcl.dto.param.UserParam;
import cn.edu.qcl.enums.UserRoleEnum;
import cn.edu.qcl.enums.UserStatusEnum;
import cn.edu.qcl.exception.Asserts;
import cn.edu.qcl.security.JwtTokenUtil;
import cn.edu.qcl.user.gateway.UserGateway;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * 用户个人表(User)表服务实现类
 *
 * @author makejava
 * @since 2025-11-27 14:43:07
 */
@Slf4j
@Service("userServiceI")
public class UserServiceImpl implements UserServiceI {
    @Resource
    private UserGateway userGateway;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 用户注册
     * @param userParam
     * @return
     */
    @Override
    public UserDTO register(UserParam userParam) {
        UserDTO user = new UserDTO();
        BeanUtils.copyProperties(userParam, user);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setStatus(UserStatusEnum.ENABLED.getCode());
        user.setRole(UserRoleEnum.USER.getCode());
        //查询是否有相同用户名的用户
        if (userGateway.queryByName(userParam.getUsername()) != null) {
            Asserts.fail(userParam.getUsername()+"用户名已被注册");
        }
        //将密码进行加密操作
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        userGateway.insert(user);
        return user;
    }

    @Override
    public String login(String username, String password) {
        String token = null;
        //密码需要客户端加密后传递
        try {
            UserDTO user = queryByUsername(username);
            if(!passwordEncoder.matches(password,user.getPassword())){
                Asserts.fail("密码不正确");
            }
            if(Objects.equals(user.getStatus(), UserStatusEnum.DISABLED.getCode())){
                Asserts.fail("帐号已被禁用");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            token = jwtTokenUtil.generateToken(user);
            //根据用户名修改登录时间
            userGateway.updateLoginTime(username);
        } catch (AuthenticationException e) {
            log.warn("登录异常:{}", e.getMessage());
        }catch (Exception e) {
            log.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }



    /**
     * 通过ID查询单条数据
     *
     * @param userId 主键
     * @return 实例对象
     */
    @Override
    public UserDTO queryById(Long userId) {
        return this.userGateway.queryById(userId);
    }

    @Override
    public UserDTO queryByUsername(String username){
        //获取用户信息
        UserDTO user = userGateway.queryByName(username);
        if (user != null) {
            return user;
        }
        Asserts.fail("用户不存在或用户名不正确");
        return null;
    }

    /**
     * 分页查询
     *
     * @param user 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<UserDTO> queryByPage(UserDTO user, PageRequest pageRequest) {
        long total = this.userGateway.count(user);
        return new PageImpl<>(this.userGateway.queryAllByLimit(user, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    @Override
    public UserDTO insert(UserDTO user) {
        this.userGateway.insert(user);
        return user;
    }

    /**
     * 修改数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    @Override
    public UserDTO update(UserDTO user) {
        this.userGateway.update(user);
        return this.queryById(user.getUserId());
    }

    /**
     * 通过主键删除数据
     *
     * @param userId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long userId) {
        return this.userGateway.deleteById(userId) > 0;
    }

}
