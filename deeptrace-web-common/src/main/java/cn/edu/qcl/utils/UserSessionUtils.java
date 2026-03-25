package cn.edu.qcl.utils;

import cn.edu.qcl.dto.data.UserDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserSessionUtils {
    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        // 从SecurityContext获取用户信息
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDTO) {
            return ((UserDTO) principal).getUserId();
        }
        // 如果principal是用户名字符串，需要查询用户ID
        if (principal instanceof String) {
            // 这里假设用户名就是userId，实际情况需要根据项目调整
            throw new RuntimeException("无法获取用户ID");
        }
        throw new RuntimeException("用户未登录");
    }
    
    
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
    
    public static void setUser(UserDTO user) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
