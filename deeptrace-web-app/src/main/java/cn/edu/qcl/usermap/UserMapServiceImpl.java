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

    @Override
    public UserMapDTO getById(Long id) {
        UserMapEntity entity = clickHouseMapper.getUserMapById(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    public List<UserMapDTO> listByName(String name) {
        List<UserMapEntity> entities = clickHouseMapper.listUserMapsByName(name);
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<UserMapDTO> listByPage(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<UserMapEntity> entities = clickHouseMapper.listUserMapsByPage(pageSize, offset);
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public long count() {
        return clickHouseMapper.countUserMaps();
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