package liu.service;

import liu.entity.CourseSelectionNotification;
import liu.entity.Student;
import liu.entity.Course;

import java.util.List;

/**
 * 通知服务接口
 * @author liu
 */
public interface NotificationService {
    
    /**
     * 发送选课成功通知
     * @param student 学生信息
     * @param courses 选课的课程列表
     * @param academicYear 学年
     * @param semester 学期
     */
    void sendCourseSelectionSuccessNotification(Student student, List<Course> courses, String academicYear, String semester);
    
    /**
     * 发送选课失败通知
     * @param student 学生信息
     * @param errorMessage 失败原因
     * @param academicYear 学年
     * @param semester 学期
     */
    void sendCourseSelectionFailedNotification(Student student, String errorMessage, String academicYear, String semester);
    
    /**
     * 发送课程变更通知
     * @param student 学生信息
     * @param course 变更的课程
     * @param changeType 变更类型
     * @param academicYear 学年
     * @param semester 学期
     */
    void sendCourseChangeNotification(Student student, Course course, String changeType, String academicYear, String semester);
    
    /**
     * 发送课程删除通知
     * @param student 学生信息
     * @param course 删除的课程
     * @param academicYear 学年
     * @param semester 学期
     */
    void sendCourseDeletionNotification(Student student, Course course, String academicYear, String semester);
    
    /**
     * 发送新学期开放选课广播通知（发送给所有用户）
     * @param academicYear 学年
     * @param semester 学期
     */
    void sendNewSemesterNotification(String academicYear, String semester);
    
    /**
     * 发送新学期开放选课通知给指定用户（单播）
     * @param targetStudent 目标学生
     * @param academicYear 学年
     * @param semester 学期
     * @param adminName 执行操作的管理员姓名
     */
    void sendNewSemesterNotificationToUser(Student targetStudent, String academicYear, String semester, String adminName);
    
    /**
     * 发送通知消息到队列
     * @param notification 通知对象
     */
    void sendNotificationToQueue(CourseSelectionNotification notification);
    
    /**
     * 发送通知消息到主题
     * @param notification 通知对象
     */
    void sendNotificationToTopic(CourseSelectionNotification notification);
    
    /**
     * 获取指定学生的通知列表
     * @param studentId 学生ID
     * @param limit 限制数量
     * @return 通知列表
     */
    List<CourseSelectionNotification> getNotificationsByStudentId(Integer studentId, int limit);
    
    /**
     * 获取所有通知（用于管理员查看）
     * @param limit 限制数量
     * @return 通知列表
     */
    List<CourseSelectionNotification> getAllNotifications(int limit);
    
    /**
     * 获取广播通知
     * @param limit 限制数量
     * @return 广播通知列表
     */
    List<CourseSelectionNotification> getBroadcastNotifications(int limit);
    
    /**
     * 保存通知到数据库
     * @param notification 通知对象
     * @return 是否保存成功
     */
    boolean saveNotification(CourseSelectionNotification notification);
    
    /**
     * 清空所有通知
     * @return 是否清空成功
     */
    boolean clearAllNotifications();
} 