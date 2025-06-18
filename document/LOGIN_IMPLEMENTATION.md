# 自定义登录功能实现说明

## 概述

在原有的Spring Security自动登录基础上，新增了自定义的 `PostMapping("/login")` 方法，提供更灵活的登录控制和额外功能。

## 实现的功能

### 1. 自定义登录验证
- **输入验证**: 检查用户名和密码是否为空
- **认证处理**: 使用Spring Security的AuthenticationManager进行用户认证
- **安全上下文**: 认证成功后自动设置SecurityContext

### 2. 增强的用户体验
- **详细错误信息**: 提供具体的错误提示（用户名为空、密码为空、认证失败等）
- **角色重定向**: 根据用户角色自动重定向到相应页面
  - 管理员用户 → `/admin/dashboard`
  - 普通用户 → `/courseSelection`

### 3. 安全功能
- **IP地址记录**: 记录用户登录的IP地址
- **登录日志**: 控制台输出登录成功/失败的日志信息
- **认证详情**: 设置Web认证详情，包含请求信息

### 4. 异常处理
- **认证异常**: 捕获并处理认证失败的情况
- **系统异常**: 处理其他可能的系统异常
- **友好提示**: 为用户提供友好的错误信息

## 代码结构

### AuthController中的新方法
```java
@PostMapping("/login")
public String login(@RequestParam String username,
                   @RequestParam String password,
                   HttpServletRequest request,
                   RedirectAttributes redirectAttributes)
```

### SecurityConfig的配置修改
- 暴露了`AuthenticationManager` bean
- 修改了Spring Security的默认登录处理URL为 `/perform_login`
- 保持自定义登录方法使用 `/login` URL

## 使用方式

### 前端表单
登录表单继续提交到 `/login` 路径：
```html
<form action="${pageContext.request.contextPath}/login" method="post">
    <input type="text" name="username" required>
    <input type="password" name="password" required>
    <button type="submit">登录</button>
</form>
```

### 错误处理
登录失败时，错误信息会通过 `RedirectAttributes` 传递给前端页面显示。

## 日志输出

系统会在控制台输出登录相关的日志信息：
- 登录成功: `用户登录成功: [用户名], IP: [IP地址]`
- 登录失败: `用户登录失败: [用户名], 原因: [失败原因]`
- 系统异常: `登录过程中发生异常: [异常信息]`

## 兼容性

这个实现与现有的Spring Security配置完全兼容，不会影响其他认证和授权功能：
- 用户注册功能正常
- 角色权限控制正常
- 会话管理正常
- 登出功能正常

## 扩展建议

可以根据需要进一步扩展以下功能：
1. **登录尝试限制**: 添加登录失败次数限制
2. **验证码功能**: 多次失败后要求输入验证码
3. **登录历史**: 将登录记录保存到数据库
4. **双因素认证**: 添加短信或邮箱验证
5. **记住我功能**: 实现自动登录功能 