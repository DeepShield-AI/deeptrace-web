package cn.edu.qcl.mapper.mysql;

import cn.edu.qcl.user.TokenDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Token表(user_token)数据库访问层
 */
public interface TokenMapper {

    /**
     * 插入Token
     *
     * @param tokenDO Token实体
     * @return 影响行数
     */
    int insert(TokenDO tokenDO);

    /**
     * 根据ID查询Token
     *
     * @param id Token ID
     * @return Token实体
     */
    TokenDO queryById(Long id);

    /**
     * 根据Token值查询Token
     *
     * @param tokenValue Token值
     * @return Token实体
     */
    TokenDO queryByTokenValue(String tokenValue);

    /**
     * 根据用户ID和Token类型查询Token列表
     *
     * @param userId    用户ID
     * @param tokenType Token类型
     * @return Token列表
     */
    List<TokenDO> queryByUserIdAndType(@Param("userId") Long userId, @Param("tokenType") String tokenType);

    /**
     * 根据用户ID查询所有有效的API Key
     *
     * @param userId 用户ID
     * @return API Key列表
     */
    List<TokenDO> queryApiKeysByUserId(Long userId);

    /**
     * 更新Token权限
     *
     * @param tokenDO Token实体
     * @return 影响行数
     */
    int updatePermission(TokenDO tokenDO);

    /**
     * 更新最后使用时间
     *
     * @param tokenValue Token值
     * @return 影响行数
     */
    int updateLastUsedTime(String tokenValue);

    /**
     * 软删除Token
     *
     * @param id     Token ID
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 统计用户的API Key数量
     *
     * @param userId 用户ID
     * @return API Key数量
     */
    long countApiKeysByUserId(Long userId);
}