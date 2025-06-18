package liu.controller;

import liu.entity.CourseSelectionNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

/**
 * WebSocket通知控制器
 * @author liu
 */
@Controller
public class NotificationController {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    /**
     * 处理客户端订阅通知的请求
     */
    @SubscribeMapping("/topic/notifications")
    public String subscribeToNotifications() {
        logger.info("客户端订阅了通知主题");
        return "欢迎订阅选课通知服务！";
    }
    
    /**
     * 处理客户端发送的消息
     */
    @MessageMapping("/notification")
    @SendTo("/topic/notifications")
    public CourseSelectionNotification handleNotification(CourseSelectionNotification notification) {
        logger.info("收到客户端通知消息: {}", notification);
        return notification;
    }
} 