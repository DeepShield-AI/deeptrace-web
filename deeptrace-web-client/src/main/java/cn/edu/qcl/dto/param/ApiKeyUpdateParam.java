package cn.edu.qcl.dto.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新API Key权限请求参数
 */
@Data
public class ApiKeyUpdateParam {
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