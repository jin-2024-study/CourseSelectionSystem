<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>页面未找到 - 404</title>
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
        <h1>404 - 页面未找到</h1>
        <div class="error-details">
            <p>很抱歉，您请求的页面不存在或已被移除。</p>
            <p>请检查您输入的URL是否正确。</p>
        </div>
        <c:url var="homeUrl" value="/courseSelection" />
        <a href="${homeUrl}" class="back-link">返回主页</a>
    </div>
</body>
</html> 