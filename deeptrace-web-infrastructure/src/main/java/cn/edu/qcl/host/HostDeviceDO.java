package cn.edu.qcl.host;

import java.io.Serializable;
import java.util.Date;

/**
 * Host Device Database Entity
 * Maps to the host_device table in MySQL
 */
public class HostDeviceDO implements Serializable {
    private static final long serialVersionUID = 1L;

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

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLcuuid() {
        return lcuuid;
    }

    public void setLcuuid(String lcuuid) {
        this.lcuuid = lcuuid;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getHtype() {
        return htype;
    }

    public void setHtype(Integer htype) {
        this.htype = htype;
    }

    public Integer getCreateMethod() {
        return createMethod;
    }

    public void setCreateMethod(Integer createMethod) {
        this.createMethod = createMethod;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPasswd() {
        return userPasswd;
    }

    public void setUserPasswd(String userPasswd) {
        this.userPasswd = userPasswd;
    }

    public Integer getVcpuNum() {
        return vcpuNum;
    }

    public void setVcpuNum(Integer vcpuNum) {
        this.vcpuNum = vcpuNum;
    }

    public Integer getMemTotal() {
        return memTotal;
    }

    public void setMemTotal(Integer memTotal) {
        this.memTotal = memTotal;
    }

    public String getAz() {
        return az;
    }

    public void setAz(String az) {
        this.az = az;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Date getSyncedAt() {
        return syncedAt;
    }

    public void setSyncedAt(Date syncedAt) {
        this.syncedAt = syncedAt;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "HostDeviceDO{" +
                "id=" + id +
                ", lcuuid='" + lcuuid + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                ", type=" + type +
                ", state=" + state +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", description='" + description + '\'' +
                ", ip='" + ip + '\'' +
                ", hostname='" + hostname + '\'' +
                ", htype=" + htype +
                ", createMethod=" + createMethod +
                ", userName='" + userName + '\'' +
                ", vcpuNum=" + vcpuNum +
                ", memTotal=" + memTotal +
                ", az='" + az + '\'' +
                ", region='" + region + '\'' +
                ", domain='" + domain + '\'' +
                ", syncedAt=" + syncedAt +
                ", uid='" + uid + '\'' +
                ", userId=" + userId +
                '}';
    }
}