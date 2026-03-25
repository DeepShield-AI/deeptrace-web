package cn.edu.qcl.enums;

import lombok.Getter;

/**
 * Token类型枚举
 * 支持多种类型的Token，通过type字段进行区分
 */
@Getter
public enum TokenTypeEnum {
    /**
     * API密钥 - 永久有效，用于API调用认证
     */
    API_KEY("API_KEY", "API密钥");

    private final String code;
    private final String description;

    TokenTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static TokenTypeEnum fromCode(String code) {
        for (TokenTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}