package liu.controller;

import liu.entity.CourseSelectionNotification;
import liu.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知页面控制器
 * @author liu
 */
@Controller
@RequestMapping("/notifications")
public class NotificationPageController {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * 显示实时通知页面 - 根据用户角色过滤通知
     */
    @GetMapping
    public String showNotifications(Model model) {
        // 检查当前用户角色
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        // 加载最近的通知记录
        List<CourseSelectionNotification> allNotifications = notificationService.getAllNotifications(50);
        
        // 根据用户角色过滤通知
        List<CourseSelectionNotification> filteredNotifications;
        if (isAdmin) {
            // 管理员只显示"新学期开放选课操作"相关记录
            filteredNotifications = allNotifications.stream()
                    .filter(notification -> "新学期开放选课操作".equals(notification.getTitle()))
                    .collect(Collectors.toList());
        } else {
            // 普通用户只显示"新学期开放选课"相关记录
            filteredNotifications = allNotifications.stream()
                    .filter(notification -> "新学期开放选课".equals(notification.getTitle()))
                    .collect(Collectors.toList());
        }
        
        model.addAttribute("recentNotifications", filteredNotifications);
        model.addAttribute("isAdmin", isAdmin);
        return "notifications/index";
    }
    
    /**
     * 获取所有通知的API接口 - 根据用户角色过滤
     */
    @GetMapping("/api/all")
    @ResponseBody
    public List<CourseSelectionNotification> getAllNotifications(@RequestParam(defaultValue = "50") int limit) {
        // 检查当前用户角色
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        // 获取所有通知
        List<CourseSelectionNotification> allNotifications = notificationService.getAllNotifications(limit);
        
        // 根据用户角色过滤通知
        if (isAdmin) {
            // 管理员只显示"新学期开放选课操作"相关记录
            return allNotifications.stream()
                    .filter(notification -> "新学期开放选课操作".equals(notification.getTitle()))
                    .collect(Collectors.toList());
        } else {
            // 普通用户只显示"新学期开放选课"相关记录
            return allNotifications.stream()
                    .filter(notification -> "新学期开放选课".equals(notification.getTitle()))
                    .collect(Collectors.toList());
        }
    }
    
    /**
     * 获取指定学生的通知
     */
    @GetMapping("/api/student")
    @ResponseBody
    public List<CourseSelectionNotification> getStudentNotifications(
            @RequestParam Integer studentId,
            @RequestParam(defaultValue = "50") int limit) {
        return notificationService.getNotificationsByStudentId(studentId, limit);
    }
    
    /**
     * 获取广播通知
     */
    @GetMapping("/api/broadcast")
    @ResponseBody
    public List<CourseSelectionNotification> getBroadcastNotifications(@RequestParam(defaultValue = "50") int limit) {
        return notificationService.getBroadcastNotifications(limit);
    }
    
    /**
     * 清空所有通知
     */
    @GetMapping("/api/clear")
    @ResponseBody
    public String clearAllNotifications() {
        boolean success = notificationService.clearAllNotifications();
        return success ? "所有通知已清空" : "清空通知失败";
    }
} 