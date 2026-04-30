package cn.edu.qcl.enums;

/**
 * 应用协议枚举
 * 对应数据库字段: l7_protocol (UInt8)
 */
public enum L7ProtocolEnum {
    /**
     * 未知协议
     */
    UNKNOWN(0, "未知"),

    /**
     * 其他协议
     */
    OTHER(1, "其他"),

    /**
     * HTTP/1.x 协议
     */
    HTTP1(20, "HTTP1"),

    /**
     * HTTP/2 协议
     */
    HTTP2(21, "HTTP2"),

    /**
     * Dubbo 协议
     */
    DUBBO(40, "Dubbo"),

    /**
     * MySQL 协议
     */
    MYSQL(60, "MySQL"),

    /**
     * Redis 协议
     */
    REDIS(80, "Redis"),

    /**
     * Kafka 协议
     */
    KAFKA(100, "Kafka"),

    /**
     * MQTT 协议
     */
    MQTT(101, "MQTT"),

    /**
     * DNS 协议
     */
    DNS(120, "DNS");

    private final Integer code;
    private final String description;

    L7ProtocolEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据编码获取枚举值
     * @param code 协议编码
     * @return 对应的枚举值，找不到返回null
     */
    public static L7ProtocolEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (L7ProtocolEnum protocol : L7ProtocolEnum.values()) {
            if (protocol.getCode().equals(code)) {
                return protocol;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code.toString();
    }
}
