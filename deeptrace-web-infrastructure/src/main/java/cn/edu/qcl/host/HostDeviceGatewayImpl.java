package cn.edu.qcl.host;

import cn.edu.qcl.dto.data.HostDeviceDTO;
import cn.edu.qcl.dto.param.HostDevicePageQuery;
import cn.edu.qcl.host.gateway.HostDeviceGateway;
import cn.edu.qcl.mapper.mysql.HostDeviceMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Host Device Gateway Implementation
 * Implements data access operations for host device
 */
@Repository
public class HostDeviceGatewayImpl implements HostDeviceGateway {

    @Resource
    private HostDeviceMapper hostDeviceMapper;

    @Override
    public List<HostDeviceDTO> queryByPage(HostDevicePageQuery query) {
        HostDeviceDO hostDeviceDO = new HostDeviceDO();
        hostDeviceDO.setUserId(query.getUserId());
        hostDeviceDO.setName(query.getName());
        hostDeviceDO.setAlias(query.getAlias());
        hostDeviceDO.setIp(query.getIp());

        PageRequest pageRequest = PageRequest.of(query.getPageIndex(), query.getPageSize());
        List<HostDeviceDO> hostDeviceDOList = hostDeviceMapper.queryByPage(hostDeviceDO, pageRequest);

        if (hostDeviceDOList == null || hostDeviceDOList.isEmpty()) {
            return Collections.emptyList();
        }

        return hostDeviceDOList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long count(HostDevicePageQuery query) {
        HostDeviceDO hostDeviceDO = new HostDeviceDO();
        hostDeviceDO.setUserId(query.getUserId());
        hostDeviceDO.setName(query.getName());
        hostDeviceDO.setAlias(query.getAlias());
        hostDeviceDO.setIp(query.getIp());

        return hostDeviceMapper.count(hostDeviceDO);
    }

    /**
     * Convert DO to DTO
     *
     * @param hostDeviceDO database entity
     * @return DTO object
     */
    private HostDeviceDTO convertToDTO(HostDeviceDO hostDeviceDO) {
        if (hostDeviceDO == null) {
            return null;
        }
        HostDeviceDTO dto = new HostDeviceDTO();
        BeanUtils.copyProperties(hostDeviceDO, dto);
        return dto;
    }
}