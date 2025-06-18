# Spring Boot Security 用户认证与权限控制实现

## 功能概述

本项目已成功集成Spring Boot Security，实现了完整的用户认证和基于角色的权限控制系统。

## 主要功能

### 1. 用户认证
- **登录功能**: 用户名/密码登录
- **注册功能**: 新用户注册
- **密码加密**: 使用BCrypt加密存储密码
- **会话管理**: 自动会话管理和超时控制

### 2. 角色权限控制
- **ADMIN角色**: 管理员，可访问所有功能
- **USER角色**: 普通用户，只能访问基本功能
- **URL级别权限控制**: 不同URL需要不同权限
- **方法级别权限控制**: 使用@PreAuthorize注解

### 3. 安全配置
- **静态资源**: CSS、JS、图片等无需认证
- **公开页面**: 登录、注册页面无需认证
- **管理员专用**: `/admin/**` 路径只有ADMIN角色可访问
- **访问拒绝处理**: 自定义访问拒绝页面

## 技术实现

### 1. 核心组件

#### 实体类
- `User.java`: 用户实体，包含用户名、密码、邮箱、角色等信息
- `Role.java`: 角色枚举，定义ADMIN和USER角色

#### 数据访问层
- `UserDao.java`: 用户数据访问接口
- `UserDao.xml`: MyBatis映射文件

#### 服务层
- `UserService.java`: 用户服务接口
- `UserServiceImpl.java`: 用户服务实现
- `CustomUserDetailsService.java`: Spring Security用户详情服务

#### 配置类
- `SecurityConfig.java`: Spring Security主配置
- `MyBatisConfig.java`: MyBatis配置，注册拦截器
- `WebMvcConfig.java`: Web MVC配置，静态资源映射
- `DataInitializer.java`: 数据初始化，创建默认用户

#### 控制器
- `AuthController.java`: 认证控制器（登录、注册、登出）
- `AdminController.java`: 管理员控制器（仅ADMIN可访问）
- `StudentController.java`: 学生控制器（普通用户）

### 2. 数据库设计

```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 3. 权限控制规则

#### URL级别权限
```java
.authorizeRequests()
    .antMatchers("/css/**", "/js/**", "/images/**").permitAll()  // 静态资源
    .antMatchers("/login", "/register").permitAll()              // 公开页面
    .antMatchers("/admin/**").hasRole("ADMIN")                   // 管理员专用
    .anyRequest().authenticated()                                // 其他需认证
```

#### 方法级别权限
```java
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    // 只有ADMIN角色可以访问这个控制器的所有方法
}
```

## 页面结构

### 认证相关页面
- `/login`: 登录页面
- `/register`: 注册页面
- `/access-denied`: 访问拒绝页面

### 管理员页面
- `/admin/dashboard`: 管理员仪表板
- `/admin/users`: 用户管理
- `/admin/users/add`: 添加用户
- `/admin/users/edit/{id}`: 编辑用户

### 普通用户页面
- `/student/dashboard`: 学生仪表板
- `/student/courses`: 选课页面

## 默认用户账号

系统启动时会自动创建以下默认用户：

1. **管理员账号**
   - 用户名: `admin`
   - 密码: `admin123`
   - 角色: ADMIN

2. **普通用户账号**
   - 用户名: `user`
   - 密码: `user123`
   - 角色: USER

## 使用说明

### 1. 启动应用
```bash
mvn spring-boot:run
```

### 2. 访问应用
- 应用地址: `http://localhost:8080/CourseSelectionSystem_war`
- 登录页面: `http://localhost:8080/CourseSelectionSystem_war/login`

### 3. 测试权限
1. 使用`admin/admin123`登录，可访问管理员功能
2. 使用`user/user123`登录，只能访问普通用户功能
3. 尝试访问`/admin/**`路径测试权限控制

### 4. 功能测试
- **用户注册**: 创建新用户账号
- **角色切换**: 不同角色登录查看不同界面
- **权限验证**: 普通用户访问管理员页面会被拒绝
- **会话管理**: 登出后需要重新登录

## 安全特性

1. **密码安全**: 使用BCrypt加密，不存储明文密码
2. **会话安全**: 自动会话管理，登出时清除会话
3. **CSRF保护**: 可配置CSRF保护（当前为简化开发已禁用）
4. **访问控制**: 严格的URL和方法级别权限控制
5. **错误处理**: 友好的错误页面和访问拒绝处理

## 扩展功能

可以进一步扩展的功能：
1. 记住我功能
2. 密码重置
3. 用户锁定/解锁
4. 登录日志记录
5. 更细粒度的权限控制
6. OAuth2集成
7. JWT令牌认证

## 注意事项

1. 生产环境建议启用CSRF保护
2. 定期更新密码加密强度
3. 配置合适的会话超时时间
4. 监控登录失败次数，防止暴力破解
5. 定期审查用户权限 