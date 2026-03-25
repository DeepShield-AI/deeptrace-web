package cn.edu.qcl.token;

import cn.edu.qcl.api.TokenServiceI;
import cn.edu.qcl.dto.data.TokenDTO;
import cn.edu.qcl.dto.param.ApiKeyCreateParam;
import cn.edu.qcl.dto.param.ApiKeyUpdateParam;
import cn.edu.qcl.enums.TokenTypeEnum;
import cn.edu.qcl.exception.Asserts;
import cn.edu.qcl.user.entity.TokenEntity;
import cn.edu.qcl.user.gateway.TokenGateway;
import cn.edu.qcl.utils.ApiKeyGenerator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Token服务实现类
 */
@Slf4j
@Service("tokenServiceI")
public class TokenServiceImpl implements TokenServiceI {

    @Resource
    private TokenGateway tokenGateway;

    @Autowired
    private ApiKeyGenerator apiKeyGenerator;

    /**
     * API Key数量限制
     */
    @Value("${api.key.max-count:10}")
    private int maxApiKeyCount;

    @Override
    public TokenDTO createApiKey(Long userId, ApiKeyCreateParam param) {
        // 检查API Key数量限制
        long count = tokenGateway.countApiKeysByUserId(userId);
        if (count >= maxApiKeyCount) {
            Asserts.fail("API Key数量已达上限，最多允许创建" + maxApiKeyCount + "个");
        }

        // 生成API Key
        String tokenValue = apiKeyGenerator.generateApiKey();
        
        // 创建Token实体
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setUserId(userId);
        tokenEntity.setTokenType(TokenTypeEnum.API_KEY);
        tokenEntity.setTokenValue(tokenValue);
        tokenEntity.setTokenName(param.getTokenName());
        tokenEntity.setReadPermission(param.getReadPermission());
        tokenEntity.setWritePermission(param.getWritePermission());
        tokenEntity.setStatus(1);
        // API Key永久有效，expireTime为null

        // 保存到数据库
        tokenGateway.insert(tokenEntity);

        log.info("创建API Key成功，userId: {}, tokenId: {}", userId, tokenEntity.getId());

        return convertToDTO(tokenEntity);
    }

    @Override
    public List<TokenDTO> listApiKeys(Long userId) {
        List<TokenEntity> entities = tokenGateway.queryApiKeysByUserId(userId);
        return entities.stream()
                .map(this::convertToDTOWithoutValue)
                .collect(Collectors.toList());
    }

    @Override
    public TokenDTO updateApiKeyPermission(Long userId, Long id, ApiKeyUpdateParam param) {
        // 查询Token
        TokenEntity entity = tokenGateway.queryById(id);
        if (entity == null) {
            Asserts.fail("API Key不存在");
        }
        
        // 校验所属用户
        if (!entity.getUserId().equals(userId)) {
            Asserts.fail("无权操作此API Key");
        }

        // 更新权限
        entity.setReadPermission(param.getReadPermission());
        entity.setWritePermission(param.getWritePermission());
        tokenGateway.updatePermission(entity);

        log.info("更新API Key权限成功，userId: {}, tokenId: {}", userId, id);

        return convertToDTOWithoutValue(entity);
    }

    @Override
    public boolean deleteApiKey(Long userId, Long id) {
        // 查询Token
        TokenEntity entity = tokenGateway.queryById(id);
        if (entity == null) {
            Asserts.fail("API Key不存在");
        }

        // 校验所属用户
        if (!entity.getUserId().equals(userId)) {
            Asserts.fail("无权操作此API Key");
        }

        // 软删除（删除后无法恢复，不支持撤销）
        int result = tokenGateway.deleteById(id, userId);
        
        if (result > 0) {
            log.info("删除API Key成功，userId: {}, tokenId: {}", userId, id);
            return true;
        }
        return false;
    }

    @Override
    public TokenDTO verifyApiKey(String tokenValue) {
        if (tokenValue == null || tokenValue.isEmpty()) {
            return null;
        }

        // 查询Token
        TokenEntity entity = tokenGateway.queryByTokenValue(tokenValue);
        if (entity == null) {
            return null;
        }

        // 检查Token类型
        if (entity.getTokenType() != TokenTypeEnum.API_KEY) {
            return null;
        }

        // 检查Token是否有效
        if (!entity.isValid()) {
            return null;
        }

        // 更新最后使用时间（异步更新，不影响性能）
        try {
            tokenGateway.updateLastUsedTime(tokenValue);
        } catch (Exception e) {
            log.warn("更新API Key最后使用时间失败: {}", e.getMessage());
        }

        return convertToDTO(entity);
    }

    @Override
    public long countApiKeys(Long userId) {
        return tokenGateway.countApiKeysByUserId(userId);
    }

    /**
     * 转换为DTO（包含tokenValue，仅在创建时使用）
     */
    private TokenDTO convertToDTO(TokenEntity entity) {
        if (entity == null) {
            return null;
        }
        TokenDTO dto = new TokenDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getTokenType() != null) {
            dto.setTokenType(entity.getTokenType().getCode());
        }
        return dto;
    }

    /**
     * 转换为DTO（不包含tokenValue，用于列表展示）
     */
    private TokenDTO convertToDTOWithoutValue(TokenEntity entity) {
        if (entity == null) {
            return null;
        }
        TokenDTO dto = new TokenDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getTokenType() != null) {
            dto.setTokenType(entity.getTokenType().getCode());
        }
        // 不返回tokenValue，安全考虑
        dto.setTokenValue(null);
        return dto;
    }
}