<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>添加选课记录 - 学生选课管理系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/form.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/alert.css">
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
            max-width: 1000px;
            margin: 0 auto;
            padding: 0 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .header h1 {
            margin: 0;
            font-size: 24px;
            font-weight: 300;
        }
        
        .nav-links a {
            color: white;
            text-decoration: none;
            margin-left: 15px;
            padding: 8px 16px;
            border-radius: 4px;
            transition: background-color 0.3s;
        }
        
        .nav-links a:hover {
            background-color: rgba(255,255,255,0.2);
        }
        
        .container {
            max-width: 1000px;
            margin: 0 auto;
            padding: 0 20px;
        }
        
        .form-container {
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            padding: 20px;
            margin-top: 20px;
        }
        
        .form-title {
            font-size: 20px;
            margin-bottom: 20px;
            color: #333;
            border-bottom: 1px solid #eee;
            padding-bottom: 10px;
        }
        
        .student-info {
            margin-bottom: 20px;
        }
        
        .form-group {
            margin-bottom: 15px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #333;
        }
        
        .form-control {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        
        .required:after {
            content: " *";
            color: #f00;
        }
        
        textarea.form-control {
            resize: vertical;
        }
        
        .course-selection {
            margin-top: 20px;
        }
        
        .course-list {
            display: flex;
            flex-direction: column;
            gap: 10px;
            max-height: 300px;
            overflow-y: auto;
            border: 1px solid #ddd;
            border-radius: 4px;
            padding: 10px;
        }
        
        .course-item {
            padding: 10px;
            border-bottom: 1px solid #eee;
            background-color: #f8f9fa;
            border-radius: 4px;
        }
        
        .course-item:last-child {
            border-bottom: none;
        }
        
        .course-item label {
            display: flex;
            align-items: center;
            cursor: pointer;
            font-weight: bold;
        }
        
        .course-item input[type="checkbox"] {
            margin-right: 10px;
            transform: scale(1.2);
        }
        
        .course-info {
            display: flex;
            margin-left: 25px;
            color: #666;
            font-size: 0.9em;
            margin-top: 5px;
        }
        
        .course-info p {
            margin: 5px 15px 5px 0;
        }
        
        .btn-container {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
            margin-top: 20px;
        }
        
        .btn {
            padding: 8px 16px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: bold;
            min-width: 100px;
            text-align: center;
        }
        
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        
        .btn-primary {
            background-color: #28a745;
            color: white;
        }
        
        .back-link {
            margin-bottom: 20px;
        }
        
        .back-link a {
            color: #007bff;
            text-decoration: none;
        }
        
        .back-link a:hover {
            text-decoration: underline;
        }
        
        .error-message {
            color: #dc3545;
            margin-top: 5px;
            font-size: 0.9em;
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
            <h1>添加选课记录</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/courseSelection/students/${student.student_id}">返回详情</a>
                <a href="${pageContext.request.contextPath}/courseSelection">返回列表</a>
                <a href="${pageContext.request.contextPath}/admin/dashboard">返回仪表板</a>
                <a href="${pageContext.request.contextPath}/logout">登出</a>
            </div>
        </div>
    </div>

    <div class="container">
        <div class="form-container">
            <h2 class="form-title">添加选课记录</h2>
            
            <!-- 显示错误消息 -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-with-icon">
                    ${error}
                </div>
            </c:if>
            
            <!-- 学生信息显示 -->
            <c:if test="${not empty student}">
                <div class="student-info">
                    <p><strong>学生: </strong>${student.student_name} (${student.student_number})</p>
                    <p><strong>学院: </strong>${student.college}</p>
                </div>
            </c:if>
            
            <form action="${pageContext.request.contextPath}/courseSelection/students/${student.student_id}/add" method="post">
                <!-- 如果已经有学生ID，则隐藏学生选择 -->
                <c:if test="${empty student}">
                    <div class="form-group">
                        <label for="student_id" class="required">选择学生</label>
                        <select name="student_id" id="student_id" class="form-control" required>
                            <option value="">-- 请选择学生 --</option>
                            <c:forEach items="${students}" var="student">
                                <option value="${student.student_id}">${student.student_name} (${student.student_number})</option>
                            </c:forEach>
                        </select>
                    </div>
                </c:if>
                <c:if test="${not empty student}">
                    <input type="hidden" name="student_id" value="${student.student_id}">
                </c:if>
                
                <div class="form-group">
                    <label for="academic_year" class="required">学年</label>
                    <input type="text" name="academic_year" id="academic_year" class="form-control" value="${courseSelection.academic_year}" readonly>
                </div>
                
                <div class="form-group">
                    <label for="semester" class="required">学期</label>
                    <input type="text" name="semester" id="semester" class="form-control" value="${courseSelection.semester}" readonly>
                </div>

                
                <!-- 课程选择部分 -->
                <div class="course-selection">
                    <h3>选择课程</h3>
                    <p>请至少选择一门课程</p>
                    
                    <div class="course-list">
                        <c:forEach items="${courses}" var="course">
                            <div class="course-item">
                                <label>
                                    <input type="checkbox" name="course_ids" value="${course.course_id}">
                                    ${course.course_name}
                                </label>
                                <div class="course-info">
                                    <p>课程代码: ${course.course_code}</p>
                                    <p>类型: ${course.course_type}</p>
                                    <p>学分: ${course.credits} (${course.credit_hours}学时)</p>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    <div class="error-message" id="courses-error"></div>
                </div>
                
                <div class="btn-container" style="margin-top: 30px; display: flex; justify-content: flex-end;">
                    <button type="button" class="btn btn-secondary" onclick="window.location.href='${pageContext.request.contextPath}/courseSelection/students/${student.student_id}'">取消</button>
                    <button type="submit" class="btn btn-primary" id="submit-btn">保存选课记录</button>
                </div>
            </form>
        </div>
    </div>
    
    <script>
        // 表单验证
        document.addEventListener('DOMContentLoaded', function() {
            const form = document.querySelector('form');
            const coursesError = document.getElementById('courses-error');
            
            form.addEventListener('submit', function(event) {
                let isValid = true;
                
                // 验证是否选择了至少一门课程
                const selectedCourses = document.querySelectorAll('input[name="course_ids"]:checked');
                if (selectedCourses.length === 0) {
                    coursesError.textContent = '请至少选择一门课程';
                    isValid = false;
                } else {
                    coursesError.textContent = '';
                }
                
                if (!isValid) {
                    event.preventDefault();
                }
            });
        });
    </script>
</body>
</html> 