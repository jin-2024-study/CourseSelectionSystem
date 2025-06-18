<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>学生详情 - ${student.student_name}</title>
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

        .alert-success {
            background-color: #d4edda;
            border-color: #28a745;
            color: #155724;
        }

        .alert-error {
            background-color: #f8d7da;
            border-color: #dc3545;
            color: #721c24;
        }

        .student-profile {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            overflow: hidden;
            margin-bottom: 30px;
        }

        .profile-header {
            background: linear-gradient(135deg, #e3f2fd, #bbdefb);
            padding: 30px;
            display: flex;
            align-items: center;
            gap: 30px;
        }

        .student-photo {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            object-fit: cover;
            border: 4px solid white;
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        }

        .no-photo {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            background-color: #e9ecef;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #6c757d;
            font-size: 48px;
            border: 4px solid white;
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        }

        .student-basic-info h2 {
            margin: 0 0 10px 0;
            color: #1976d2;
            font-size: 28px;
            font-weight: 600;
        }

        .student-basic-info p {
            margin: 5px 0;
            color: #424242;
            font-size: 16px;
        }

        .student-basic-info .label {
            font-weight: 600;
            color: #1976d2;
        }

        .profile-body {
            padding: 30px;
        }

        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 30px;
            margin-bottom: 30px;
        }

        .info-card {
            background: #f8f9fa;
            border-radius: 8px;
            padding: 20px;
            border-left: 4px solid #007bff;
        }

        .info-card h3 {
            margin: 0 0 15px 0;
            color: #495057;
            font-size: 18px;
            font-weight: 600;
        }

        .info-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 8px 0;
            border-bottom: 1px solid #dee2e6;
        }

        .info-item:last-child {
            border-bottom: none;
        }

        .info-label {
            font-weight: 600;
            color: #495057;
        }

        .info-value {
            color: #6c757d;
            text-align: right;
        }

        .action-buttons {
            display: flex;
            gap: 15px;
            justify-content: center;
            padding-top: 20px;
            border-top: 1px solid #dee2e6;
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
            text-align: center;
        }

        .btn-warning {
            background-color: #ffc107;
            color: #212529;
        }

        .btn-warning:hover {
            background-color: #e0a800;
            transform: translateY(-2px);
        }

        .btn-danger {
            background-color: #dc3545;
            color: white;
        }

        .btn-danger:hover {
            background-color: #c82333;
            transform: translateY(-2px);
        }

        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }

        .btn-secondary:hover {
            background-color: #545b62;
            transform: translateY(-2px);
        }

        @media (max-width: 768px) {
            .profile-header {
                flex-direction: column;
                text-align: center;
            }
            
            .action-buttons {
                flex-direction: column;
            }
            
            .info-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="header-content">
            <h1>学生详细信息</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/students">返回列表</a>
                <a href="${pageContext.request.contextPath}/admin/dashboard">仪表板</a>
            </div>
        </div>
    </div>

    <div class="container">
        <!-- 成功/错误消息 -->
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>

        <!-- 学生档案 -->
        <div class="student-profile">
            <div class="profile-header">
                <div>
                    <c:choose>
                        <c:when test="${not empty student.photo_path}">
                            <!-- 统一使用 /students/{id}/photo 接口来显示照片 -->
                            <img src="${pageContext.request.contextPath}/students/${student.student_id}/photo" 
                                 alt="学生照片" class="student-photo" 
                                 onerror="this.style.display='none'; this.nextSibling.style.display='flex';">
                            <div class="no-photo" style="display: none;">👤</div>
                        </c:when>
                        <c:otherwise>
                            <div class="no-photo">👤</div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="student-basic-info">
                    <h2>${student.student_name}</h2>
                    <p><span class="label">学号:</span> ${student.student_number}</p>
                    <p><span class="label">学院:</span> ${student.college}</p>
                    <p><span class="label">入学日期:</span> <fmt:formatDate value="${student.enrollment_date}" pattern="yyyy年MM月dd日"/></p>
                </div>
            </div>
            
            <div class="profile-body">
                <div class="info-grid">
                    <div class="info-card">
                        <h3>基本信息</h3>
                        <div class="info-item">
                            <span class="info-label">学号</span>
                            <span class="info-value">${student.student_number}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">姓名</span>
                            <span class="info-value">${student.student_name}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">所在学院</span>
                            <span class="info-value">${student.college}</span>
                        </div>
                    </div>
                    
                    <div class="info-card">
                        <h3>学籍信息</h3>
                        <div class="info-item">
                            <span class="info-label">入学日期</span>
                            <span class="info-value">
                                <fmt:formatDate value="${student.enrollment_date}" pattern="yyyy-MM-dd"/>
                            </span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">在校时长</span>
                            <span class="info-value">
                                <jsp:useBean id="now" class="java.util.Date"/>
                                <c:set var="daysDiff" value="${(now.time - student.enrollment_date.time) / (1000 * 60 * 60 * 24)}" />
                                <fmt:formatNumber value="${daysDiff}" pattern="#" maxFractionDigits="0"/>天
                            </span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">照片状态</span>
                            <span class="info-value">
                                <c:choose>
                                    <c:when test="${not empty student.photo_path}">已上传</c:when>
                                    <c:otherwise>未上传</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>
                </div>
                
                <div class="action-buttons">
                    <a href="${pageContext.request.contextPath}/students/${student.student_id}/edit" 
                       class="btn btn-warning">编辑信息</a>
                    <form action="${pageContext.request.contextPath}/students/${student.student_id}/delete" 
                          method="post" style="display: inline;" 
                          onsubmit="return confirm('确定要删除学生 ${student.student_name} 的所有信息吗？此操作不可撤销！')">
                        <button type="submit" class="btn btn-danger">删除学生</button>
                    </form>
                    <a href="${pageContext.request.contextPath}/students" class="btn btn-secondary">返回列表</a>
                </div>
            </div>
        </div>
    </div>
</body>
</html> 




