package liu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 * dashboard页面允许所有用户访问，根据角色显示不同功能
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    /**
     * 仪表板 - 所有用户都可以访问，根据角色显示不同功能
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            // 获取当前用户信息
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = auth.getName(); // 获取当前登录用户名
            
            // 检查当前用户是否为管理员
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            // 传递用户信息到页面
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("currentUsername", currentUsername);
            
        } catch (Exception e) {
            // 如果获取用户信息失败，不影响页面显示
            logger.error("获取用户信息失败: {}", e.getMessage(), e);
            // 默认设置为非管理员
            model.addAttribute("isAdmin", false);
            model.addAttribute("currentUsername", "未知用户");
        }
        return "admin/dashboard";
    }
} 