package cn.edu.qcl.user;

import cn.edu.qcl.dto.data.UserDTO;
import cn.edu.qcl.mapper.mysql.UserMapper;
import cn.edu.qcl.user.gateway.UserGateway;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class UserGatewayImpl implements UserGateway {

    @Resource
    private UserMapper userMapper;

    @Override
    public int insert(UserDTO userDto) {
        User user = new User();
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        BeanUtils.copyProperties(userDto, user);
        return userMapper.insert(user);
    }

    @Override
    public long count(UserDTO userDto) {
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        userMapper.count(user);
        return 0;
    }

    @Override
    public List<UserDTO> queryAllByLimit(UserDTO userDto, PageRequest pageRequest) {
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        List<User> users = userMapper.queryAllByLimit(user, pageRequest);
        if (users == null) {
            return java.util.Collections.emptyList();
        }
        return users.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public UserDTO queryByName(String username) {
        User user = userMapper.queryByName(username);
        return convertToDTO( user);
    }

    @Override
    public UserDTO queryById(Long userId) {
        User user = userMapper.queryById(userId);
        return convertToDTO( user);
    }

    @Override
    public int update(UserDTO userDto) {
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        return userMapper.update(user);
    }

    @Override
    public int updateLoginTime(String username) {
        return userMapper.updateLoginTime(username);
    }

    @Override
    public int deleteById(Long userId) {
        return userMapper.deleteById(userId);
    }



    private UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }
}
