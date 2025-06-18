<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>500 - 服务器内部错误</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
            margin: 0;
            padding: 50px;
            text-align: center;
        }
        .error-container {
            background: white;
            border-radius: 8px;
            padding: 40px;
            max-width: 600px;
            margin: 0 auto;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .error-code {
            font-size: 72px;
            font-weight: bold;
            color: #dc3545;
            margin-bottom: 20px;
        }
        .error-title {
            font-size: 24px;
            margin-bottom: 15px;
            color: #333;
        }
        .error-message {
            font-size: 16px;
            color: #666;
            margin-bottom: 30px;
        }
        .btn {
            display: inline-block;
            padding: 12px 30px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            margin: 5px;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .error-details {
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 4px;
            padding: 15px;
            margin-top: 20px;
            text-align: left;
            font-family: monospace;
            font-size: 12px;
            color: #495057;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-code">500</div>
        <div class="error-title">服务器内部错误</div>
        <div class="error-message">
            很抱歉，服务器遇到了一个意外错误，无法完成您的请求。<br>
            我们的技术团队已经收到此问题的通知，并正在努力解决。
        </div>
        
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn">返回仪表板</a>
        <a href="${pageContext.request.contextPath}/students" class="btn">学生管理</a>
        
        <% if (exception != null) { %>
        <div class="error-details">
            <strong>错误详情:</strong><br>
            <%= exception.getClass().getSimpleName() %>: <%= exception.getMessage() %>
        </div>
        <% } %>
    </div>
</body>
</html> 
            border-radius: 4px;
            padding: 15px;
            margin-top: 20px;
            text-align: left;
            font-family: monospace;
            font-size: 12px;
            color: #495057;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-code">500</div>
        <div class="error-title">服务器内部错误</div>
        <div class="error-message">
            很抱歉，服务器遇到了一个意外错误，无法完成您的请求。<br>
            我们的技术团队已经收到此问题的通知，并正在努力解决。
        </div>
        
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn">返回仪表板</a>
        <a href="${pageContext.request.contextPath}/students" class="btn">学生管理</a>
        
        <% if (exception != null) { %>
        <div class="error-details">
            <strong>错误详情:</strong><br>
            <%= exception.getClass().getSimpleName() %>: <%= exception.getMessage() %>
        </div>
        <% } %>
    </div>
</body>
</html> 