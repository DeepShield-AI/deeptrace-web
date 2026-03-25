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
        UserDO userDO = new UserDO();
        userDO.setCreateTime(new Date());
        userDO.setUpdateTime(new Date());
        BeanUtils.copyProperties(userDto, userDO);
        return userMapper.insert(userDO);
    }

    @Override
    public long count(UserDTO userDto) {
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userDto, userDO);
        userMapper.count(userDO);
        return 0;
    }

    @Override
    public List<UserDTO> queryAllByLimit(UserDTO userDto, PageRequest pageRequest) {
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userDto, userDO);
        List<UserDO> userDOS = userMapper.queryAllByLimit(userDO, pageRequest);
        if (userDOS == null) {
            return java.util.Collections.emptyList();
        }
        return userDOS.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public UserDTO queryByName(String username) {
        UserDO userDO = userMapper.queryByName(username);
        return convertToDTO(userDO);
    }

    @Override
    public UserDTO queryById(Long userId) {
        UserDO userDO = userMapper.queryById(userId);
        return convertToDTO(userDO);
    }

    @Override
    public int update(UserDTO userDto) {
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userDto, userDO);
        return userMapper.update(userDO);
    }

    @Override
    public int updateLoginTime(String username) {
        return userMapper.updateLoginTime(username);
    }

    @Override
    public int deleteById(Long userId) {
        return userMapper.deleteById(userId);
    }



    private UserDTO convertToDTO(UserDO userDO) {
        if (userDO == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userDO, userDTO);
        return userDTO;
    }
}
