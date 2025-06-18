package liu.controller;

import liu.entity.OperationLog;
import liu.service.OperationLogService;
import liu.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 操作日志控制器 - 仅管理员可访问
 * 支持Redis缓存功能
 */
@Controller
@RequestMapping("/logs")
@PreAuthorize("hasRole('ADMIN')")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;
    
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 日志列表页面
     */
    @GetMapping
    public String list(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "operationType", required = false) String operationType,
            @RequestParam(value = "operationModule", required = false) String operationModule,
            @RequestParam(value = "operationUser", required = false) String operationUser,
            @RequestParam(value = "startTime", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            Model model) {

        List<OperationLog> logs;
        int totalCount;

        // 根据条件查询
        if (hasSearchConditions(operationType, operationModule, operationUser, startTime, endTime)) {
            logs = operationLogService.findByCondition(operationType, operationModule, 
                    operationUser, startTime, endTime, page, size);
            totalCount = operationLogService.countByCondition(operationType, operationModule, 
                    operationUser, startTime, endTime);
        } else {
            logs = operationLogService.findByPage(page, size);
            totalCount = operationLogService.count();
        }

        // 计算分页信息
        int totalPages = (int) Math.ceil((double) totalCount / size);

        model.addAttribute("logs", logs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("size", size);
        
        // 保持搜索条件
        model.addAttribute("operationType", operationType);
        model.addAttribute("operationModule", operationModule);
        model.addAttribute("operationUser", operationUser);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);

        // 获取统计信息
        Map<String, Object> statistics = operationLogService.getOperationStatistics();
        model.addAttribute("statistics", statistics);

        return "logs/list";
    }

    /**
     * 查看日志详情
     */
    @GetMapping("/detail")
    public String detail(@RequestParam("logId") Integer logId, Model model) {
        OperationLog log = operationLogService.findById(logId);
        model.addAttribute("log", log);
        return "logs/detail";
    }

    /**
     * 简单测试页面
     */
    @GetMapping("/test")
    public String test(Model model) {
        List<OperationLog> logs = operationLogService.findByPage(1, 10);
        model.addAttribute("logs", logs);
        return "logs/simple";
    }

    /**
     * 获取操作统计数据
     */
    @GetMapping("/statistics")
    @ResponseBody
    public Map<String, Object> getStatistics() {
        return operationLogService.getOperationStatistics();
    }

    /**
     * 清理过期日志
     */
    @GetMapping("/cleanup")
    @ResponseBody
    public Map<String, Object> cleanupLogs() {
        try {
            int deletedCount = operationLogService.cleanExpiredLogs();
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", true);
            result.put("message", "成功清理了 " + deletedCount + " 条过期日志");
            result.put("deletedCount", deletedCount);
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("message", "清理失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 清除日志相关缓存
     */
    @GetMapping("/cache/clear")
    @ResponseBody
    public Map<String, Object> clearLogCache() {
        try {
            // 清除日志相关的所有缓存
            redisUtils.deleteByPattern("operation_log*");
            
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", true);
            result.put("message", "缓存清除成功");
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("message", "缓存清除失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 获取缓存统计信息
     */
    @GetMapping("/cache/info")
    @ResponseBody
    public Map<String, Object> getCacheInfo() {
        try {
            Map<String, Object> result = new java.util.HashMap<>();
            
            // 检查各种缓存键是否存在
            boolean hasPageCache = redisUtils.hasKey("operation_log_page:1:10");
            boolean hasStatisticsCache = redisUtils.hasKey("operation_log_statistics");
            
            result.put("success", true);
            result.put("hasPageCache", hasPageCache);
            result.put("hasStatisticsCache", hasStatisticsCache);
            result.put("redisInfo", redisUtils.getRedisInfo());
            
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("message", "获取缓存信息失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 检查是否有搜索条件
     */
    private boolean hasSearchConditions(String operationType, String operationModule,
                                       String operationUser, LocalDateTime startTime,
                                       LocalDateTime endTime) {
        return (operationType != null && !operationType.trim().isEmpty()) ||
               (operationModule != null && !operationModule.trim().isEmpty()) ||
               (operationUser != null && !operationUser.trim().isEmpty()) ||
               startTime != null || endTime != null;
    }
} 