# ActiveMQ中间件实现学生选课实时通知系统

## 概述

本项目使用ActiveMQ中间件实现了学生选课管理模块中的实时通知功能，包括选课成功/失败/课程变更的实时通知。

## 技术架构

### 1. 核心组件

- **ActiveMQ Broker**: 内嵌式消息代理，端口61616
- **JMS消息队列**: 用于可靠消息传递
- **WebSocket**: 用于前端实时通知展示
- **Spring Boot**: 整合ActiveMQ和WebSocket

### 2. 消息流程

```
业务操作 → 发送消息到ActiveMQ → 消息监听器处理 → WebSocket推送 → 前端实时显示
```

## 实现细节

### 1. 依赖配置 (pom.xml)

```xml
<!-- ActiveMQ 相关依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-activemq</artifactId>
</dependency>

<!-- ActiveMQ 内嵌Broker 依赖 -->
<dependency>
    <groupId>org.apache.activemq</groupId>
    <artifactId>activemq-broker</artifactId>
</dependency>

<!-- WebSocket支持 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

### 2. ActiveMQ配置 (ActiveMQConfig.java)

- 配置内嵌Broker服务器
- 设置连接工厂和重试策略
- 定义消息队列和主题
- 配置JSON消息转换器

### 3. WebSocket配置 (WebSocketConfig.java)

- 启用STOMP协议
- 配置消息代理端点
- 设置客户端连接端点

### 4. 通知实体类 (CourseSelectionNotification.java)

```java
public class CourseSelectionNotification {
    public enum NotificationType {
        COURSE_SELECTION_SUCCESS("选课成功"),
        COURSE_SELECTION_FAILED("选课失败"),
        COURSE_CHANGE("课程变更"),
        COURSE_DELETION("课程删除");
    }
    
    private Integer studentId;
    private String studentName;
    private NotificationType type;
    private String title;
    private String message;
    private String courseInfo;
    private LocalDateTime timestamp;
    // ... 其他属性
}
```

### 5. 通知服务 (NotificationService)

#### 接口定义
- `sendCourseSelectionSuccessNotification()`: 发送选课成功通知
- `sendCourseSelectionFailedNotification()`: 发送选课失败通知
- `sendCourseChangeNotification()`: 发送课程变更通知
- `sendCourseDeletionNotification()`: 发送课程删除通知

#### 实现特点
- 同时发送到队列和主题
- 集成WebSocket实时推送
- 完善的异常处理和日志记录

### 6. 消息监听器 (CourseSelectionNotificationListener.java)

```java
@Component
public class CourseSelectionNotificationListener {
    
    @JmsListener(destination = "course.selection.queue")
    public void handleQueueNotification(CourseSelectionNotification notification) {
        // 处理队列消息
        processNotification(notification);
        forwardToWebSocket(notification);
    }
    
    @JmsListener(destination = "course.selection.topic")
    public void handleTopicNotification(CourseSelectionNotification notification) {
        // 处理主题消息
        processNotification(notification);
    }
}
```

### 7. 业务集成

在`CourseSelectionController`中集成通知功能：

#### 选课成功通知
```java
// 选课成功后发送通知
if (student != null && !selectedCourses.isEmpty()) {
    notificationService.sendCourseSelectionSuccessNotification(
        student, selectedCourses, academicYear, semester);
}
```

#### 选课失败通知
```java
// 选课失败时发送通知
if (student != null) {
    notificationService.sendCourseSelectionFailedNotification(
        student, "失败原因", academicYear, semester);
}
```

#### 课程变更通知
```java
// 课程信息更新后发送通知
if (student != null && courseSelection != null) {
    notificationService.sendCourseChangeNotification(
        student, course, changeDetails, 
        courseSelection.getAcademicYear(), courseSelection.getSemester());
}
```

#### 课程删除通知
```java
// 课程删除后发送通知
if (student != null && course != null) {
    notificationService.sendCourseDeletionNotification(
        student, course, academicYear, semester);
}
```

## 前端实时通知

### 1. 通知页面 (notifications/index.jsp)

- 使用SockJS和STOMP.js连接WebSocket
- 实时接收和显示通知消息
- 支持不同类型通知的样式区分
- 提供连接状态指示和消息管理功能

### 2. WebSocket连接

```javascript
// 连接WebSocket
const socket = new SockJS('/CourseSelectionSystem_war/ws-notifications');
stompClient = Stomp.over(socket);

// 订阅通知主题
stompClient.subscribe('/topic/notifications', function(notification) {
    const notificationData = JSON.parse(notification.body);
    displayNotification(notificationData);
});
```

### 3. 通知展示

- 成功通知：绿色边框，成功图标
- 失败通知：红色边框，错误图标
- 变更通知：黄色边框，编辑图标
- 删除通知：蓝色边框，删除图标

## 配置说明

### application.yml配置

```yaml
spring:
  # ActiveMQ配置
  activemq:
    broker-url: tcp://localhost:61616
    user: admin
    password: admin
    in-memory: true
    pool:
      enabled: true
      max-connections: 50
      idle-timeout: 30s
  
  # JMS配置
  jms:
    template:
      default-destination: course.selection.queue
      delivery-mode: persistent
      priority: 100
      qos-enabled: true
      receive-timeout: 1000ms
      time-to-live: 36000000ms
```

## 测试功能

### 测试端点

提供了测试控制器`TestNotificationController`，包含以下测试端点：

- `/test/notification/success`: 测试选课成功通知
- `/test/notification/failed`: 测试选课失败通知
- `/test/notification/change`: 测试课程变更通知
- `/test/notification/delete`: 测试课程删除通知
- `/test/notification/queue`: 测试直接发送队列消息

### 使用方法

1. 启动应用程序
2. 访问通知页面：`/notifications`
3. 在另一个浏览器标签页访问测试端点
4. 观察通知页面的实时消息显示

## 功能特点

### 1. 可靠性
- 使用JMS持久化消息，确保消息不丢失
- 配置消息重试机制
- 完善的异常处理

### 2. 实时性
- WebSocket实时推送
- 内嵌ActiveMQ Broker，减少网络延迟
- 高效的消息处理

### 3. 扩展性
- 支持队列和主题两种消息模式
- 易于添加新的通知类型
- 模块化设计，便于维护

### 4. 用户体验
- 美观的通知界面
- 连接状态指示
- 消息分类和过滤
- 自动滚动和手动清理

## 部署说明

### 1. 开发环境
- 使用内嵌ActiveMQ Broker
- 无需额外安装ActiveMQ服务

### 2. 生产环境
- 建议使用独立的ActiveMQ服务器
- 修改broker-url配置指向生产环境ActiveMQ
- 配置适当的用户认证和权限

### 3. 监控
- 可通过ActiveMQ Web控制台监控消息队列状态
- 应用日志记录详细的消息处理信息

## 总结

本实现成功集成了ActiveMQ中间件，实现了学生选课管理模块的实时通知功能。通过消息队列确保了通知的可靠传递，通过WebSocket实现了前端的实时展示，为用户提供了良好的交互体验。整个系统具有良好的可扩展性和可维护性，可以方便地添加新的通知类型和功能。 