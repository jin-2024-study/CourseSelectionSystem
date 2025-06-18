package liu.listener;

import liu.entity.CourseSelectionNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * 选课通知消息监听器
 * @author liu
 */
@Component
public class CourseSelectionNotificationListener {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseSelectionNotificationListener.class);
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * 监听队列消息
     */
    @JmsListener(destination = "course.selection.queue")
    public void handleQueueNotification(CourseSelectionNotification notification) {
        logger.info("接收到队列消息: {}", notification);
        
        try {
            // 处理业务逻辑，如保存到数据库、发送邮件等
            processNotification(notification);
            
            // 将消息转发到WebSocket，实现实时通知
            forwardToWebSocket(notification);
            
        } catch (Exception e) {
            logger.error("处理队列消息失败: {}", e.getMessage(), e);
            // 这里可以实现重试机制或错误处理
        }
    }
    
    /**
     * 监听主题消息
     */
    @JmsListener(destination = "course.selection.topic")
    public void handleTopicNotification(CourseSelectionNotification notification) {
        logger.info("接收到主题消息: {}", notification);
        
        try {
            // 处理业务逻辑
            processNotification(notification);
            
            // 将主题消息也转发到WebSocket
            forwardToWebSocket(notification);
            
        } catch (Exception e) {
            logger.error("处理主题消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 处理通知的业务逻辑
     */
    private void processNotification(CourseSelectionNotification notification) {
        // 根据通知类型执行不同的处理逻辑
        switch (notification.getType()) {
            case COURSE_SELECTION_SUCCESS:
                logger.info("处理选课成功通知: 学生ID={}, 学生姓名={}", 
                          notification.getStudentId(), notification.getStudentName());
                // 这里可以添加额外的业务逻辑，如更新统计数据、发送邮件等
                break;
                
            case COURSE_SELECTION_FAILED:
                logger.warn("处理选课失败通知: 学生ID={}, 失败原因={}", 
                          notification.getStudentId(), notification.getMessage());
                // 这里可以添加失败处理逻辑，如记录错误日志、通知管理员等
                break;
                
            case COURSE_CHANGE:
                logger.info("处理课程变更通知: 学生ID={}, 课程信息={}", 
                          notification.getStudentId(), notification.getCourseInfo());
                // 这里可以添加课程变更处理逻辑
                break;
                
            case COURSE_DELETION:
                logger.info("处理课程删除通知: 学生ID={}, 课程信息={}", 
                          notification.getStudentId(), notification.getCourseInfo());
                // 这里可以添加课程删除处理逻辑
                break;
                
            case NEW_SEMESTER_AVAILABLE:
                logger.info("处理新学期开放选课通知: 学年={}, 学期={}", 
                          notification.getAcademicYear(), notification.getSemester());
                // 这里可以添加新学期开放选课的处理逻辑
                break;
                
            default:
                logger.warn("未知的通知类型: {}", notification.getType());
        }
    }
    
    /**
     * 将消息转发到WebSocket
     */
    private void forwardToWebSocket(CourseSelectionNotification notification) {
        try {
            // 对于所有类型的通知，都发送到公共主题让所有用户都能看到
            messagingTemplate.convertAndSend("/topic/notifications", notification);
            logger.info("消息已转发到WebSocket: 类型={}, 学生ID={}, 消息={}", 
                       notification.getType(), notification.getStudentId(), notification.getMessage());
            
            // 如果有特定学生ID，也发送给特定用户
            if (notification.getStudentId() != null) {
                messagingTemplate.convertAndSendToUser(
                        notification.getStudentId().toString(),
                        "/queue/notifications",
                        notification
                );
                logger.debug("消息已发送给特定用户: 用户ID={}", notification.getStudentId());
            }
            
        } catch (Exception e) {
            logger.error("转发消息到WebSocket失败: {}", e.getMessage(), e);
        }
    }
} 