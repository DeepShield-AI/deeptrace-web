package cn.edu.qcl.dto.data;

import lombok.*;

/**
 * Service Node Metrics Result
 * Contains detailed metrics and tag information for service nodes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableNodeMetricsDTO extends MetricDTO{
    /**
     * Application service name
     */
    private String appService;

    /**
     * Availability zone name
     */
    private String azName;

    /**
     * Auto service name (from device_map)
     */
    private String autoServiceName;

    /**
     * Host name (宿主机名称)
     */
    private String hostName;

    /**
     * Pod cluster name
     */
    private String podClusterName;

    /**
     * Pod namespace name
     */
    private String podNsName;

    /**
     * Pod group name
     */
    private String podGroupName;

    /**
     * Pod name
     */
    private String podName;

    /**
     * Pod node name
     */
    private String podNodeName;

    /**
     * Service name
     */
    private String serviceName;

    /**
     * L3 EPC name
     */
    private String l3EpcName;

    /**
     * Subnet name
     */
    private String subnetName;

    /**
     * L3 device name
     */
    private String l3DeviceName;

    /**
     * Auto instance name
     */
    private String autoInstanceName;

    /**
     * Whether is IPv4
     */
    private Integer isIpv4;

    /**
     * IPv4 address string
     */
    private String ip4;

    /**
     * IPv6 address string
     */
    private String ip6;

    /**
     * Server port
     */
    private Integer serverPort;

}