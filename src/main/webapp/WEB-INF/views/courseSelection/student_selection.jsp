<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>学生身份选择</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Microsoft YaHei', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
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

        .alert {
            padding: 15px 20px;
            margin-bottom: 20px;
            border-radius: 8px;
            border-left: 4px solid;
        }

        .alert-info {
            background-color: #d1ecf1;
            border-color: #17a2b8;
            color: #0c5460;
        }

        .alert-error {
            background-color: #f8d7da;
            border-color: #dc3545;
            color: #721c24;
        }

        .search-section {
            background: white;
            padding: 20px;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            margin-bottom: 30px;
        }

        .search-form {
            display: flex;
            gap: 15px;
            align-items: center;
            flex-wrap: wrap;
        }

        .search-input {
            flex: 1;
            min-width: 250px;
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
            padding: 12px 20px;
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
            padding: 12px 16px;
            background: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 6px;
            font-weight: 600;
            transition: background-color 0.3s;
        }

        .clear-btn:hover {
            background: #545b62;
        }

        .search-results-info {
            text-align: center;
            margin: 15px 0;
            padding: 10px;
            background: #f8f9fa;
            border-radius: 6px;
            color: #495057;
        }

        .selection-card {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            overflow: hidden;
            margin-bottom: 30px;
        }

        .selection-header {
            background: #f8f9fa;
            padding: 30px;
            text-align: center;
            border-bottom: 1px solid #dee2e6;
        }

        .selection-header h2 {
            color: #007bff;
            margin: 0 0 10px 0;
            font-size: 28px;
            font-weight: 600;
        }

        .selection-header p {
            color: #6c757d;
            font-size: 16px;
            margin: 0;
        }

        .student-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 20px;
            padding: 30px;
        }

        .student-item {
            border: 2px solid #e9ecef;
            border-radius: 8px;
            padding: 20px;
            text-align: center;
            transition: all 0.3s;
            cursor: pointer;
            background: #f8f9fa;
        }

        .student-item:hover {
            border-color: #007bff;
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(0,123,255,0.15);
            background: white;
        }

        .student-photo {
            width: 80px;
            height: 80px;
            border-radius: 50%;
            object-fit: cover;
            margin: 0 auto 15px auto;
            display: block;
            border: 3px solid #dee2e6;
        }

        .no-photo {
            width: 80px;
            height: 80px;
            border-radius: 50%;
            background-color: #e9ecef;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #6c757d;
            font-size: 32px;
            margin: 0 auto 15px auto;
            border: 3px solid #dee2e6;
        }

        .student-info h3 {
            margin: 0 0 10px 0;
            color: #2c3e50;
            font-size: 18px;
            font-weight: 600;
        }

        .student-info p {
            margin: 5px 0;
            color: #666;
            font-size: 14px;
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

        .back-section {
            text-align: center;
            margin-top: 30px;
        }

        .btn {
            padding: 12px 24px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s;
            display: inline-block;
        }

        .btn-secondary {
            background: #6c757d;
            color: white;
        }

        .btn-secondary:hover {
            background: #545b62;
            transform: translateY(-1px);
        }

        @media (max-width: 768px) {
            .student-grid {
                grid-template-columns: 1fr;
                padding: 20px;
            }
            
            .search-form {
                flex-direction: column;
                align-items: stretch;
            }
            
            .search-input {
                min-width: auto;
            }

            .selection-header {
                padding: 20px;
            }

            .selection-header h2 {
                font-size: 24px;
            }
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="header-content">
            <h1>学生身份选择</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/notifications">
                    <i class="fas fa-bell"></i> 实时通知
                </a>
                <a href="${pageContext.request.contextPath}/admin/dashboard">返回首页</a>
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
            <form class="search-form" method="get" action="${pageContext.request.contextPath}/courseSelection">
                <input type="text" 
                       name="keyword" 
                       class="search-input" 
                       placeholder="请输入学号或姓名进行搜索..." 
                       value="${keyword}">
                <button type="submit" class="search-btn">🔍 搜索</button>
                <c:if test="${not empty keyword}">
                    <a href="${pageContext.request.contextPath}/courseSelection" class="clear-btn">清除搜索</a>
                </c:if>
            </form>
        </div>

        <!-- 搜索结果信息 -->
        <c:if test="${hasResults}">
            <div class="search-results-info">
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

        <div class="selection-card">
            <div class="selection-header">
                <h2>选择学生身份</h2>
                <p>请选择您对应的学生身份以进行选课操作</p>
            </div>

            <c:choose>
                <c:when test="${not empty students}">
                    <div class="student-grid">
                        <c:forEach items="${students}" var="student">
                            <div class="student-item" onclick="selectStudent(${student.student_id})">
                                <c:choose>
                                    <c:when test="${not empty student.photo_path}">
                                        <img src="${pageContext.request.contextPath}/students/${student.student_id}/photo" 
                                             alt="学生照片" class="student-photo" 
                                             onerror="this.style.display='none'; this.nextSibling.style.display='flex';">
                                        <div class="no-photo" style="display: none;">👤</div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="no-photo">👤</div>
                                    </c:otherwise>
                                </c:choose>
                                
                                <div class="student-info">
                                    <h3>${student.student_name}</h3>
                                    <p><strong>学号:</strong> ${student.student_number}</p>
                                    <p><strong>学院:</strong> ${student.college}</p>
                                    <p><strong>入学日期:</strong> <fmt:formatDate value="${student.enrollment_date}" pattern="yyyy-MM-dd"/></p>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div style="text-align: center; padding: 40px;">
                        <p style="color: #666; font-size: 16px;">暂无学生记录</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- 分页功能 -->
        <c:if test="${hasResults && totalPages > 1}">
            <div class="pagination-section">
                <div class="pagination">
                    <c:choose>
                        <c:when test="${currentPage > 1}">
                            <c:choose>
                                <c:when test="${not empty keyword}">
                                    <a href="${pageContext.request.contextPath}/courseSelection?keyword=${keyword}&page=${currentPage - 1}">‹ 上一页</a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/courseSelection?page=${currentPage - 1}">‹ 上一页</a>
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
                                        <a href="${pageContext.request.contextPath}/courseSelection?keyword=${keyword}&page=${pageNum}">${pageNum}</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${pageContext.request.contextPath}/courseSelection?page=${pageNum}">${pageNum}</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    
                    <c:choose>
                        <c:when test="${currentPage < totalPages}">
                            <c:choose>
                                <c:when test="${not empty keyword}">
                                    <a href="${pageContext.request.contextPath}/courseSelection?keyword=${keyword}&page=${currentPage + 1}">下一页 ›</a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/courseSelection?page=${currentPage + 1}">下一页 ›</a>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <span class="disabled">下一页 ›</span>
                        </c:otherwise>
                    </c:choose>
                </div>
                
                <div style="margin-top: 10px; color: #6c757d; font-size: 14px;">
                    第 ${currentPage} 页，共 ${totalPages} 页
                </div>
            </div>
        </c:if>

    </div>

    <script>
        function selectStudent(studentId) {
            // 重定向到该学生的选课页面
            window.location.href = '${pageContext.request.contextPath}/courseSelection/students/' + studentId;
        }
    </script>
</body>
</html>

