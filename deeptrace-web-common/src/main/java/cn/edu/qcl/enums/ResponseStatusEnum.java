package cn.edu.qcl.enums;

/**
 * 响应状态枚举
 * 对应数据库字段: response_status (UInt8)
 */
public enum ResponseStatusEnum {
    /**
     * 正常响应
     */
    NORMAL(0, "正常"),

    /**
     * 异常响应
     */
    ERROR(1, "异常"),

    /**
     * 资源不存在
     */
    NOT_FOUND(2, "不存在"),

    /**
     * 服务端异常
     */
    SERVER_ERROR(3, "服务端异常"),

    /**
     * 客户端异常
     */
    CLIENT_ERROR(4, "客户端异常");

    private final Integer code;
    private final String description;

    ResponseStatusEnum(Integer code, String description) {
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
     * @param code 状态编码
     * @return 对应的枚举值，找不到返回null
     */
    public static ResponseStatusEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ResponseStatusEnum status : ResponseStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code.toString();
    }
}
