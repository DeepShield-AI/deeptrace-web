package cn.edu.qcl.web;

import cn.edu.qcl.api.TokenServiceI;
import cn.edu.qcl.dto.data.TokenDTO;
import cn.edu.qcl.dto.data.UserDTO;
import cn.edu.qcl.dto.param.ApiKeyCreateParam;
import cn.edu.qcl.dto.param.ApiKeyUpdateParam;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import jakarta.annotation.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.edu.qcl.utils.UserSessionUtils.getCurrentUserId;

/**
 * Token控制器
 * 提供API Key的管理接口
 */
@RestController
@RequestMapping("/api/token")
public class TokenController {

    @Resource
    private TokenServiceI tokenServiceI;

    /**
     * 创建API Key
     *
     * @param param 创建参数
     * @return Token信息（包含tokenValue，仅此一次返回）
     */
    @PostMapping("/api-key")
    public SingleResponse<TokenDTO> createApiKey(@Validated @RequestBody ApiKeyCreateParam param) {
        Long userId = getCurrentUserId();
        TokenDTO tokenDTO = tokenServiceI.createApiKey(userId, param);
        return SingleResponse.of(tokenDTO);
    }

    /**
     * 查询当前用户的API Key列表
     *
     * @return API Key列表（不包含tokenValue）
     */
    @GetMapping("/api-key/list")
    public SingleResponse<List<TokenDTO>> listApiKeys() {
        Long userId = getCurrentUserId();
        List<TokenDTO> tokens = tokenServiceI.listApiKeys(userId);
        return SingleResponse.of(tokens);
    }

    /**
     * 更新API Key权限
     *
     * @param id    Token ID
     * @param param 更新参数
     * @return 更新后的Token信息
     */
    @PutMapping("/api-key/{id}")
    public SingleResponse<TokenDTO> updateApiKeyPermission(
            @PathVariable("id") Long id,
            @Validated @RequestBody ApiKeyUpdateParam param) {
        Long userId = getCurrentUserId();
        TokenDTO tokenDTO = tokenServiceI.updateApiKeyPermission(userId, id, param);
        return SingleResponse.of(tokenDTO);
    }

    /**
     * 删除API Key（软删除，不支持撤销）
     *
     * @param id Token ID
     * @return 删除结果
     */
    @DeleteMapping("/api-key/{id}")
    public Response deleteApiKey(@PathVariable("id") Long id) {
        Long userId = getCurrentUserId();
        boolean success = tokenServiceI.deleteApiKey(userId, id);
        if (success) {
            return Response.buildSuccess();
        }
        return Response.buildFailure("DELETE_FAILED", "删除API Key失败");
    }

    /**
     * 统计当前用户的API Key数量
     *
     * @return API Key数量
     */
    @GetMapping("/api-key/count")
    public SingleResponse<Long> countApiKeys() {
        Long userId = getCurrentUserId();
        long count = tokenServiceI.countApiKeys(userId);
        return SingleResponse.of(count);
    }

    /**
     * 验证API Key（内部接口）
     * 通过请求头中的X-API-Key验证
     *
     * @param apiKey API Key值
     * @return 验证结果
     */
    @PostMapping("/verify")
    public SingleResponse<TokenDTO> verifyApiKey(@RequestHeader("X-API-Key") String apiKey) {
        TokenDTO tokenDTO = tokenServiceI.verifyApiKey(apiKey);
        if (tokenDTO == null) {
            return SingleResponse.buildFailure("INVALID_API_KEY", "API Key无效或已过期");
        }
        return SingleResponse.of(tokenDTO);
    }


}