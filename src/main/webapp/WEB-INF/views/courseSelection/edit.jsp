<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>编辑选课记录 - 学生选课管理系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/form.css">
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

        /* 编辑页面特定样式 */
        .form-control[readonly] {
            background-color: #f9f9f9;
            cursor: not-allowed;
        }

        h2 {
            margin-top: 20px;
        }
        
        .course-item {
            margin-bottom: 20px;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            background-color: #f9f9f9;
        }
        
        .course-item h3 {
            margin-top: 0;
            border-bottom: 1px solid #ddd;
            padding-bottom: 10px;
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="header-content">
            <h1>编辑选课记录</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/courseSelection/students/${courseSelection.student_id}">返回详情</a>
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
        
        <form:form action="${pageContext.request.contextPath}/courseSelection/selections/${courseSelection.selection_id}/edit" method="post" modelAttribute="courseSelection">
            <input type="hidden" name="selection_id" value="${courseSelection.selection_id}">
            <input type="hidden" name="student_id" value="${courseSelection.student_id}">
            
            <div class="form-group">
                <label for="student_name">学生</label>
                <input type="text" id="student_name" class="form-control" value="${courseSelection.student.student_name} (${courseSelection.student.student_number})" readonly>
            </div>
            
            <div class="form-group">
                <label for="academic_year">学年</label>
                <input type="text" name="academic_year" id="academic_year" class="form-control" required
                       value="${courseSelection.academic_year}" placeholder="例如：2023-2024">
            </div>
            
            <div class="form-group">
                <label for="semester">学期</label>
                <select name="semester" id="semester" class="form-control" required>
                    <option value="">-- 请选择学期 --</option>
                    <option value="第一学期" ${courseSelection.semester == '第一学期' ? 'selected' : ''}>第一学期</option>
                    <option value="第二学期" ${courseSelection.semester == '第二学期' ? 'selected' : ''}>第二学期</option>
                </select>
            </div>

            <h2>课程信息</h2>
            
            <c:forEach items="${selectedCourses}" var="courseDetail" varStatus="status">
                <div class="course-item">
                    <h3>课程 ${status.index + 1}</h3>
                    <input type="hidden" name="courseDetails[${status.index}].list_id" value="${courseDetail.list_id}">
                    <input type="hidden" name="courseDetails[${status.index}].selection_id" value="${courseDetail.selection_id}">
                    <input type="hidden" name="courseDetails[${status.index}].course_id" value="${courseDetail.course_id}">
                    
                    <div class="form-group">
                        <label for="courseCode_${status.index}">课程代码</label>
                        <input type="text" name="courseDetails[${status.index}].course.course_code" id="courseCode_${status.index}" 
                               class="form-control" value="${courseDetail.course.course_code}" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="courseName_${status.index}">课程名称</label>
                        <input type="text" name="courseDetails[${status.index}].course.course_name" id="courseName_${status.index}" 
                               class="form-control" value="${courseDetail.course.course_name}" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="courseType_${status.index}">课程类型</label>
                        <select name="courseDetails[${status.index}].course.course_type" id="courseType_${status.index}" class="form-control" required>
                            <option value="必修" ${courseDetail.course.course_type == '必修' ? 'selected' : ''}>必修</option>
                            <option value="选修" ${courseDetail.course.course_type == '选修' ? 'selected' : ''}>选修</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="creditHours_${status.index}">学时</label>
                        <input type="number" name="courseDetails[${status.index}].course.credit_hours" id="creditHours_${status.index}" 
                               class="form-control" value="${courseDetail.course.credit_hours}" required min="1">
                    </div>
                    
                    <div class="form-group">
                        <label for="credits_${status.index}">学分</label>
                        <input type="number" name="courseDetails[${status.index}].course.credits" id="credits_${status.index}" 
                               class="form-control" value="${courseDetail.course.credits}" required step="0.5" min="0.5">
                    </div>
                </div>
            </c:forEach>
            
            <div class="form-group">
                <button type="submit" class="btn">保存修改</button>
            </div>
        </form:form>
    </div>
</body>
</html> 