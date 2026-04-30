package cn.edu.qcl.usermap;

import cn.edu.qcl.api.UserMapServiceI;
import cn.edu.qcl.dto.data.UserMapDTO;
import cn.edu.qcl.mapper.clickhouse.ClickHouseMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserMap 服务实现
 */
@Service
public class UserMapServiceImpl implements UserMapServiceI {

    @Autowired
    private ClickHouseMapper clickHouseMapper;

    @Override
    public List<UserMapDTO> listAll() {
        List<UserMapEntity> entities = clickHouseMapper.listAllUserMaps();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
    /**
     * 实体转DTO
     */
    private UserMapDTO toDTO(UserMapEntity entity) {
        UserMapDTO dto = new UserMapDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}