<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>学生选课管理系统</title>
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
        
        /* 搜索和分页样式 */
        .search-section {
            background: white;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .search-form {
            display: flex;
            gap: 10px;
            align-items: center;
            flex-wrap: wrap;
        }
        
        .search-input {
            flex: 1;
            min-width: 300px;
            padding: 12px 16px;
            border: 2px solid #e9ecef;
            border-radius: 6px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        
        .search-input:focus {
            outline: none;
            border-color: #007bff;
            box-shadow: 0 0 0 3px rgba(0,123,255,0.1);
        }
        
        .search-btn {
            padding: 12px 24px;
            background: #007bff;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-weight: 600;
            transition: background-color 0.3s;
        }
        
        .search-btn:hover {
            background: #0056b3;
        }
        
        .clear-btn {
            padding: 12px 20px;
            background: #6c757d;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-weight: 600;
            text-decoration: none;
            transition: background-color 0.3s;
        }
        
        .clear-btn:hover {
            background: #545b62;
        }
        
        .results-info {
            text-align: center;
            margin: 15px 0;
            padding: 10px;
            background: #f8f9fa;
            border-radius: 6px;
            color: #495057;
        }
        
        /* 分页样式 */
        .pagination-section {
            text-align: center;
            margin-top: 30px;
            padding: 20px;
        }
        
        .pagination {
            display: inline-flex;
            gap: 5px;
            align-items: center;
        }
        
        .pagination a, .pagination span {
            padding: 10px 15px;
            border: 1px solid #dee2e6;
            color: #007bff;
            text-decoration: none;
            border-radius: 4px;
            transition: all 0.3s;
        }
        
        .pagination a:hover {
            background: #e9ecef;
        }
        
        .pagination .current {
            background: #007bff;
            color: white;
            border-color: #007bff;
        }
        
        .pagination .disabled {
            color: #6c757d;
            cursor: not-allowed;
        }
        
        .pagination .disabled:hover {
            background: transparent;
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="header-content">
            <h1>学生选课管理系统</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/logs">日志管理</a>
                <a href="${pageContext.request.contextPath}/admin/dashboard">返回仪表板</a>
                <a href="${pageContext.request.contextPath}/logout">登出</a>
            </div>
        </div>
    </div>

    <div class="container">
        
        <!-- 消息提示 -->
        <c:if test="${not empty message}">
            <div class="alert alert-info">${message}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        
        <!-- 搜索区域 -->
        <div class="search-section">
            <h3>学生选课信息列表</h3>
            <form class="search-form" method="get" action="${pageContext.request.contextPath}/courseSelection/admin/list">
                <input type="text" 
                       name="keyword" 
                       class="search-input" 
                       placeholder="请输入学号或姓名进行搜索..." 
                       value="${keyword}">
                <button type="submit" class="search-btn">🔍 搜索</button>
                <c:if test="${not empty keyword}">
                    <a href="${pageContext.request.contextPath}/courseSelection/admin/list" class="clear-btn">清除搜索</a>
                </c:if>
            </form>
        </div>

        <!-- 搜索结果信息 -->
        <c:if test="${hasResults}">
            <div class="results-info">
                <c:choose>
                    <c:when test="${not empty keyword}">
                        搜索结果：共找到 ${totalCount} 个学生，当前显示第 ${currentPage} 页
                    </c:when>
                    <c:otherwise>
                        共有 ${totalCount} 个学生，当前显示第 ${currentPage} 页
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>
        <c:if test="${empty students}">
            <p class="no-data">暂无学生选课信息</p>
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
                            <td>${student.student_number}</td>
                            <td>${student.student_name}</td>
                            <td>${student.college}</td>
                            <td> <fmt:formatDate value="${student.enrollment_date}" pattern="yyyy-MM-dd"/></td>
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
        
        <!-- 分页功能 -->
        <c:if test="${hasResults && totalPages > 1}">
            <div class="pagination-section">
                <div class="pagination">
                    <c:choose>
                        <c:when test="${currentPage > 1}">
                            <c:choose>
                                <c:when test="${not empty keyword}">
                                    <a href="${pageContext.request.contextPath}/courseSelection/admin/list?keyword=${keyword}&page=${currentPage - 1}">‹ 上一页</a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/courseSelection/admin/list?page=${currentPage - 1}">‹ 上一页</a>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <span class="disabled">‹ 上一页</span>
                        </c:otherwise>
                    </c:choose>
                    
                    <c:forEach begin="1" end="${totalPages}" var="pageNum">
                        <c:choose>
                            <c:when test="${pageNum == currentPage}">
                                <span class="current">${pageNum}</span>
                            </c:when>
                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${not empty keyword}">
                                        <a href="${pageContext.request.contextPath}/courseSelection/admin/list?keyword=${keyword}&page=${pageNum}">${pageNum}</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${pageContext.request.contextPath}/courseSelection/admin/list?page=${pageNum}">${pageNum}</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    
                    <c:choose>
                        <c:when test="${currentPage < totalPages}">
                            <c:choose>
                                <c:when test="${not empty keyword}">
                                    <a href="${pageContext.request.contextPath}/courseSelection/admin/list?keyword=${keyword}&page=${currentPage + 1}">下一页 ›</a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/courseSelection/admin/list?page=${currentPage + 1}">下一页 ›</a>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <span class="disabled">下一页 ›</span>
                        </c:otherwise>
                    </c:choose>
                </div>
                
                <div style="margin-top: 10px; color: #6c757d; font-size: 14px;">
                    第 ${currentPage} 页，共 ${totalPages} 页，每页显示 7 条记录
                </div>
            </div>
        </c:if>
    </div>
</body>
</html> 