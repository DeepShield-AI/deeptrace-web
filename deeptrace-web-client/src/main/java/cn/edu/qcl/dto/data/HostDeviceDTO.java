package cn.edu.qcl.dto.data;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Host Device DTO
 * Represents a host device entity for API responses
 */
@Data
@ToString
public class HostDeviceDTO {
    /**
     * Primary key, auto-increment
     */
    private Integer id;

    /**
     * Unique identifier
     */
    private String lcuuid;

    /**
     * Creation time
     */
    private Date createdAt;

    /**
     * Update time
     */
    private Date updatedAt;

    /**
     * Soft delete time
     */
    private Date deletedAt;

    /**
     * Host type (1=Server, 3=Gateway, 4=DFI)
     */
    private Integer type;

    /**
     * State (0=Temp, 1=Creating, 2=Complete...)
     */
    private Integer state;

    /**
     * Host name
     */
    private String name;

    /**
     * Alias
     */
    private String alias;

    /**
     * Description
     */
    private String description;

    /**
     * Host IP
     */
    private String ip;

    /**
     * Hostname
     */
    private String hostname;

    /**
     * Host type (1=Xen, 2=VMware, 3=KVM, 4=Public Cloud, 5=Hyper-V)
     */
    private Integer htype;

    /**
     * Creation method (0=Auto, 1=Manual)
     */
    private Integer createMethod;

    /**
     * SSH username
     */
    private String userName;

    /**
     * SSH password
     */
    private String userPasswd;

    /**
     * vCPU count
     */
    private Integer vcpuNum;

    /**
     * Total memory (MiB)
     */
    private Integer memTotal;

    /**
     * Availability zone lcuuid
     */
    private String az;

    /**
     * Region lcuuid
     */
    private String region;

    /**
     * Domain lcuuid
     */
    private String domain;

    /**
     * Sync time
     */
    private Date syncedAt;

    /**
     * Extra information
     */
    private String extraInfo;

    /**
     * Cloud platform UID
     */
    private String uid;

    /**
     * User ID
     */
    private Integer userId;
}