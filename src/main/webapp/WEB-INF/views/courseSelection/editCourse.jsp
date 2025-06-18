<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>编辑课程信息 - 学生选课管理系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/form.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/alert.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/buttons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/editCourse.css">
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
            <h1>编辑课程信息</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/courseSelection/students/${courseSelection.student_id}/filter?academicYear=${courseSelection.academic_year}&semester=${courseSelection.semester}">返回详情</a>
                <a href="${pageContext.request.contextPath}/courseSelection">返回列表</a>
                <a href="${pageContext.request.contextPath}/admin/dashboard">返回仪表板</a>
                <a href="${pageContext.request.contextPath}/logout">登出</a>
            </div>
        </div>
    </div>

    <div class="container">
        <c:if test="${not empty error}">
            <div class="alert alert-danger">
                ${error}
            </div>
        </c:if>
        
        <c:if test="${not empty success}">
            <div class="alert alert-success">
                ${success}
            </div>
        </c:if>
        
        <div class="course-info">
            <p><strong>学年：</strong>${courseSelection.academic_year}</p>
            <p><strong>学期：</strong>${courseSelection.semester}</p>
        </div>
        
        <form action="${pageContext.request.contextPath}/courseSelection/courses/${courseDetail.list_id}/edit" method="post">
            <input type="hidden" name="selectionId" value="${courseDetail.selection_id}">
            <input type="hidden" name="course_id" value="${courseDetail.course_id}">
            <input type="hidden" name="academicYear" value="${courseSelection.academic_year}">
            <input type="hidden" name="semester" value="${courseSelection.semester}">
            
            <div class="form-group">
                <label for="course_code">课程代码</label>
                <input type="text" name="course_code" id="course_code" class="form-control" 
                       value="${courseDetail.course.course_code}" required>
                <div class="form-hint">请输入有效的课程代码</div>
            </div>
            
            <div class="form-group">
                <label for="course_name">课程名称</label>
                <input type="text" name="course_name" id="course_name" class="form-control" 
                       value="${courseDetail.course.course_name}" required>
                <div class="form-hint">请输入完整的课程名称</div>
            </div>
            
            <div class="form-group">
                <label for="course_type">课程类型</label>
                <select name="course_type" id="course_type" class="form-control" required>
                    <option value="必修" ${courseDetail.course.course_type == '必修' ? 'selected' : ''}>必修</option>
                    <option value="选修" ${courseDetail.course.course_type == '选修' ? 'selected' : ''}>选修</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="credit_hours">学时</label>
                <input type="number" name="credit_hours" id="credit_hours" class="form-control" 
                       value="${courseDetail.course.credit_hours}" required min="1">
                <div class="form-hint">请输入大于或等于1的整数</div>
            </div>
            
            <div class="form-group">
                <label for="credits">学分</label>
                <input type="number" name="credits" id="credits" class="form-control" 
                       value="${courseDetail.course.credits}" required step="0.5" min="0.5">
                <div class="form-hint">请输入大于或等于0.5的数值，步长为0.5</div>
            </div>
            
            <div class="form-actions">
                <a href="${pageContext.request.contextPath}/courseSelection/students/${courseSelection.student_id}/filter?academicYear=${courseSelection.academic_year}&semester=${courseSelection.semester}" class="btn-base btn-danger">取消</a>
                <button type="submit" class="btn-base btn-primary">保存修改</button>
            </div>
        </form>
    </div>

    <script>
        // 处理URL中的中文参数编码问题
        document.addEventListener('DOMContentLoaded', function() {
            // 对所有包含semester参数的链接添加编码
            document.querySelectorAll('a[href*="semester="]').forEach(function(link) {
                var href = link.getAttribute('href');
                // 提取学期参数
                var match = href.match(/semester=([^&]*)/);
                if (match && match[1]) {
                    var semester = decodeURIComponent(match[1]);
                    // 替换为编码后的参数
                    var newHref = href.replace(/semester=[^&]*/, 'semester=' + encodeURIComponent(semester));
                    link.setAttribute('href', newHref);
                }
            });
            
            // 添加表单提交验证
            document.querySelector('form').addEventListener('submit', function(e) {
                // 验证课程代码
                var courseCode = document.getElementById('course_code').value.trim();
                if (!courseCode) {
                    alert('请输入课程代码');
                    e.preventDefault();
                    return false;
                }
                
                // 验证课程名称
                var courseName = document.getElementById('course_name').value.trim();
                if (!courseName) {
                    alert('请输入课程名称');
                    e.preventDefault();
                    return false;
                }
                
                // 验证学时
                var creditHours = document.getElementById('credit_hours').value;
                if (creditHours <= 0) {
                    alert('学时必须大于0');
                    e.preventDefault();
                    return false;
                }
                
                // 验证学分
                var credits = document.getElementById('credits').value;
                if (credits < 0.5) {
                    alert('学分必须大于或等于0.5');
                    e.preventDefault();
                    return false;
                }
                
                console.log('表单验证通过，准备提交...');
                return true;
            });
        });
    </script>
</body>
</html> 