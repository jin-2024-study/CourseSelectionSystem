package liu.controller;

import liu.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 认证控制器
 * 处理用户登录、注册、登出等功能
 */
@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 显示登录页面
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "用户名或密码错误");
        }
        if (logout != null) {
            model.addAttribute("message", "您已成功登出");
        }
        return "auth/login";
    }

    /**
     * 显示注册页面
     */
    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    /**
     * 处理用户注册
     */
    @PostMapping("/register")
    public String register(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String email,
                          @RequestParam(defaultValue = "USER") String role,
                          RedirectAttributes redirectAttributes) {
        try {
            // 验证角色
            if (!role.equals("USER") && !role.equals("ADMIN")) {
                role = "USER";
            }
            
            userService.createUser(username, password, email, role);
            redirectAttributes.addFlashAttribute("message", "注册成功，请登录");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    /**
     * 访问拒绝页面
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }

    /**
     * 首页重定向
     */
    @GetMapping("/")
    public String home() {
        // 所有用户都重定向到dashboard页面，页面内部根据角色显示不同功能
        return "redirect:/admin/dashboard";
    }

    /**
     * 处理用户登录
     */
    @PostMapping("/login")
    public String login(@RequestParam String username,
                       @RequestParam String password,
                       HttpServletRequest request,
                       RedirectAttributes redirectAttributes) {
        try {
            // 验证用户输入
            if (username == null || username.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "用户名不能为空");
                return "redirect:/login";
            }
            
            if (password == null || password.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "密码不能为空");
                return "redirect:/login";
            }

            // 创建认证token
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(username.trim(), password);
            
            // 设置认证详情（IP地址等）
            authToken.setDetails(new WebAuthenticationDetails(request));
            
            // 执行认证
            Authentication authentication = authenticationManager.authenticate(authToken);
            
            // 认证成功，设置到SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 记录登录日志（可选）
            logger.info("用户登录成功: {}, IP: {}", username, request.getRemoteAddr());
            
            // 所有用户都重定向到dashboard页面，页面内部根据角色显示不同功能
            return "redirect:/admin/dashboard";
            
        } catch (AuthenticationException e) {
            // 认证失败
            logger.warn("用户登录失败: {}, 原因: {}", username, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "用户名或密码错误");
            return "redirect:/login?error=true";
        } catch (Exception e) {
            // 其他异常
            logger.error("登录过程中发生异常: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "登录失败，请稍后重试");
            return "redirect:/login?error=true";
        }
    }
} 