package cn.edu.qcl.security;

import cn.edu.qcl.api.UserServiceI;
import cn.edu.qcl.dto.data.UserDTO;
import cn.edu.qcl.utils.UserContextHolder;
import cn.edu.qcl.utils.UserSessionUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


/**
 * 拦截所有进入系统的 HTTP 请求，从请求头中提取 JWT Token
 * JWT登录授权过滤器
 *
 * 请求示例
 * GET /api/user/profile HTTP/1.1
 * Host: localhost:8080
 * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
 * Content-Type: application/json
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    @Autowired
    private UserServiceI userServiceI;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Value("${secure.ignored.urls}")
    private List<String> ignoredUrls;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // todo-2026.05.15 待数据库ready后删除  检查请求URL是否在白名单中，如果是则跳过JWT验证
        String requestUri = request.getRequestURI();
        if (isIgnoredUrl(requestUri)) {
            LOGGER.debug("Request URI {} is in ignored urls, skipping JWT authentication", requestUri);
            chain.doFilter(request, response);
            return;
        }
        
        // 如果请求头中有 X-API-Key，跳过 JWT 验证，交给 ApiKeyAuthenticationFilter 处理
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null && !apiKey.isEmpty()) {
            LOGGER.debug("X-API-Key detected, skipping JWT authentication");
            chain.doFilter(request, response);
            return;
        }

        try {
            String authHeader = request.getHeader(this.tokenHeader);
            if (authHeader != null && authHeader.startsWith(this.tokenHead)) {
                String authToken = authHeader.substring(this.tokenHead.length());// The part after "Bearer "
                String username = jwtTokenUtil.getUserNameFromToken(authToken);
                LOGGER.info("checking username:{}", username);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDTO user = this.userServiceI.queryByUsername(username);
                    if (user != null && jwtTokenUtil.validateToken(authToken, user.getUsername())) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        LOGGER.info("authenticated user:{}", username);
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        Long userId = user.getUserId();
                        if (userId != null) {
                            UserContextHolder.setUserId(userId);
                        }
                    }
                }/*else {
                    throw new RuntimeException("暂未登录或token已经过期");
                }*/
            }/*else {
                throw new RuntimeException("请传入认证token");
            }*/
            chain.doFilter(request, response);
        } finally {
            UserContextHolder.clear(); // 每次请求结束必须清理，防止线程复用时泄露
        }
    }
    
    /**
     *  todo-2026.05.15 待数据库ready后删除
     *  检查请求URL是否在白名单中
     * @param requestUri 请求URI
     * @return 是否在白名单中
     */
    private boolean isIgnoredUrl(String requestUri) {
        if (ignoredUrls == null || ignoredUrls.isEmpty()) {
            return false;
        }
        for (String pattern : ignoredUrls) {
            // 支持Ant风格路径匹配，并去除前后空白
            String trimmedPattern = pattern.trim();
            if (pathMatcher.match(trimmedPattern, requestUri)) {
                return true;
            }
        }
        return false;
    }

}
