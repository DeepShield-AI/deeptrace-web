package cn.edu.qcl.agent;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Agent Database Entity
 * Maps to the agent_basic table in MySQL
 */
@Getter
@Setter
public class AgentDO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识 (Primary Key)
     */
    private String lcuuid;

    /**
     * 分析器IP
     */
    private String analyzerIp;

    /**
     * 架构
     */
    private String arch;

    /**
     * 架构类型
     */
    private Long archType;

    /**
     * 可用区
     */
    private String az;

    /**
     * 可用区名称
     */
    private String azName;

    /**
     * 完整版本号
     */
    private String completeRevision;

    /**
     * 控制器IP
     */
    private String controllerIp;

    /**
     * CPU数量
     */
    private Long cpuNum;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 控制IP
     */
    private String ctrlIp;

    /**
     * 控制MAC地址
     */
    private String ctrlMac;

    /**
     * 当前分析器IP
     */
    private String curAnalyzerIp;

    /**
     * 当前控制器IP
     */
    private String curControllerIp;

    /**
     * 当前K8s镜像
     */
    private String currentK8sImage;

    /**
     * 内核版本
     */
    private String kernelVersion;

    /**
     * 启动服务器
     */
    private String launchServer;

    /**
     * 许可证类型
     */
    private String licenseType;

    /**
     * 内存大小
     */
    private Float memorySize;

    /**
     * 名称
     */
    private String name;

    /**
     * 操作系统
     */
    private String os;

    /**
     * Pod集群名称
     */
    private String podClusterName;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 版本号
     */
    private String revision;

    /**
     * 状态
     */
    private String state;

    /**
     * 采集模式
     */
    private Long tapMode;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 虚拟采集组UUID
     */
    private String vtapGroupLcuuid;

    /**
     * 虚拟采集组名称
     */
    private String vtapGroupName;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 错误信息
     */
    private String errorInfo;

    /**
     * 许可证功能
     */
    private String licenseFunctions;

    /**
     * 同步分析器时间
     */
    private Date syncedAnalyzerAt;

    /**
     * 同步控制器时间
     */
    private Date syncedControllerAt;

    // Getters and Setters
    public String getLcuuid() {
        return lcuuid;
    }

    public void setLcuuid(String lcuuid) {
        this.lcuuid = lcuuid;
    }

    public String getAnalyzerIp() {
        return analyzerIp;
    }

    public void setAnalyzerIp(String analyzerIp) {
        this.analyzerIp = analyzerIp;
    }

    public String getArch() {
        return arch;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public Long getArchType() {
        return archType;
    }

    public void setArchType(Long archType) {
        this.archType = archType;
    }

    public String getAz() {
        return az;
    }

    public void setAz(String az) {
        this.az = az;
    }

    public String getAzName() {
        return azName;
    }

    public void setAzName(String azName) {
        this.azName = azName;
    }

    public String getCompleteRevision() {
        return completeRevision;
    }

    public void setCompleteRevision(String completeRevision) {
        this.completeRevision = completeRevision;
    }

    public String getControllerIp() {
        return controllerIp;
    }

    public void setControllerIp(String controllerIp) {
        this.controllerIp = controllerIp;
    }

    public Long getCpuNum() {
        return cpuNum;
    }

    public void setCpuNum(Long cpuNum) {
        this.cpuNum = cpuNum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCtrlIp() {
        return ctrlIp;
    }

    public void setCtrlIp(String ctrlIp) {
        this.ctrlIp = ctrlIp;
    }

    public String getCtrlMac() {
        return ctrlMac;
    }

    public void setCtrlMac(String ctrlMac) {
        this.ctrlMac = ctrlMac;
    }

    public String getCurAnalyzerIp() {
        return curAnalyzerIp;
    }

    public void setCurAnalyzerIp(String curAnalyzerIp) {
        this.curAnalyzerIp = curAnalyzerIp;
    }

    public String getCurControllerIp() {
        return curControllerIp;
    }

    public void setCurControllerIp(String curControllerIp) {
        this.curControllerIp = curControllerIp;
    }

    public String getCurrentK8sImage() {
        return currentK8sImage;
    }

    public void setCurrentK8sImage(String currentK8sImage) {
        this.currentK8sImage = currentK8sImage;
    }

    public String getKernelVersion() {
        return kernelVersion;
    }

    public void setKernelVersion(String kernelVersion) {
        this.kernelVersion = kernelVersion;
    }

    public String getLaunchServer() {
        return launchServer;
    }

    public void setLaunchServer(String launchServer) {
        this.launchServer = launchServer;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public Float getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(Float memorySize) {
        this.memorySize = memorySize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getPodClusterName() {
        return podClusterName;
    }

    public void setPodClusterName(String podClusterName) {
        this.podClusterName = podClusterName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getTapMode() {
        return tapMode;
    }

    public void setTapMode(Long tapMode) {
        this.tapMode = tapMode;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getVtapGroupLcuuid() {
        return vtapGroupLcuuid;
    }

    public void setVtapGroupLcuuid(String vtapGroupLcuuid) {
        this.vtapGroupLcuuid = vtapGroupLcuuid;
    }

    public String getVtapGroupName() {
        return vtapGroupName;
    }

    public void setVtapGroupName(String vtapGroupName) {
        this.vtapGroupName = vtapGroupName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getLicenseFunctions() {
        return licenseFunctions;
    }

    public void setLicenseFunctions(String licenseFunctions) {
        this.licenseFunctions = licenseFunctions;
    }

    public Date getSyncedAnalyzerAt() {
        return syncedAnalyzerAt;
    }

    public void setSyncedAnalyzerAt(Date syncedAnalyzerAt) {
        this.syncedAnalyzerAt = syncedAnalyzerAt;
    }

    public Date getSyncedControllerAt() {
        return syncedControllerAt;
    }

    public void setSyncedControllerAt(Date syncedControllerAt) {
        this.syncedControllerAt = syncedControllerAt;
    }

    @Override
    public String toString() {
        return "AgentDO{" +
                "lcuuid='" + lcuuid + '\'' +
                ", analyzerIp='" + analyzerIp + '\'' +
                ", arch='" + arch + '\'' +
                ", archType=" + archType +
                ", az='" + az + '\'' +
                ", azName='" + azName + '\'' +
                ", completeRevision='" + completeRevision + '\'' +
                ", controllerIp='" + controllerIp + '\'' +
                ", cpuNum=" + cpuNum +
                ", createTime=" + createTime +
                ", ctrlIp='" + ctrlIp + '\'' +
                ", ctrlMac='" + ctrlMac + '\'' +
                ", curAnalyzerIp='" + curAnalyzerIp + '\'' +
                ", curControllerIp='" + curControllerIp + '\'' +
                ", currentK8sImage='" + currentK8sImage + '\'' +
                ", kernelVersion='" + kernelVersion + '\'' +
                ", launchServer='" + launchServer + '\'' +
                ", licenseType='" + licenseType + '\'' +
                ", memorySize=" + memorySize +
                ", name='" + name + '\'' +
                ", os='" + os + '\'' +
                ", podClusterName='" + podClusterName + '\'' +
                ", regionName='" + regionName + '\'' +
                ", revision='" + revision + '\'' +
                ", state='" + state + '\'' +
                ", tapMode=" + tapMode +
                ", updateTime=" + updateTime +
                ", vtapGroupLcuuid='" + vtapGroupLcuuid + '\'' +
                ", vtapGroupName='" + vtapGroupName + '\'' +
                ", userId=" + userId +
                ", errorInfo='" + errorInfo + '\'' +
                ", licenseFunctions='" + licenseFunctions + '\'' +
                ", syncedAnalyzerAt=" + syncedAnalyzerAt +
                ", syncedControllerAt=" + syncedControllerAt +
                '}';
    }
}