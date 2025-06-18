<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>系统错误</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            text-align: center;
        }
        .error-container {
            margin: 50px auto;
            max-width: 600px;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #e74c3c;
        }
        .error-details {
            margin: 20px 0;
            color: #555;
        }
        .error-message {
            background-color: #f8f8f8;
            padding: 10px;
            border-radius: 5px;
            margin: 20px 0;
            text-align: left;
            font-family: monospace;
        }
        .back-link {
            display: inline-block;
            margin-top: 20px;
            padding: 10px 20px;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }
        .back-link:hover {
            background-color: #2980b9;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <h1>系统错误</h1>
        <div class="error-details">
            <p>很抱歉，系统遇到了一个错误，无法完成您的请求。</p>
        </div>
        
        <c:if test="${not empty timestamp}">
            <div class="error-message">
                <strong>时间:</strong> ${timestamp}<br>
                <strong>状态:</strong> ${status}<br>
                <strong>错误:</strong> ${error}<br>
                <strong>消息:</strong> ${message}<br>
                <strong>路径:</strong> ${path}<br>
            </div>
        </c:if>
        
        <c:url var="homeUrl" value="/courseSelection" />
        <a href="${homeUrl}" class="back-link">返回主页</a>
    </div>
</body>
</html> 