package cn.edu.qcl.mapper.mysql;

import cn.edu.qcl.host.HostDeviceDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Host Device Mapper Interface
 * Database access layer for host_device table
 */
public interface HostDeviceMapper {

    /**
     * Query host devices with pagination and filtering
     *
     * @param hostDeviceDO filter conditions
     * @param pageable pagination parameters
     * @return list of host devices
     */
    List<HostDeviceDO> queryByPage(HostDeviceDO hostDeviceDO, @Param("pageable") Pageable pageable);

    /**
     * Count total rows with filter conditions
     *
     * @param hostDeviceDO filter conditions
     * @return total count
     */
    long count(HostDeviceDO hostDeviceDO);

    /**
     * Query by ID
     *
     * @param id primary key
     * @return host device entity
     */
    HostDeviceDO queryById(Integer id);
}