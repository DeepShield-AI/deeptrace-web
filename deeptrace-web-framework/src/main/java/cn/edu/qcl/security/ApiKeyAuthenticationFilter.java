package cn.edu.qcl.security;

import cn.edu.qcl.api.TokenServiceI;
import cn.edu.qcl.dto.data.TokenDTO;
import cn.edu.qcl.dto.data.UserDTO;
import cn.edu.qcl.utils.ApiKeyGenerator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * API Key认证过滤器
 * 拦截带有X-API-Key请求头的请求，进行API Key认证
 *
 * 请求示例:
 * GET /api/data HTTP/1.1
 * Host: localhost:8080
 * X-API-Key: sk_xxxxxxxxxxxxxxxx.xxxxxxxxxxxx.xxxxxxxxxxxxxxxx
 */
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);

    private static final String API_KEY_HEADER = "X-API-Key";

    @Autowired
    private TokenServiceI tokenServiceI;

    @Autowired
    private ApiKeyGenerator apiKeyGenerator;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String apiKey = request.getHeader(API_KEY_HEADER);

        // 如果没有API Key请求头，跳过此过滤器
        if (apiKey == null || apiKey.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        // 检查是否是API Key格式
        if (!apiKeyGenerator.isApiKey(apiKey)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // 验证API Key格式和签名
            if (!apiKeyGenerator.validateApiKey(apiKey)) {
                LOGGER.warn("Invalid API Key format or signature: {}", apiKey.substring(0, Math.min(10, apiKey.length())) + "...");
                chain.doFilter(request, response);
                return;
            }

            // 验证API Key并获取用户信息
            TokenDTO tokenDTO = tokenServiceI.verifyApiKey(apiKey);
            if (tokenDTO == null) {
                LOGGER.warn("API Key not found or invalid in database: {}", apiKey.substring(0, Math.min(10, apiKey.length())) + "...");
                chain.doFilter(request, response);
                return;
            }

            // 检查权限
            String method = request.getMethod();
            if (isReadMethod(method) && !tokenDTO.hasReadPermission()) {
                LOGGER.warn("API Key does not have read permission: {}", apiKey.substring(0, Math.min(10, apiKey.length())) + "...");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "API Key does not have read permission");
                return;
            }
            if (isWriteMethod(method) && !tokenDTO.hasWritePermission()) {
                LOGGER.warn("API Key does not have write permission: {}", apiKey.substring(0, Math.min(10, apiKey.length())) + "...");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "API Key does not have write permission");
                return;
            }

            // 创建认证对象
            // 注意：这里使用API_KEY_USER作为用户名，实际用户ID存储在details中
            UserDTO userDTO = createUserDTOFromToken(tokenDTO);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDTO,
                    null,
                    userDTO.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 设置认证信息到SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            LOGGER.debug("API Key authenticated successfully, userId: {}", tokenDTO.getUserId());

        } catch (Exception e) {
            LOGGER.error("API Key authentication failed", e);
        }

        chain.doFilter(request, response);
    }

    /**
     * 判断是否是读方法
     */
    private boolean isReadMethod(String method) {
        return "GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method);
    }

    /**
     * 判断是否是写方法
     */
    private boolean isWriteMethod(String method) {
        return "POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) ||
               "DELETE".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method);
    }

    /**
     * 从TokenDTO创建UserDTO
     */
    private UserDTO createUserDTOFromToken(TokenDTO tokenDTO) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(tokenDTO.getUserId());
        userDTO.setRole("API_KEY");  // 设置特殊角色标识
        return userDTO;
    }
}