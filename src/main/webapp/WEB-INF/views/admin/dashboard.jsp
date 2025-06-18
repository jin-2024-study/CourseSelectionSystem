<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>
        <c:choose>
            <c:when test="${isAdmin}">管理员仪表</c:when>
            <c:otherwise>用户仪表</c:otherwise>
        </c:choose>
        - 选课系统
    </title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/common.css">
    <style>
        .dashboard-header {
            background: #007bff;
            color: white;
            padding: 20px;
            margin-bottom: 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .dashboard-header h1 {
            margin: 0;
            font-size: 28px;
        }
        .user-info {
            font-size: 14px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .username {
            color: #ffd700;
            font-weight: bold;
            margin: 0 5px;
        }
        .quick-actions {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }
        .action-buttons {
            display: flex;
            gap: 15px;
            flex-wrap: wrap;
        }
        .btn-action {
            padding: 12px 24px;
            background: #28a745;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            font-size: 14px;
        }
        .btn-action:hover {
            background: #218838;
        }
        .btn-logout {
            background: #dc3545;
            padding: 8px 16px;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            font-size: 14px;
        }
        .btn-logout:hover {
            background: #c82333;
        }
        
        /* 普通用户样式调整 */
        .user-dashboard {
            text-align: center;
            padding: 40px 20px;
        }
        .user-dashboard .btn-action {
            padding: 20px 40px;
            font-size: 18px;
            min-width: 300px;
            display: inline-block;
        }
        .welcome-message {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 30px;
            color: #495057;
        }
    </style>
</head>
<body>
    <div class="dashboard-header">
        <h1>
            <c:choose>
                <c:when test="${isAdmin}">管理员仪表</c:when>
                <c:otherwise>用户仪表</c:otherwise>
            </c:choose>
        </h1>
        <div class="user-info">
            <span>欢迎，
                <c:choose>
                    <c:when test="${isAdmin}">管理员</c:when>
                    <c:otherwise>用户</c:otherwise>
                </c:choose>
            </span>
            <span class="username">${currentUsername}</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">登出</a>
        </div>
    </div>

    <div class="container">
        <!-- 管理员功能 -->
        <c:if test="${isAdmin}">
            <div class="quick-actions">
                <h3>快速操作</h3>
                <div class="action-buttons" style="justify-content: center;">
                    <a href="${pageContext.request.contextPath}/courseSelection" class="btn-action" style="background: #007bff; padding: 15px 30px; font-size: 16px; min-width: 200px; text-align: center;">学生选课管理</a>
                    <a href="${pageContext.request.contextPath}/students" class="btn-action" style="background: #28a745; padding: 15px 30px; font-size: 16px; min-width: 200px; text-align: center;">学生信息管理</a>
                    <a href="${pageContext.request.contextPath}/logs" class="btn-action" style="background: #6c757d; padding: 15px 30px; font-size: 16px; min-width: 200px; text-align: center;">日志记录管理</a>
                </div>
            </div>
        </c:if>

        <!-- 普通用户功能 -->
        <c:if test="${not isAdmin}">
            <div class="user-dashboard">
                <h3>功能模块</h3>
                <div class="action-buttons" style="justify-content: center;">
                    <a href="${pageContext.request.contextPath}/courseSelection" class="btn-action" style="background: #007bff;">学生选课管理</a>
                </div>
            </div>
        </c:if>
    </div>
</body>
</html> 