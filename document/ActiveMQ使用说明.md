# ActiveMQ实时通知系统使用说明

## 功能概述

本系统使用ActiveMQ中间件实现了学生选课管理模块中的实时通知功能，包括：

1. **选课成功通知** - 当学生成功选课时发送
2. **选课失败通知** - 当选课操作失败时发送
3. **课程变更通知** - 当管理员修改课程信息时发送
4. **课程删除通知** - 当管理员删除课程时发送

## 启动和使用

### 1. 启动应用

```bash
mvn spring-boot:run
```

或者运行主类：`liu.CourseSelectionSystemApplication`

### 2. 访问系统

- 主页：http://localhost:8080/CourseSelectionSystem_war
- 实时通知页面：http://localhost:8080/CourseSelectionSystem_war/notifications

### 3. 测试通知功能

访问以下测试端点来验证通知功能：

- 选课成功通知：http://localhost:8080/CourseSelectionSystem_war/test/notification/success
- 选课失败通知：http://localhost:8080/CourseSelectionSystem_war/test/notification/failed
- 课程变更通知：http://localhost:8080/CourseSelectionSystem_war/test/notification/change
- 课程删除通知：http://localhost:8080/CourseSelectionSystem_war/test/notification/delete
- 队列消息测试：http://localhost:8080/CourseSelectionSystem_war/test/notification/queue

## 使用步骤

### 1. 查看实时通知

1. 打开浏览器访问通知页面
2. 页面会自动连接WebSocket服务
3. 连接状态显示在右上角（绿色=已连接，红色=断开，黄色=连接中）

### 2. 触发通知

#### 方法一：正常业务操作
1. 登录系统（管理员或普通用户）
2. 进行选课操作：
   - 添加选课记录
   - 修改课程信息
   - 删除课程
3. 观察通知页面的实时消息

#### 方法二：使用测试端点
1. 在新标签页访问测试端点
2. 返回通知页面查看实时消息

### 3. 通知类型说明

- **绿色通知**：选课成功
- **红色通知**：选课失败
- **黄色通知**：课程变更
- **蓝色通知**：课程删除

## 技术特点

### 1. 实时性
- 使用WebSocket技术实现毫秒级实时推送
- 内嵌ActiveMQ Broker，减少网络延迟

### 2. 可靠性
- JMS持久化消息，确保消息不丢失
- 自动重连机制，网络断开后自动恢复
- 消息重试机制，处理失败自动重试

### 3. 用户体验
- 美观的通知界面设计
- 连接状态实时显示
- 支持消息清理和自动滚动
- 响应式设计，支持移动端

## 配置说明

### ActiveMQ配置（application.yml）

```yaml
spring:
  activemq:
    broker-url: tcp://localhost:61616  # ActiveMQ服务地址
    user: admin                        # 用户名
    password: admin                    # 密码
    in-memory: true                    # 使用内嵌Broker
    pool:
      enabled: true                    # 启用连接池
      max-connections: 50              # 最大连接数
      idle-timeout: 30s                # 空闲超时
```

### 消息队列配置

- **队列名称**：`course.selection.queue`
- **主题名称**：`course.selection.topic`
- **消息格式**：JSON
- **持久化**：启用

## 故障排除

### 1. 连接失败
- 检查ActiveMQ服务是否启动
- 确认端口61616未被占用
- 查看应用日志中的错误信息

### 2. 消息未接收
- 检查WebSocket连接状态
- 确认浏览器支持WebSocket
- 查看浏览器控制台错误信息

### 3. 通知不显示
- 刷新通知页面
- 检查网络连接
- 确认JavaScript未被禁用

## 监控和维护

### 1. 日志监控
应用日志会记录以下信息：
- 消息发送状态
- 连接状态变化
- 错误和异常信息

### 2. 性能监控
- 消息处理延迟
- 连接数统计
- 内存使用情况

### 3. ActiveMQ监控
- 队列消息数量
- 消费者连接状态
- 消息处理速率

## 扩展功能

### 1. 添加新通知类型
1. 在`CourseSelectionNotification.NotificationType`枚举中添加新类型
2. 在`NotificationService`中添加对应方法
3. 在业务逻辑中调用通知服务

### 2. 自定义通知样式
1. 修改`notifications/index.jsp`中的CSS样式
2. 在`getNotificationClass()`方法中添加新样式映射
3. 在`getNotificationIcon()`方法中添加新图标

### 3. 消息持久化
1. 配置数据库存储消息历史
2. 添加消息查询和管理功能
3. 实现消息统计和分析

## 注意事项

1. **生产环境部署**：建议使用独立的ActiveMQ服务器
2. **安全配置**：配置适当的用户认证和权限控制
3. **性能优化**：根据实际负载调整连接池和队列参数
4. **监控告警**：设置消息积压和连接异常告警
5. **备份恢复**：定期备份ActiveMQ数据和配置

## 联系支持

如有问题或建议，请联系开发团队或查看项目文档。 