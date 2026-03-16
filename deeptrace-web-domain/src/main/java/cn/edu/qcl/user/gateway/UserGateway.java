package cn.edu.qcl.user.gateway;

import cn.edu.qcl.dto.data.UserDTO;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface UserGateway {
    int insert(UserDTO userDto);

    long count(UserDTO user);

    List<UserDTO> queryAllByLimit(UserDTO user, PageRequest pageRequest);

    UserDTO queryByName(String username);

    UserDTO queryById(Long userId);

    int update(UserDTO user);

    int updateLoginTime(String username);

    int deleteById(Long userId);
}
