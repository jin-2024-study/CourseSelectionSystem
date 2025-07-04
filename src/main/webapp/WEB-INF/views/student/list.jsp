<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>学生信息管理系统</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #ffffff;
            min-height: 100vh;
            color: #333;
            line-height: 1.6;
        }

        .header {
            background: #007bff;
            border-bottom: 1px solid #0056b3;
            padding: 1rem 0;
        }

        .header-content {
            max-width: 1200px;
            margin: 0 auto;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0 20px;
        }

        .header h1 {
            color: white;
            font-size: 28px;
            font-weight: 700;
        }

        .nav-links a {
            color: white;
            text-decoration: none;
            margin-left: 20px;
            padding: 8px 16px;
            border-radius: 6px;
            transition: background-color 0.3s;
        }

        .nav-links a:hover {
            background: rgba(255, 255, 255, 0.2);
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 30px 20px;
        }

        .stats {
            display: flex;
            gap: 20px;
            margin-bottom: 30px;
        }

        .stat-card {
            background: white;
            padding: 20px;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            text-align: center;
            min-width: 150px;
        }

        .stat-number {
            font-size: 36px;
            font-weight: bold;
            color: #007bff;
            margin-bottom: 5px;
        }

        .stat-label {
            color: #666;
            font-size: 14px;
        }

        .results-info {
            text-align: center;
            margin: 15px 0;
            padding: 10px;
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 6px;
            color: #495057;
        }

        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 8px;
            font-weight: 500;
        }

        .alert-success {
            background: #d4edda;
            color: #155724;
            border-left: 4px solid #28a745;
        }

        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border-left: 4px solid #dc3545;
        }

        .alert-warning {
            background: #fff3cd;
            color: #856404;
            border-left: 4px solid #ffc107;
        }

        .toolbar {
            background: white;
            padding: 20px;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            margin-bottom: 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 15px;
        }

        .search-form {
            display: flex;
            gap: 10px;
            align-items: center;
        }

        .search-form input {
            padding: 12px 16px;
            border: 2px solid #e9ecef;
            border-radius: 6px;
            font-size: 14px;
            width: 300px;
            transition: border-color 0.3s;
        }

        .search-form input:focus {
            outline: none;
            border-color: #007bff;
            box-shadow: 0 0 0 3px rgba(0,123,255,0.1);
        }

        .action-buttons {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        .btn {
            padding: 12px 20px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s;
            display: inline-block;
            text-align: center;
        }

        .btn-primary {
            background: #007bff;
            color: white;
        }

        .btn-primary:hover {
            background: #0056b3;
        }

        .btn-success {
            background: #28a745;
            color: white;
        }

        .btn-success:hover {
            background: #1e7e34;
        }

        .btn-info {
            background: #17a2b8;
            color: white;
        }

        .btn-info:hover {
            background: #138496;
        }

        .btn-warning {
            background: #ffc107;
            color: #212529;
        }

        .btn-warning:hover {
            background: #e0a800;
        }

        .btn-danger {
            background: #dc3545;
            color: white;
        }

        .btn-danger:hover {
            background: #bd2130;
        }

        .btn-secondary {
            background: #6c757d;
            color: white;
        }

        .btn-secondary:hover {
            background: #545b62;
        }

        .btn-sm {
            padding: 6px 12px;
            font-size: 12px;
        }

        .content-card {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            overflow: hidden;
            margin-bottom: 30px;
        }

        .card-header {
            background: #f8f9fa;
            padding: 20px;
            font-weight: 600;
            color: #495057;
            border-bottom: 1px solid #dee2e6;
        }

        .table-container {
            overflow-x: auto;
        }

        .table {
            width: 100%;
            border-collapse: collapse;
            margin: 0;
        }

        .table th,
        .table td {
            padding: 15px 12px;
            text-align: left;
            border-bottom: 1px solid #dee2e6;
        }

        .table th {
            background: #f8f9fa;
            font-weight: 600;
            color: #495057;
            white-space: nowrap;
        }

        .table tbody tr:hover {
            background: #f8f9fa;
        }

        .student-photo {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            object-fit: cover;
            border: 2px solid #dee2e6;
        }

        .no-photo {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: #e9ecef;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #6c757d;
            font-size: 18px;
            border: 2px solid #dee2e6;
        }

        .no-data {
            text-align: center;
            padding: 60px 20px;
            color: #6c757d;
            font-size: 16px;
        }

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

        @media (max-width: 768px) {
            .toolbar {
                flex-direction: column;
                align-items: stretch;
            }

            .search-form {
                flex-direction: column;
            }

            .search-form input {
                width: 100%;
            }

            .action-buttons {
                justify-content: center;
            }

            .stats {
                flex-direction: column;
            }

            .table th,
            .table td {
                padding: 10px 8px;
            }
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="header-content">
            <h1>学生信息管理</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/admin/dashboard">返回仪表板</a>
                <a href="${pageContext.request.contextPath}/logout">登出</a>
            </div>
        </div>
    </div>

    <div class="container">
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

        <!-- 消息提示 -->
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        <c:if test="${not empty warning}">
            <div class="alert alert-warning">${warning}</div>
        </c:if>

        <!-- 工具栏 -->
        <div class="toolbar">
            <form action="${pageContext.request.contextPath}/students/search" method="get" class="search-form">
                <input type="text" name="keyword" placeholder="搜索学号、姓名或学院..." value="${keyword}">
                <button type="submit" class="btn btn-primary">搜索</button>
                <a href="${pageContext.request.contextPath}/students" class="btn btn-secondary">显示全部</a>
            </form>
            <div class="action-buttons">
                <a href="${pageContext.request.contextPath}/students/export" class="btn btn-info">📊 下载学生信息</a>
                <a href="${pageContext.request.contextPath}/logs" class="btn btn-secondary">📋 日志记录信息</a>
                <a href="${pageContext.request.contextPath}/students/add" class="btn btn-success">+ 添加学生</a>
            </div>
        </div>

        <!-- 学生列表 -->
        <div class="content-card">
            <div class="card-header">
                学生信息列表
                <c:if test="${not empty keyword}">
                    - 搜索结果: "${keyword}"
                </c:if>
            </div>
            
            <c:choose>
                <c:when test="${empty students}">
                    <div class="no-data">
                        <c:choose>
                            <c:when test="${not empty keyword}">
                                未找到匹配的学生信息
                            </c:when>
                            <c:otherwise>
                                暂无学生信息
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>照片</th>
                                    <th>学号</th>
                                    <th>姓名</th>
                                    <th>学院</th>
                                    <th>入学日期</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="student" items="${students}">
                                    <tr>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty student.photo_path}">
                                                    <!-- 统一使用 /students/{id}/photo 接口来显示照片 -->
                                                    <img src="${pageContext.request.contextPath}/students/${student.student_id}/photo" 
                                                         alt="学生头像" class="student-photo" 
                                                         onerror="this.style.display='none'; this.nextSibling.style.display='flex';">
                                                    <div class="no-photo" style="display: none;">👤</div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="no-photo">👤</div>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><strong>${student.student_number}</strong></td>
                                        <td>${student.student_name}</td>
                                        <td>${student.college}</td>
                                        <td>
                                            <fmt:formatDate value="${student.enrollment_date}" pattern="yyyy-MM-dd"/>
                                        </td>
                                        <td>
                                            <div class="action-buttons">
                                                <a href="${pageContext.request.contextPath}/students/${student.student_id}" 
                                                   class="btn btn-primary btn-sm">查看</a>
                                                <a href="${pageContext.request.contextPath}/students/${student.student_id}/edit" 
                                                   class="btn btn-warning btn-sm">编辑</a>
                                                <form action="${pageContext.request.contextPath}/students/${student.student_id}/delete" 
                                                      method="post" style="display: inline;" 
                                                      onsubmit="return confirm('确定要删除学生 ${student.student_name} 的信息吗？')">
                                                    <button type="submit" class="btn btn-danger btn-sm">删除</button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        
        <!-- 分页功能 -->
        <c:if test="${hasResults && totalPages > 1}">
            <div class="pagination-section">
                <div class="pagination">
                    <!-- 构建查询参数 -->
                    <c:set var="queryParams" value="" />
                    <c:if test="${not empty keyword}">
                        <c:set var="queryParams" value="${queryParams}&keyword=${keyword}" />
                    </c:if>
                    
                    <!-- 上一页 -->
                    <c:choose>
                        <c:when test="${currentPage > 1}">
                            <a href="${pageContext.request.contextPath}/students?page=${currentPage - 1}${queryParams}">‹ 上一页</a>
                        </c:when>
                        <c:otherwise>
                            <span class="disabled">‹ 上一页</span>
                        </c:otherwise>
                    </c:choose>
                    
                    <!-- 页码数字 -->
                    <c:forEach begin="1" end="${totalPages}" var="pageNum">
                        <c:choose>
                            <c:when test="${pageNum == currentPage}">
                                <span class="current">${pageNum}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/students?page=${pageNum}${queryParams}">${pageNum}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    
                    <!-- 下一页 -->
                    <c:choose>
                        <c:when test="${currentPage < totalPages}">
                            <a href="${pageContext.request.contextPath}/students?page=${currentPage + 1}${queryParams}">下一页 ›</a>
                        </c:when>
                        <c:otherwise>
                            <span class="disabled">下一页 ›</span>
                        </c:otherwise>
                    </c:choose>
                </div>
                
                <div style="margin-top: 10px; color: #6c757d; font-size: 14px;">
                    第 ${currentPage} 页，共 ${totalPages} 页，每页显示 5 条记录
                </div>
            </div>
        </c:if>
    </div>
</body>
</html>
