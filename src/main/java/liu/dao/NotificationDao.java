package liu.dao;

import liu.entity.CourseSelectionNotification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知数据访问层接口
 * @author liu
 */
@Mapper
public interface NotificationDao {

    /**
     * 保存通知到数据库
     */
    int insertNotification(CourseSelectionNotification notification);

    /**
     * 获取指定学生的通知列表
     */
    List<CourseSelectionNotification> getNotificationsByStudentId(@Param("studentId") Integer studentId, @Param("limit") int limit);

    /**
     * 获取所有通知（用于管理员查看）
     */
    List<CourseSelectionNotification> getAllNotifications(@Param("limit") int limit);

    /**
     * 获取广播通知（学生ID为null的通知）
     */
    List<CourseSelectionNotification> getBroadcastNotifications(@Param("limit") int limit);

    /**
     * 删除指定学生的通知
     */
    int deleteNotificationsByStudentId(@Param("studentId") Integer studentId);

    /**
     * 删除指定通知
     */
    int deleteNotificationById(@Param("id") Long id);

    /**
     * 清空所有通知
     */
    int clearAllNotifications();

    /**
     * 根据ID获取通知
     */
    CourseSelectionNotification getNotificationById(@Param("id") Long id);

    /**
     * 更新通知信息
     */
    int updateNotification(CourseSelectionNotification notification);

    /**
     * 统计指定学生的通知数量
     */
    int countNotificationsByStudentId(@Param("studentId") Integer studentId);

    /**
     * 获取最近的通知（分页）
     */
    List<CourseSelectionNotification> getRecentNotifications(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 动态查询通知
     */
    List<CourseSelectionNotification> findNotifications(
            @Param("studentId") Integer studentId,
            @Param("type") String type,
            @Param("academicYear") String academicYear,
            @Param("semester") String semester,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit
    );

    /**
     * 批量插入通知
     */
    int batchInsertNotifications(List<CourseSelectionNotification> notifications);

    /**
     * 根据条件删除通知
     */
    int deleteNotificationsByCondition(
            @Param("studentId") Integer studentId,
            @Param("type") String type,
            @Param("academicYear") String academicYear,
            @Param("semester") String semester,
            @Param("beforeDate") LocalDateTime beforeDate
    );
} 