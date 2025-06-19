<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>日志测试页面</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>
    <h1>操作日志测试页面</h1>
    
    <p>日志数量: ${logs.size()}</p>
    
    <c:if test="${empty logs}">
        <p>没有找到日志记录</p>
    </c:if>
    
    <c:if test="${not empty logs}">
        <table>
            <tr>
                <th>ID</th>
                <th>操作类型</th>
                <th>操作模块</th>
                <th>操作用户</th>
                <th>操作时间</th>
                <th>结果</th>
            </tr>
            <c:forEach items="${logs}" var="log">
                <tr>
                    <td>${log.logId}</td>
                    <td>${log.operationType}</td>
                    <td>${log.operationModule}</td>
                    <td>${log.operationUser}</td>
                    <td>${log.formattedOperationTime}</td>
                    <td>${log.operationResult}</td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
    
    <p><a href="${pageContext.request.contextPath}/logs">返回日志列表</a></p>
</body>
</html> 



















