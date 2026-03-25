package cn.edu.qcl.dto.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建API Key请求参数
 */
@Data
public class ApiKeyCreateParam {
    /**
     * Token名称，用于标识用途
     */
    @NotBlank(message = "Token名称不能为空")
    private String tokenName;

    /**
     * 读权限
     */
    @NotNull(message = "读权限不能为空")
    private Boolean readPermission;

    /**
     * 写权限
     */
    @NotNull(message = "写权限不能为空")
    private Boolean writePermission;
}