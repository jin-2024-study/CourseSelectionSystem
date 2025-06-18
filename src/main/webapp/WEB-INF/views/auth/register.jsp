<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>用户注册 - 选课系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/form.css">
    <style>
        .register-container {
            max-width: 450px;
            margin: 50px auto;
            padding: 30px;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .register-header {
            text-align: center;
            margin-bottom: 30px;
            color: #333;
        }
        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #555;
        }
        .form-group input, .form-group select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }
        .btn-register {
            width: 100%;
            padding: 12px;
            background: #28a745;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            cursor: pointer;
        }
        .btn-register:hover {
            background: #218838;
        }
        .login-link {
            text-align: center;
            margin-top: 20px;
        }
        .login-link a {
            color: #007bff;
            text-decoration: none;
        }
        .alert {
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 4px;
        }
        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
    </style>
</head>
<body>
    <div class="register-container">
        <div class="register-header">
            <h2>用户注册</h2>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-error">
                ${error}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/register" method="post">
            <div class="form-group">
                <label for="username">用户名:</label>
                <input type="text" id="username" name="username" required 
                       placeholder="请输入用户名">
            </div>

            <div class="form-group">
                <label for="email">邮箱:</label>
                <input type="email" id="email" name="email" required 
                       placeholder="请输入邮箱地址">
            </div>

            <div class="form-group">
                <label for="password">密码:</label>
                <input type="password" id="password" name="password" required 
                       placeholder="请输入密码">
            </div>

            <div class="form-group">
                <label for="role">角色:</label>
                <select id="role" name="role">
                    <option value="USER">普通用户</option>
                    <option value="ADMIN">管理员</option>
                </select>
            </div>

            <button type="submit" class="btn-register">注册</button>
        </form>

        <div class="login-link">
            <p>已有账号? <a href="${pageContext.request.contextPath}/login">立即登录</a></p>
        </div>
    </div>
</body>
</html> 