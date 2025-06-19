package liu.service.impl;

import liu.dao.NotificationDao;
import liu.entity.Course;
import liu.entity.CourseSelectionNotification;
import liu.entity.Student;
import liu.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Queue;
import javax.jms.Topic;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 * @author liu
 */
@Service
public class NotificationServiceImpl implements NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    
    private final JmsTemplate jmsTemplate;
    private final Queue courseSelectionQueue;
    private final Topic courseSelectionTopic;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationDao notificationDao;
    
    @Autowired
    public NotificationServiceImpl(
            JmsTemplate jmsTemplate,
            Queue courseSelectionQueue,
            Topic courseSelectionTopic,
            @Lazy SimpMessagingTemplate messagingTemplate,
            NotificationDao notificationDao) {
        this.jmsTemplate = jmsTemplate;
        this.courseSelectionQueue = courseSelectionQueue;
        this.courseSelectionTopic = courseSelectionTopic;
        this.messagingTemplate = messagingTemplate;
        this.notificationDao = notificationDao;
    }
    
    @Override
    public void sendCourseSelectionSuccessNotification(Student student, List<Course> courses, String academicYear, String semester) {
        String courseInfo = courses.stream()
                .map(course -> course.getCourse_name() + "(" + course.getCourse_code() + ")")
                .collect(Collectors.joining(", "));
        
        CourseSelectionNotification notification = new CourseSelectionNotification(
                student.getStudent_id(),
                student.getStudent_name(),
                student.getStudent_number(),
                CourseSelectionNotification.NotificationType.COURSE_SELECTION_SUCCESS,
                "选课成功",
                String.format("恭喜您成功选择了 %d 门课程", courses.size())
        );
        
        notification.setCourseInfo(courseInfo);
        notification.setAcademicYear(academicYear);
        notification.setSemester(semester);
        
        // 保存到数据库
        saveNotification(notification);
        
        // 发送到队列和主题
        sendNotificationToQueue(notification);
        sendNotificationToTopic(notification);
        
        // 发送WebSocket实时通知
        sendWebSocketNotification(notification);
        
        logger.info("发送选课成功通知: 学生={}, 课程={}", student.getStudent_name(), courseInfo);
    }
    
    @Override
    public void sendCourseSelectionFailedNotification(Student student, String errorMessage, String academicYear, String semester) {
        CourseSelectionNotification notification = new CourseSelectionNotification(
                student.getStudent_id(),
                student.getStudent_name(),
                student.getStudent_number(),
                CourseSelectionNotification.NotificationType.COURSE_SELECTION_FAILED,
                "选课失败",
                "选课失败: " + errorMessage
        );
        
        notification.setAcademicYear(academicYear);
        notification.setSemester(semester);
        
        // 保存到数据库
        saveNotification(notification);
        
        // 发送到队列和主题
        sendNotificationToQueue(notification);
        sendNotificationToTopic(notification);
        
        // 发送WebSocket实时通知
        sendWebSocketNotification(notification);
        
        logger.warn("发送选课失败通知: 学生={}, 错误={}", student.getStudent_name(), errorMessage);
    }
    
    @Override
    public void sendCourseChangeNotification(Student student, Course course, String changeType, String academicYear, String semester) {
        String courseInfo = course.getCourse_name() + "(" + course.getCourse_code() + ")";
        
        CourseSelectionNotification notification = new CourseSelectionNotification(
                student.getStudent_id(),
                student.getStudent_name(),
                student.getStudent_number(),
                CourseSelectionNotification.NotificationType.COURSE_CHANGE,
                "课程信息变更",
                String.format("您的课程 %s 信息已更新: %s", courseInfo, changeType)
        );
        
        notification.setCourseInfo(courseInfo);
        notification.setAcademicYear(academicYear);
        notification.setSemester(semester);
        
        // 保存到数据库
        saveNotification(notification);
        
        // 发送到队列和主题
        sendNotificationToQueue(notification);
        sendNotificationToTopic(notification);
        
        // 发送WebSocket实时通知
        sendWebSocketNotification(notification);
        
        logger.info("发送课程变更通知: 学生={}, 课程={}, 变更类型={}", student.getStudent_name(), courseInfo, changeType);
    }
    
    @Override
    public void sendCourseDeletionNotification(Student student, Course course, String academicYear, String semester) {
        String courseInfo = course.getCourse_name() + "(" + course.getCourse_code() + ")";
        
        CourseSelectionNotification notification = new CourseSelectionNotification(
                student.getStudent_id(),
                student.getStudent_name(),
                student.getStudent_number(),
                CourseSelectionNotification.NotificationType.COURSE_DELETION,
                "课程删除通知",
                String.format("您的课程 %s 已被删除", courseInfo)
        );
        
        notification.setCourseInfo(courseInfo);
        notification.setAcademicYear(academicYear);
        notification.setSemester(semester);
        
        // 保存到数据库
        saveNotification(notification);
        
        // 发送到队列和主题
        sendNotificationToQueue(notification);
        sendNotificationToTopic(notification);
        
        // 发送WebSocket实时通知
        sendWebSocketNotification(notification);
        
        logger.info("发送课程删除通知: 学生={}, 课程={}", student.getStudent_name(), courseInfo);
    }
    
    @Override
    public void sendNewSemesterNotification(String academicYear, String semester) {
        // 创建一个广播通知（不针对特定学生）
        CourseSelectionNotification notification = new CourseSelectionNotification(
                null, // 广播通知，不针对特定学生
                "系统通知",
                "SYSTEM",
                CourseSelectionNotification.NotificationType.NEW_SEMESTER_AVAILABLE,
                "新学期开放选课",
                String.format("📢 %s %s 已开放选课，请及时登录系统进行选课！", academicYear, semester)
        );
        
        notification.setAcademicYear(academicYear);
        notification.setSemester(semester);
        
        // 保存到数据库
        saveNotification(notification);
        
        // 只发送到主题，让监听器处理WebSocket转发
        sendNotificationToTopic(notification);
        
        logger.info("发送新学期开放选课广播通知: 学年={}, 学期={}", academicYear, semester);
    }
    
    @Override
    public void sendNewSemesterNotificationToUser(Student targetStudent, String academicYear, String semester, String adminName) {
        logger.info("=== 开始发送新学期单播通知 ===");
        logger.info("目标学生: {} ({})", targetStudent.getStudent_name(), targetStudent.getStudent_number());
        logger.info("学年学期: {} {}", academicYear, semester);
        logger.info("管理员: {}", adminName);
        
        // 创建针对特定学生的通知
        CourseSelectionNotification studentNotification = new CourseSelectionNotification(
                targetStudent.getStudent_id(),
                targetStudent.getStudent_name(),
                targetStudent.getStudent_number(),
                CourseSelectionNotification.NotificationType.NEW_SEMESTER_AVAILABLE,
                "新学期开放选课",
                String.format("🎓 管理员 已为%s开放了 %s %s 的选课，请及时登录系统进行选课！",
                    targetStudent.getStudent_name(),academicYear, semester)
        );
        
        studentNotification.setAcademicYear(academicYear);
        studentNotification.setSemester(semester);
        
        logger.info("✅ 学生通知创建完成: {}", studentNotification.getMessage());
        
        // 保存学生通知到数据库
        saveNotification(studentNotification);
        
        // 发送到队列和主题
        sendNotificationToQueue(studentNotification);
        sendNotificationToTopic(studentNotification);
        
        // 直接发送WebSocket通知给学生
        sendWebSocketNotification(studentNotification);
        
        // 创建管理员通知（记录管理员的操作）
        CourseSelectionNotification adminNotification = new CourseSelectionNotification(
                null, // 管理员通知
                "管理员操作记录",
                "ADMIN",
                CourseSelectionNotification.NotificationType.NEW_SEMESTER_AVAILABLE,
                "新学期开放选课操作",
                String.format("✅ 管理员%s已成功为学生 %s (%s) 开放了 %s %s 的选课",
                    adminName,targetStudent.getStudent_name(), targetStudent.getStudent_number(), academicYear, semester)
        );
        
        adminNotification.setAcademicYear(academicYear);
        adminNotification.setSemester(semester);
        
        logger.info("✅ 管理员通知创建完成: {}", adminNotification.getMessage());
        
        // 保存管理员通知到数据库
        saveNotification(adminNotification);
        
        // 发送管理员通知到主题（管理员也能在通知中心看到）
        sendNotificationToTopic(adminNotification);
        
        // 发送WebSocket广播通知给所有在线用户（包括管理员）
        sendBroadcastWebSocketNotification(adminNotification);
        
        logger.info("=== 新学期单播通知发送完成 ===");
        logger.info("发送新学期开放选课单播通知: 学生={}, 学年={}, 学期={}, 管理员={}", 
                targetStudent.getStudent_name(), academicYear, semester, adminName);
    }
    
    @Override
    public void sendNotificationToQueue(CourseSelectionNotification notification) {
        try {
            jmsTemplate.convertAndSend(courseSelectionQueue, notification);
            logger.debug("消息已发送到队列: {}", notification);
        } catch (Exception e) {
            logger.error("发送消息到队列失败: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public void sendNotificationToTopic(CourseSelectionNotification notification) {
        try {
            jmsTemplate.convertAndSend(courseSelectionTopic, notification);
            logger.debug("消息已发送到主题: {}", notification);
        } catch (Exception e) {
            logger.error("发送消息到主题失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 发送WebSocket实时通知
     */
    private void sendWebSocketNotification(CourseSelectionNotification notification) {
        try {
            if (messagingTemplate != null) {
                // 发送给特定用户
                messagingTemplate.convertAndSendToUser(
                        notification.getStudentId().toString(),
                        "/queue/notifications",
                        notification
                );
                
                // 发送给所有用户（可选）
                messagingTemplate.convertAndSend("/topic/notifications", notification);
                
                logger.debug("WebSocket通知已发送: {}", notification);
            } else {
                logger.warn("SimpMessagingTemplate未初始化，跳过WebSocket通知");
            }
        } catch (Exception e) {
            logger.error("发送WebSocket通知失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 发送WebSocket广播通知（发送给所有在线用户）
     */
    private void sendBroadcastWebSocketNotification(CourseSelectionNotification notification) {
        try {
            if (messagingTemplate != null) {
                // 发送给所有在线用户
                messagingTemplate.convertAndSend("/topic/notifications", notification);
                
                logger.info("WebSocket广播通知已发送: 类型={}, 消息={}", notification.getType(), notification.getMessage());
            } else {
                logger.warn("SimpMessagingTemplate未初始化，跳过WebSocket广播通知");
            }
        } catch (Exception e) {
            logger.error("发送WebSocket广播通知失败: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public List<CourseSelectionNotification> getNotificationsByStudentId(Integer studentId, int limit) {
        try {
            return notificationDao.getNotificationsByStudentId(studentId, limit);
        } catch (Exception e) {
            logger.error("获取学生通知失败: studentId={}, error={}", studentId, e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<CourseSelectionNotification> getAllNotifications(int limit) {
        try {
            return notificationDao.getAllNotifications(limit);
        } catch (Exception e) {
            logger.error("获取所有通知失败: error={}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<CourseSelectionNotification> getBroadcastNotifications(int limit) {
        try {
            return notificationDao.getBroadcastNotifications(limit);
        } catch (Exception e) {
            logger.error("获取广播通知失败: error={}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public boolean saveNotification(CourseSelectionNotification notification) {
        try {
            int result = notificationDao.insertNotification(notification);
            boolean success = result > 0;
            
            if (success) {
                logger.debug("通知已保存到数据库: 类型={}, 学生ID={}, 消息={}", 
                    notification.getType(), notification.getStudentId(), notification.getMessage());
            } else {
                logger.warn("通知保存失败: 类型={}, 学生ID={}, 消息={}", 
                    notification.getType(), notification.getStudentId(), notification.getMessage());
            }
            
            return success;
        } catch (Exception e) {
            logger.error("保存通知到数据库失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean clearAllNotifications() {
        try {
            int result = notificationDao.clearAllNotifications();
            logger.info("已清空所有通知，删除数量: {}", result);
            return result >= 0;
        } catch (Exception e) {
            logger.error("清空所有通知失败: {}", e.getMessage(), e);
            return false;
        }
    }
} 