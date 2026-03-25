package cn.edu.qcl.api;

import cn.edu.qcl.dto.data.TokenDTO;
import cn.edu.qcl.dto.param.ApiKeyCreateParam;
import cn.edu.qcl.dto.param.ApiKeyUpdateParam;

import java.util.List;

/**
 * Token服务接口
 */
public interface TokenServiceI {

    /**
     * 创建API Key
     *
     * @param userId 用户ID
     * @param param  创建参数
     * @return Token信息（包含tokenValue，仅此一次返回）
     */
    TokenDTO createApiKey(Long userId, ApiKeyCreateParam param);

    /**
     * 查询用户的API Key列表
     *
     * @param userId 用户ID
     * @return API Key列表（不包含tokenValue）
     */
    List<TokenDTO> listApiKeys(Long userId);

    /**
     * 更新API Key权限
     *
     * @param userId 用户ID
     * @param id     Token ID
     * @param param  更新参数
     * @return 更新后的Token信息
     */
    TokenDTO updateApiKeyPermission(Long userId, Long id, ApiKeyUpdateParam param);

    /**
     * 删除API Key（软删除，不支持撤销）
     *
     * @param userId 用户ID
     * @param id     Token ID
     * @return 是否删除成功
     */
    boolean deleteApiKey(Long userId, Long id);

    /**
     * 根据Token值验证并获取用户信息
     *
     * @param tokenValue Token值
     * @return Token信息（包含userId和权限）
     */
    TokenDTO verifyApiKey(String tokenValue);

    /**
     * 统计用户的API Key数量
     *
     * @param userId 用户ID
     * @return API Key数量
     */
    long countApiKeys(Long userId);
}