package liu.dao;

import liu.entity.CourseSelectionNotification;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NotificationDao测试类
 * 验证XML映射配置是否正确
 * @author liu
 */
@SpringBootTest
@Transactional // 测试后回滚
public class NotificationDaoTest {

    @Resource
    private NotificationDao notificationDao;

    @Test
    public void testInsertNotification() {
        // 创建测试通知
        CourseSelectionNotification notification = new CourseSelectionNotification();
        notification.setStudentId(1001);
        notification.setStudentName("张三");
        notification.setStudentNumber("20230001");
        notification.setType(CourseSelectionNotification.NotificationType.COURSE_SELECTION_SUCCESS);
        notification.setTitle("选课成功");
        notification.setMessage("您已成功选择课程");
        notification.setCourseInfo("高等数学(MATH001)");
        notification.setAcademicYear("2023-2024");
        notification.setSemester("第一学期");
        notification.setTimestamp(LocalDateTime.now());

        // 插入通知
        int result = notificationDao.insertNotification(notification);
        
        // 验证插入结果
        assertEquals(1, result);
        assertNotNull(notification.getId());
        System.out.println("插入通知成功，ID: " + notification.getId());
    }

    @Test
    public void testGetNotificationsByStudentId() {
        // 先插入一条测试数据
        CourseSelectionNotification notification = createTestNotification(1002, "李四", "20230002");
        notificationDao.insertNotification(notification);

        // 查询通知
        List<CourseSelectionNotification> notifications = 
            notificationDao.getNotificationsByStudentId(1002, 10);
        
        // 验证查询结果
        assertFalse(notifications.isEmpty());
        assertEquals(1002, notifications.get(0).getStudentId());
        System.out.println("查询到通知数量: " + notifications.size());
    }

    @Test
    public void testCountNotificationsByStudentId() {
        // 先插入测试数据
        CourseSelectionNotification notification1 = createTestNotification(1003, "王五", "20230003");
        CourseSelectionNotification notification2 = createTestNotification(1003, "王五", "20230003");
        
        notificationDao.insertNotification(notification1);
        notificationDao.insertNotification(notification2);

        // 统计通知数量
        int count = notificationDao.countNotificationsByStudentId(1003);
        
        // 验证统计结果
        assertEquals(2, count);
        System.out.println("学生1003的通知数量: " + count);
    }

    @Test
    public void testBatchInsertNotifications() {
        // 创建批量通知
        List<CourseSelectionNotification> notifications = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            CourseSelectionNotification notification = createTestNotification(
                2000 + i, "批量测试" + i, "BATCH00" + i);
            notifications.add(notification);
        }

        // 批量插入
        int result = notificationDao.batchInsertNotifications(notifications);
        
        // 验证批量插入结果
        assertEquals(3, result);
        System.out.println("批量插入通知成功，数量: " + result);
    }

    @Test
    public void testFindNotifications() {
        // 先插入测试数据
        CourseSelectionNotification notification = createTestNotification(1004, "赵六", "20230004");
        notification.setAcademicYear("2023-2024");
        notification.setSemester("第二学期");
        notificationDao.insertNotification(notification);

        // 动态查询
        List<CourseSelectionNotification> notifications = notificationDao.findNotifications(
            1004, // studentId
            null, // type
            "2023-2024", // academicYear
            "第二学期", // semester
            null, // startDate
            null, // endDate
            null, // offset
            10 // limit
        );
        
        // 验证查询结果
        assertFalse(notifications.isEmpty());
        assertEquals("2023-2024", notifications.get(0).getAcademicYear());
        assertEquals("第二学期", notifications.get(0).getSemester());
        System.out.println("动态查询到通知数量: " + notifications.size());
    }

    @Test
    public void testDeleteNotificationsByStudentId() {
        // 先插入测试数据
        CourseSelectionNotification notification = createTestNotification(1005, "孙七", "20230005");
        notificationDao.insertNotification(notification);

        // 删除通知
        int result = notificationDao.deleteNotificationsByStudentId(1005);
        
        // 验证删除结果
        assertTrue(result > 0);
        
        // 验证删除后查询结果为空
        List<CourseSelectionNotification> notifications = 
            notificationDao.getNotificationsByStudentId(1005, 10);
        assertTrue(notifications.isEmpty());
        System.out.println("删除学生1005的通知数量: " + result);
    }

    /**
     * 创建测试通知
     */
    private CourseSelectionNotification createTestNotification(int studentId, String name, String number) {
        CourseSelectionNotification notification = new CourseSelectionNotification();
        notification.setStudentId(studentId);
        notification.setStudentName(name);
        notification.setStudentNumber(number);
        notification.setType(CourseSelectionNotification.NotificationType.COURSE_SELECTION_SUCCESS);
        notification.setTitle("测试通知");
        notification.setMessage("这是一条测试通知");
        notification.setCourseInfo("测试课程(TEST001)");
        notification.setAcademicYear("2023-2024");
        notification.setSemester("第一学期");
        notification.setTimestamp(LocalDateTime.now());
        return notification;
    }
} 