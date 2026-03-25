package cn.edu.qcl.user.gateway;

import cn.edu.qcl.enums.TokenTypeEnum;
import cn.edu.qcl.user.entity.TokenEntity;

import java.util.List;

/**
 * Token网关接口
 * 定义Token相关的数据访问操作
 */
public interface TokenGateway {

    /**
     * 插入Token
     *
     * @param tokenEntity Token实体
     * @return 影响行数
     */
    int insert(TokenEntity tokenEntity);

    /**
     * 根据ID查询Token
     *
     * @param id Token ID
     * @return Token实体
     */
    TokenEntity queryById(Long id);

    /**
     * 根据Token值查询Token
     *
     * @param tokenValue Token值
     * @return Token实体
     */
    TokenEntity queryByTokenValue(String tokenValue);

    /**
     * 根据用户ID和Token类型查询Token列表
     *
     * @param userId    用户ID
     * @param tokenType Token类型
     * @return Token列表
     */
    List<TokenEntity> queryByUserIdAndType(Long userId, TokenTypeEnum tokenType);

    /**
     * 根据用户ID查询所有有效的API Key
     *
     * @param userId 用户ID
     * @return API Key列表
     */
    List<TokenEntity> queryApiKeysByUserId(Long userId);

    /**
     * 更新Token权限
     *
     * @param tokenEntity Token实体
     * @return 影响行数
     */
    int updatePermission(TokenEntity tokenEntity);

    /**
     * 更新最后使用时间
     *
     * @param tokenValue Token值
     * @return 影响行数
     */
    int updateLastUsedTime(String tokenValue);

    /**
     * 软删除Token（删除后无法恢复，不支持撤销）
     *
     * @param id     Token ID
     * @param userId 用户ID（用于权限校验）
     * @return 影响行数
     */
    int deleteById(Long id, Long userId);

    /**
     * 统计用户的API Key数量
     *
     * @param userId 用户ID
     * @return API Key数量
     */
    long countApiKeysByUserId(Long userId);
}