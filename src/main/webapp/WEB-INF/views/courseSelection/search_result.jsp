<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>搜索结果 - 学生选课管理系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/list.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/alert.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/buttons.css">
    <style>
        body {
            background-color: #f5f5f5;
            font-family: 'Microsoft YaHei', Arial, sans-serif;
            margin: 0;
            padding: 0;
        }
        
        .header {
            background: linear-gradient(135deg, #007bff, #0056b3);
            color: white;
            padding: 20px 0;
            margin-bottom: 30px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .header-content {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .header h1 {
            margin: 0;
            font-size: 28px;
            font-weight: 300;
        }
        
        .nav-links a {
            color: white;
            text-decoration: none;
            margin-left: 20px;
            padding: 8px 16px;
            border-radius: 4px;
            transition: background-color 0.3s;
        }
        
        .nav-links a:hover {
            background-color: rgba(255,255,255,0.2);
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 20px;
        }
        
        .search-results {
            margin-top: 20px;
        }
        
        .search-keyword {
            font-weight: bold;
            color: #e91e63;
        }
        
        .no-data {
            padding: 20px;
            background-color: #f9f9f9;
            border-radius: 5px;
        }
        
        .highlight {
            background-color: #fff3cd;
            padding: 2px 4px;
            border-radius: 3px;
        }
        
        .back-link {
            margin-bottom: 20px;
        }
        
        .back-link a {
            color: #007bff;
            text-decoration: none;
            font-size: 14px;
        }
        
        .back-link a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="header-content">
            <h1>搜索结果 - 学生选课管理</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/courseSelection">返回列表</a>
                <a href="${pageContext.request.contextPath}/admin/dashboard">返回仪表板</a>
                <a href="${pageContext.request.contextPath}/logout">登出</a>
            </div>
        </div>
    </div>

    <div class="container">
        <!-- 搜索表单 -->
        <div class="search-form">
            <form action="${pageContext.request.contextPath}/courseSelection/search" method="get">
                <input type="text" name="keyword" value="${keyword}" placeholder="请输入学号或姓名" required>
                <button type="submit">搜索</button>
            </form>
        </div>
        
        <!-- 搜索结果 -->
        <div class="search-results">
            <h2>搜索结果：<span class="search-keyword">${keyword}</span></h2>
            
            <c:if test="${empty students}">
                <div class="no-data">
                    <p>未找到匹配的学生信息</p>
                </div>
            </c:if>
            
            <c:if test="${not empty students}">
                <table>
                    <thead>
                        <tr>
                            <th>学号</th>
                            <th>姓名</th>
                            <th>学院</th>
                            <th>入学日期</th>
                            <th>选课数量</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${students}" var="student">
                            <tr>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty keyword && student.student_number.contains(keyword)}">
                                            ${student.student_number.substring(0, student.student_number.indexOf(keyword))}<span class="highlight">${keyword}</span>${student.student_number.substring(student.student_number.indexOf(keyword) + keyword.length())}
                                        </c:when>
                                        <c:otherwise>
                                            ${student.student_number}
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty keyword && student.student_name.contains(keyword)}">
                                            ${student.student_name.substring(0, student.student_name.indexOf(keyword))}<span class="highlight">${keyword}</span>${student.student_name.substring(student.student_name.indexOf(keyword) + keyword.length())}
                                        </c:when>
                                        <c:otherwise>
                                            ${student.student_name}
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${student.college}</td>
                                <td><fmt:formatDate value="${student.enrollment_date}" pattern="yyyy-MM-dd"/></td>
                                <td>
                                    <c:set var="totalCourses" value="0"/>
                                    <c:forEach items="${student.courseSelections}" var="selection">
                                        <c:if test="${selection.courseSelectionLists != null}">
                                            <c:set var="totalCourses" value="${totalCourses + selection.courseSelectionLists.size()}"/>
                                        </c:if>
                                    </c:forEach>
                                    ${totalCourses}
                                </td>
                                <td class="action-links">
                                    <a href="${pageContext.request.contextPath}/courseSelection/students/${student.student_id}">查看选课详情</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </div>
    </div>
</body>
</html> 