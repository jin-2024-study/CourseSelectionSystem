package liu.controller;

import liu.entity.User;
import liu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 管理员控制器
 * dashboard页面允许所有用户访问，其他接口只有ADMIN角色可以访问
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

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
                    .anyMatch(a -> a.getAuthority().equals("ROLE_" +
                            "ADMIN"));
            
            // 获取用户详细信息
            User currentUser = null;
            try {
                currentUser = userService.findByUsername(currentUsername);
            } catch (Exception e) {
                System.err.println("获取用户详细信息失败: " + e.getMessage());
            }
            
            // 传递用户信息到页面
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("currentUsername", currentUsername);
            model.addAttribute("currentUser", currentUser);
            
            // 只有管理员才需要用户数据
            if (isAdmin) {
                List<User> users = userService.findAll();
                model.addAttribute("users", users);
                model.addAttribute("totalUsers", users.size());
            }
        } catch (Exception e) {
            // 如果获取用户信息失败，不影响页面显示
            System.err.println("获取用户信息失败: " + e.getMessage());
            // 默认设置为非管理员
            model.addAttribute("isAdmin", false);
            model.addAttribute("currentUsername", "未知用户");
        }
        return "admin/dashboard";
    }

    /**
     * 用户管理页面
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String userManagement(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin/user-management";
    }

    /**
     * 显示添加用户页面
     */
    @GetMapping("/users/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addUserPage() {
        return "admin/add-user";
    }

    /**
     * 添加新用户
     */
    @PostMapping("/users/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addUser(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String email,
                         @RequestParam String role,
                         RedirectAttributes redirectAttributes) {
        try {
            userService.createUser(username, password, email, role);
            redirectAttributes.addFlashAttribute("message", "用户创建成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /**
     * 显示编辑用户页面
     */
    @GetMapping("/users/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editUserPage(@PathVariable Integer id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user);
        return "admin/edit-user";
    }

    /**
     * 更新用户信息
     */
    @PostMapping("/users/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editUser(@PathVariable Integer id,
                          @RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String role,
                          @RequestParam(required = false) String password,
                          @RequestParam Boolean enabled,
                          RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "用户不存在");
                return "redirect:/admin/users";
            }

            user.setUsername(username);
            user.setEmail(email);
            user.setRole(role);
            user.setEnabled(enabled);
            
            // 如果提供了新密码，则更新密码
            if (password != null && !password.trim().isEmpty()) {
                // 这里需要在UserService中添加更新密码的方法
                user.setPassword(password);
            }

            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("message", "用户更新成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /**
     * 删除用户
     */
    @PostMapping("/users/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            if (userService.deleteUser(id)) {
                redirectAttributes.addFlashAttribute("message", "用户删除成功");
            } else {
                redirectAttributes.addFlashAttribute("error", "删除用户失败");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }
} 