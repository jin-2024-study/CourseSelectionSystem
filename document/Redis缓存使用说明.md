# Redis缓存功能使用说明

## 概述

本项目已为日志查询功能集成了Redis缓存，以提高查询性能和用户体验。

## 功能特性

### 1. 缓存策略

- **分页查询缓存**：缓存时间10分钟
- **条件查询缓存**：缓存时间15分钟  
- **统计信息缓存**：缓存时间5分钟
- **单条记录查询缓存**：缓存时间30分钟
- **目标查询缓存**：缓存时间20分钟

### 2. 缓存键设计

- `operation_log:` - 单条日志记录缓存前缀
- `operation_log_page:` - 分页查询缓存前缀
- `operation_log_condition:` - 条件查询缓存前缀
- `operation_log_statistics` - 统计信息缓存键

### 3. 自动缓存清理

当进行以下操作时，相关缓存会自动清除：
- 添加新日志记录
- 删除日志记录
- 清理过期日志

## 配置说明

### 1. 依赖配置 (pom.xml)

```xml
<!-- Redis 缓存依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- Lettuce 连接池依赖 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
```

### 2. Redis配置 (application.yml)

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: # 如果有密码请填写
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 20
          max-wait: -1ms
          max-idle: 10
          min-idle: 0
```

## 核心组件

### 1. RedisConfig.java
配置Redis连接、序列化器和缓存管理器。

### 2. OperationLogServiceImpl.java
带缓存功能的日志服务实现类，使用Spring Cache注解和手动缓存管理。

### 3. RedisUtils.java
Redis工具类，提供常用的缓存操作方法。

## 使用方法

### 1. 查看缓存状态
在日志管理页面点击"查看缓存状态"按钮，可以查看：
- 分页缓存是否存在
- 统计信息缓存是否存在

### 2. 清除缓存
点击"清除缓存"按钮可以手动清除所有日志相关的缓存。

### 3. 刷新页面
点击"刷新页面"按钮重新加载页面数据。

## API接口

### 缓存管理接口

- `GET /logs/cache/info` - 获取缓存状态信息
- `GET /logs/cache/clear` - 清除日志相关缓存

### 响应格式

```json
{
    "success": true,
    "message": "操作成功",
    "hasPageCache": true,
    "hasStatisticsCache": false
}
```

## 性能优化

### 1. 缓存命中率
- 频繁查询的分页数据会被缓存
- 统计信息会被缓存避免重复计算
- 条件查询结果会被缓存

### 2. 内存管理
- 缓存设置了合理的过期时间
- 支持手动清理缓存
- 数据变更时自动清除相关缓存

## 监控和调试

### 1. 日志记录
缓存操作会在控制台输出相关日志信息。

### 2. 错误处理
缓存操作失败不会影响主业务逻辑，会降级到直接查询数据库。

### 3. 缓存状态检查
通过Web界面可以实时查看缓存状态。

## 注意事项

### 1. Redis服务要求
- 确保Redis服务正常运行
- 建议使用Redis 5.0以上版本

### 2. 内存使用
- 监控Redis内存使用情况
- 根据数据量调整缓存策略

### 3. 数据一致性
- 缓存会在数据变更时自动清除
- 如有数据不一致可手动清除缓存

## 扩展功能

### 1. 缓存统计
可以扩展添加缓存命中率统计功能。

### 2. 缓存预热
可以在系统启动时预加载热点数据。

### 3. 分布式缓存
支持Redis集群部署实现分布式缓存。

## 故障排除

### 1. Redis连接失败
检查Redis服务状态和配置信息。

### 2. 缓存不生效
确认@EnableCaching注解已启用，检查方法上的缓存注解。

### 3. 内存泄漏
定期清理过期缓存，监控Redis内存使用。 