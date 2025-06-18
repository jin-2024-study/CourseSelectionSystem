<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>访问拒绝 - 选课系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/common.css">
    <style>
        .access-denied-container {
            max-width: 600px;
            margin: 100px auto;
            padding: 40px;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            text-align: center;
        }
        .error-icon {
            font-size: 72px;
            color: #dc3545;
            margin-bottom: 20px;
        }
        .error-title {
            font-size: 24px;
            color: #333;
            margin-bottom: 15px;
        }
        .error-message {
            font-size: 16px;
            color: #666;
            margin-bottom: 30px;
            line-height: 1.5;
        }
        .btn-back {
            display: inline-block;
            padding: 12px 24px;
            background: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            margin-right: 10px;
        }
        .btn-back:hover {
            background: #0056b3;
        }
        .btn-logout {
            display: inline-block;
            padding: 12px 24px;
            background: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 4px;
        }
        .btn-logout:hover {
            background: #545b62;
        }
    </style>
</head>
<body>
    <div class="access-denied-container">
        <div class="error-icon">🚫</div>
        <h1 class="error-title">访问拒绝</h1>
        <p class="error-message">
            抱歉，您没有权限访问此页面。<br>
            请联系管理员获取相应权限，或使用有权限的账号登录。
        </p>
        <div>
            <a href="javascript:history.back()" class="btn-back">返回上页</a>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">重新登录</a>
        </div>
    </div>
</body>
</html> 