package cn.edu.qcl.user;

import cn.edu.qcl.enums.TokenTypeEnum;
import cn.edu.qcl.mapper.mysql.TokenMapper;
import cn.edu.qcl.user.entity.TokenEntity;
import cn.edu.qcl.user.gateway.TokenGateway;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Token网关实现类
 */
@Repository
public class TokenGatewayImpl implements TokenGateway {

    @Resource
    private TokenMapper tokenMapper;

    @Override
    public int insert(TokenEntity tokenEntity) {
        TokenDO tokenDO = convertToDO(tokenEntity);
        return tokenMapper.insert(tokenDO);
    }

    @Override
    public TokenEntity queryById(Long id) {
        TokenDO tokenDO = tokenMapper.queryById(id);
        return convertToEntity(tokenDO);
    }

    @Override
    public TokenEntity queryByTokenValue(String tokenValue) {
        TokenDO tokenDO = tokenMapper.queryByTokenValue(tokenValue);
        return convertToEntity(tokenDO);
    }

    @Override
    public List<TokenEntity> queryByUserIdAndType(Long userId, TokenTypeEnum tokenType) {
        List<TokenDO> tokenDOs = tokenMapper.queryByUserIdAndType(userId, tokenType.getCode());
        return tokenDOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<TokenEntity> queryApiKeysByUserId(Long userId) {
        List<TokenDO> tokenDOs = tokenMapper.queryApiKeysByUserId(userId);
        return tokenDOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public int updatePermission(TokenEntity tokenEntity) {
        TokenDO tokenDO = new TokenDO();
        tokenDO.setId(tokenEntity.getId());
        tokenDO.setUserId(tokenEntity.getUserId());
        tokenDO.setReadPermission(tokenEntity.getReadPermission());
        tokenDO.setWritePermission(tokenEntity.getWritePermission());
        return tokenMapper.updatePermission(tokenDO);
    }

    @Override
    public int updateLastUsedTime(String tokenValue) {
        return tokenMapper.updateLastUsedTime(tokenValue);
    }

    @Override
    public int deleteById(Long id, Long userId) {
        return tokenMapper.deleteByIdAndUserId(id, userId);
    }

    @Override
    public long countApiKeysByUserId(Long userId) {
        return tokenMapper.countApiKeysByUserId(userId);
    }

    /**
     * 将实体转换为DO
     */
    private TokenDO convertToDO(TokenEntity entity) {
        if (entity == null) {
            return null;
        }
        TokenDO tokenDO = new TokenDO();
        BeanUtils.copyProperties(entity, tokenDO);
        if (entity.getTokenType() != null) {
            tokenDO.setTokenType(entity.getTokenType().getCode());
        }
        return tokenDO;
    }

    /**
     * 将DO转换为实体
     */
    private TokenEntity convertToEntity(TokenDO tokenDO) {
        if (tokenDO == null) {
            return null;
        }
        TokenEntity entity = new TokenEntity();
        BeanUtils.copyProperties(tokenDO, entity);
        entity.setTokenType(TokenTypeEnum.fromCode(tokenDO.getTokenType()));
        return entity;
    }
}