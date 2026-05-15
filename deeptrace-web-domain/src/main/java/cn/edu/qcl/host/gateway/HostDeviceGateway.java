package cn.edu.qcl.host.gateway;

import cn.edu.qcl.dto.data.HostDeviceDTO;
import cn.edu.qcl.dto.param.HostDevicePageQuery;

import java.util.List;

/**
 * Host Device Gateway Interface
 * Defines the data access operations for host device
 */
public interface HostDeviceGateway {

    /**
     * Query host devices with pagination and filtering
     *
     * @param query query parameters including pagination and filters
     * @return list of host devices
     */
    List<HostDeviceDTO> queryByPage(HostDevicePageQuery query);

    /**
     * Count total host devices with filter conditions
     *
     * @param query filter conditions
     * @return total count
     */
    long count(HostDevicePageQuery query);
}