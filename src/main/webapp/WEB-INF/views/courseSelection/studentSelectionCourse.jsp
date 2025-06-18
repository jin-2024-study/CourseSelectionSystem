<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>学生选课详情 - 学生选课管理系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/view.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/detail.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/alert.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/buttons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseSelection/studentDetail.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        /* 学生照片样式 */
        .student-info-content {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
        }
        
        .student-details {
            flex-grow: 1;
        }
        
        .student-photo {
            width: 150px;
            height: 200px;
            display: flex;
            justify-content: center;
            align-items: center;
            margin-left: 20px;
            background-color: #f8f9fa;
            border-radius: 4px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            overflow: hidden;
        }
        
        .student-photo img {
            max-width: 100%;
            max-height: 100%;
            object-fit: cover;
        }
        
        .no-photo {
            width: 100%;
            height: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
            background-color: #e9ecef;
            border: 1px solid #ddd;
            border-radius: 4px;
            color: #6c757d;
        }
        

        
        .btn-info:before {
            content: "";
            display: inline-block;
            width: 16px;
            height: 16px;
            margin-right: 5px;
            background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="white"><path d="M8 9.44l3.22-3.22 1.06 1.06L8 11.56 3.72 7.28l1.06-1.06L8 9.44z"/><path d="M2 14h12v-2H2v2z"/></svg>');
            background-size: cover;
        }
        
        /* 筛选表单样式 */
        .filter-form {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
        }

        .filter-form select {
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            margin-right: 10px;
            min-width: 150px;
        }

        .filter-form button {
            padding: 8px 16px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        .filter-form button:hover {
            background-color: #45a049;
        }

        .filter-form .reset-btn {
            background-color: #6c757d;
        }

        .filter-form .reset-btn:hover {
            background-color: #5a6268;
        }

        .filter-title {
            margin-bottom: 10px;
            font-weight: bold;
            color: #333;
            font-size: 16px;
        }

        .filter-hint {
            background-color: #fff3cd;
            color: #856404;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
            border-left: 4px solid #ffc107;
            font-weight: bold;
        }

        .search-btn {
            background-color: #007bff !important;
            font-weight: bold;
        }

        .search-btn:hover {
            background-color: #0069d9 !important;
        }

        /* 添加选课和删除全部按钮样式 */
        .btn-group {
            display: flex;
            gap: 15px;
            margin-bottom: 20px;
        }

        .btn-group .btn-base {
            padding: 10px 20px;
            font-size: 16px;
            font-weight: bold;
            border-radius: 5px;
            text-decoration: none;
            text-align: center;
            transition: all 0.3s;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
        }

        .btn-group .btn-primary {
            background-color: #28a745;
            color: white;
            border: 2px solid #28a745;
        }

        .btn-group .btn-primary:hover {
            background-color: #218838;
            border-color: #1e7e34;
            transform: translateY(-2px);
        }

        .btn-group .btn-danger {
            background-color: #dc3545;
            color: white;
            border: 2px solid #dc3545;
        }

        .btn-group .btn-danger:hover {
            background-color: #c82333;
            border-color: #bd2130;
            transform: translateY(-2px);
        }

        /* 添加选课按钮样式 */
        .btn-success {
            background-color: #28a745;
            color: white;
            padding: 6px 12px;
            border-radius: 4px;
            text-decoration: none;
            font-weight: bold;
            display: inline-block;
            transition: all 0.3s;
        }

        .btn-success:hover {
            background-color: #218838;
            transform: translateY(-1px);
            box-shadow: 0 2px 3px rgba(0, 0, 0, 0.2);
        }

        .courseSelection-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid #eee;
            margin-bottom: 10px;
        }

        .courseSelection-header-actions {
            display: flex;
            gap: 10px;
        }

        /* 添加学年学期按钮样式 */
        .add-semester-btn {
            background-color: #17a2b8;
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 4px;
            cursor: pointer;
            margin-left: 10px;
            font-weight: bold;
        }

        .add-semester-btn:hover {
            background-color: #138496;
        }

        /* 下载按钮样式 */
        .download-btn {
            background-color: #28a745;
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 4px;
            cursor: pointer;
            margin-left: 10px;
            font-weight: bold;
        }

        .download-btn:hover {
            background-color: #218838;
        }

        /* 模态框样式 */
        .modal {
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
        }

        .modal-content {
            background-color: #fefefe;
            margin: 10% auto;
            padding: 20px;
            border: none;
            border-radius: 8px;
            width: 400px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            position: relative;
        }

        .close {
            color: #aaa;
            float: right;
            font-size: 28px;
            font-weight: bold;
            cursor: pointer;
            position: absolute;
            right: 15px;
            top: 10px;
        }

        .close:hover,
        .close:focus {
            color: #000;
            text-decoration: none;
        }

        .modal h3 {
            margin-top: 0;
            margin-bottom: 20px;
            color: #333;
            text-align: center;
        }

        .modal .form-group {
            margin-bottom: 20px;
        }

        .modal .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #333;
        }

        .modal .form-group input,
        .modal .form-group select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
            box-sizing: border-box;
        }

        .modal .form-group input:focus,
        .modal .form-group select:focus {
            outline: none;
            border-color: #4CAF50;
            box-shadow: 0 0 5px rgba(76, 175, 80, 0.3);
        }

        .required {
            color: #dc3545;
        }

        .error-message {
            color: #dc3545;
            font-size: 12px;
            margin-top: 5px;
            display: none;
        }

        .modal-buttons {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
            margin-top: 20px;
        }

        .modal-buttons button {
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: bold;
        }

        .modal-buttons button[type="button"] {
            background-color: #6c757d;
            color: white;
        }

        .modal-buttons button[type="button"]:hover {
            background-color: #5a6268;
        }

        .modal-buttons button[type="submit"] {
            background-color: #4CAF50;
            color: white;
        }

        .modal-buttons button[type="submit"]:hover {
            background-color: #45a049;
        }
        /* 头部样式 */
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
            max-width: 1200px;
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
            <h1>学生选课详情</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/notifications">
                    <i class="fas fa-bell"></i> 实时通知
                </a>
                <c:if test="${isAdmin}">
                    <a href="${pageContext.request.contextPath}/logs">日志管理</a>
                </c:if>
                <a href="${pageContext.request.contextPath}/courseSelection">返回列表</a>
                <a href="${pageContext.request.contextPath}/admin/dashboard">返回仪表板</a>
                <a href="${pageContext.request.contextPath}/logout">登出</a>
            </div>
        </div>
    </div>

<div class="container">
    <!-- 显示成功或错误消息 -->
    <c:if test="${not empty success}">
        <div class="alert alert-success alert-with-icon">
                ${success}
        </div>
    </c:if>

    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-with-icon">
                ${error}
        </div>
    </c:if>

    <c:if test="${not empty warning}">
        <div class="alert alert-warning alert-with-icon">
                ${warning}
        </div>
    </c:if>

    <c:if test="${not empty info}">
        <div class="alert alert-info alert-with-icon">
                ${info}
        </div>
    </c:if>

    <!-- 学生基本信息 -->
    <div class="student-info">
        <h2>学生信息</h2>
        <div class="student-info-content">
            <div class="student-details">
                <p><strong>学号</strong>${student.student_number}</p>
                <p><strong>姓名</strong>${student.student_name}</p>
                <p><strong>学院</strong>${student.college}</p>
                <p><strong>入学日期</strong><fmt:formatDate value="${student.enrollment_date}" pattern="yyyy-MM-dd"/></p>

            </div>
            <div class="student-photo">
                <c:choose>
                    <c:when test="${not empty student.photo_path}">
                        <img src="${pageContext.request.contextPath}/students/${student.student_id}/photo" 
                             alt="学生照片" style="max-width: 150px; max-height: 200px; border: 1px solid #ddd; border-radius: 4px;">
                    </c:when>
                    <c:otherwise>
                        <div class="no-photo">
                            <span>暂无照片</span>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- 学年学期筛选表单 -->
    <div class="filter-form">
        <div class="filter-title">按学年学期筛选</div>
        <form action="${pageContext.request.contextPath}/courseSelection/students/${student.student_id}/filter" method="get">
            <input type="hidden" name="studentId" value="${student.student_id}">

            <select name="academicYear">
                <option value="">所有学年</option>
                <c:forEach items="${academicYears}" var="year">
                    <option value="${year}" ${selectedYear eq year ? 'selected' : ''}>${year}</option>
                </c:forEach>
            </select>

            <select name="semester">
                <option value="">所有学期</option>
                <c:forEach items="${semesters}" var="sem">
                    <option value="${sem}" ${selectedSemester eq sem ? 'selected' : ''}>${sem}</option>
                </c:forEach>
            </select>

            <button type="submit">筛选</button>
            <button type="button" class="reset-btn"
                    onclick="window.location.href='${pageContext.request.contextPath}/courseSelection/students/${student.student_id}/filter?academicYear=&semester='">
                重置
            </button>
            <!-- 所有用户都可以下载学生选课情况 -->
            <button type="button" class="download-btn" onclick="downloadStudentCourses()">
                📊 下载学生选课情况
            </button>
            <!-- 只有管理员才能添加学年学期 -->
            <c:if test="${isAdmin}">
                <button type="button" class="add-semester-btn" onclick="showAddSemesterModal()">
                    添加学年学期
                </button>
            </c:if>
        </form>
    </div>

    <!-- 添加学年学期模态框 - 只有管理员可见 -->
    <c:if test="${isAdmin}">
        <div id="addSemesterModal" class="modal" style="display: none;">
            <div class="modal-content">
                <span class="close" onclick="closeAddSemesterModal()">&times;</span>
                <h3>添加学年学期</h3>
                <form id="addSemesterForm" action="${pageContext.request.contextPath}/courseSelection/students/${student.student_id}/addSemester" method="post">
                    <div class="form-group">
                        <label for="academicYear">学年<span class="required">*</span></label>
                        <input type="text" id="academicYear" name="academicYear" placeholder="例如：2025-2026" required>
                        <div class="error-message" id="academicYear-error"></div>
                    </div>
                    <div class="form-group">
                        <label for="semester">学期<span class="required">*</span></label>
                        <select id="semester" name="semester" required>
                            <option value="">请选择学期</option>
                            <option value="第一学期">第一学期</option>
                            <option value="第二学期">第二学期</option>
                        </select>
                        <div class="error-message" id="semester-error"></div>
                    </div>
                    <div class="modal-buttons">
                        <button type="button" onclick="closeAddSemesterModal()">取消</button>
                        <button type="submit">添加</button>
                    </div>
                </form>
            </div>
        </div>
    </c:if>

    <!-- 选课列表 -->
    <div class="section-title">
        <h2>选课记录</h2>
    </div>

    <c:if test="${empty courseSelections}">
        <p class="no-data">该学生暂无选课记录</p>
    </c:if>

    <c:if test="${not empty groupedSelections}">
        <!-- 添加统计信息 -->
        <c:set var="totalCredits" value="0"/>
        <c:set var="totalHours" value="0"/>
        <c:set var="requiredCourses" value="0"/>
        <c:set var="electiveCourses" value="0"/>
        <c:forEach items="${groupedSelections}" var="group">
            <div class="semester-group">
                <h3 class="semester-title">${group.key} <span style="margin-left: 10px; font-weight: normal;">${group.value[0].semester}</span>
                        <a href="${pageContext.request.contextPath}/courseSelection/students/${student.student_id}/add?academicYear=${group.key}&semester=${group.value[0].semester}"
                           class="btn-base btn-success btn-sm" style="float: right;" >添加选课</a>
                </h3>
                <c:forEach items="${group.value}" var="courseSelection">
                    <c:if test="${not empty courseSelection.courseSelectionLists}">
                    <div class="courseSelection-card">
                        <div class="courseSelection-details">
                                <table>
                                    <thead>
                                    <tr>
                                        <th>课程代码</th>
                                        <th>课程名称</th>
                                        <th>课程类型</th>
                                        <th>学时</th>
                                        <th>学分</th>
                                        <th>操作</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${courseSelection.courseSelectionLists}" var="detail">
                                        <tr>
                                            <td>${detail.course.course_code}</td>
                                            <td>${detail.course.course_name}</td>
                                            <td class="${detail.course.course_type == '必修' ? 'required-course' : 'elective-course'}">${detail.course.course_type}</td>
                                            <td>${detail.course.credit_hours}</td>
                                            <td>${detail.course.credits}</td>
                                            <td class="action-cell">
                                                <!-- 只有管理员才能修改和删除选课 -->
                                                <c:if test="${isAdmin}">
                                                    <a href="${pageContext.request.contextPath}/courseSelection/courses/${detail.list_id}/edit?selectionId=${courseSelection.selection_id}&academicYear=${selectedYear}&semester=${selectedSemester}"
                                                       class="btn-base btn-info btn-sm">修改</a>
                                                    <a href="javascript:void(0)" 
                                                       class="btn-base btn-danger btn-sm delete-course-btn"
                                                       data-list-id="${detail.list_id}"
                                                       data-selection-id="${courseSelection.selection_id}"
                                                       data-student-id="${student.student_id}"
                                                       data-academic-year="${selectedYear}"
                                                       data-semester="${selectedSemester}">删除</a>
                                                </c:if>
                                                <c:if test="${!isAdmin}">
                                                    <span class="text-muted">-</span>
                                                </c:if>
                                            </td>
                                            <c:forEach items="${courseSelection.courseSelectionLists}" var="detail">
                                                <c:set var="totalCredits" value="${totalCredits + detail.course.credits}"/>
                                                <c:set var="totalHours" value="${totalHours + detail.course.credit_hours}"/>
                                                <c:if test="${detail.course.course_type == '必修'}">
                                                    <c:set var="requiredCourses" value="${requiredCourses + 1}"/>
                                                </c:if>
                                                <c:if test="${detail.course.course_type == '选修'}">
                                                    <c:set var="electiveCourses" value="${electiveCourses + 1}"/>
                                                </c:if>
                                            </c:forEach>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                        </div>
                    </div>
                    </c:if>
                </c:forEach>
                <div class="course-stats">
                    <span><strong>总课程：</strong>${requiredCourses + electiveCourses}门</span>
                    <span><strong>必修课：</strong>${requiredCourses}门</span>
                    <span><strong>选修课：</strong>${electiveCourses}门</span>
                    <span><strong>总学时：</strong>${totalHours}学时</span>
                    <span><strong>总学分：</strong>${totalCredits}分</span>
                </div>
            </div>
        </c:forEach>
    </c:if>
</div>
<script>
    // 添加URL编码函数，专门处理中文字符
    function encodeParam(value) {
        if (!value) return '';
        return encodeURIComponent(value);
    }
    
    // 页面加载完成后绑定事件
    document.addEventListener('DOMContentLoaded', function() {
        // 修改所有包含学期参数的链接
        document.querySelectorAll('a[href*="semester="]').forEach(function(link) {
            var href = link.getAttribute('href');
            // 提取学期参数
            var match = href.match(/semester=([^&]*)/);
            if (match && match[1]) {
                var semester = decodeURIComponent(match[1]);
                // 替换为编码后的参数
                var newHref = href.replace(/semester=[^&]*/, 'semester=' + encodeParam(semester));
                link.setAttribute('href', newHref);
            }
        });
        
        // 为所有删除按钮添加点击事件
        var deleteButtons = document.querySelectorAll('.delete-course-btn');
        deleteButtons.forEach(function(button) {
            button.addEventListener('click', function() {
                var listId = this.getAttribute('data-list-id');
                var selectionId = this.getAttribute('data-selection-id');
                var studentId = this.getAttribute('data-student-id');
                var academicYear = this.getAttribute('data-academic-year');
                var semester = this.getAttribute('data-semester');
                
                deleteCourse(listId, selectionId, studentId, academicYear, semester);
            });
        });
    });

    // 显示添加学年学期模态框
    function showAddSemesterModal() {
        document.getElementById('addSemesterModal').style.display = 'block';
        // 清空表单
        document.getElementById('addSemesterForm').reset();
        // 隐藏错误信息
        document.getElementById('academicYear-error').style.display = 'none';
        document.getElementById('semester-error').style.display = 'none';
    }

    // 关闭添加学年学期模态框
    function closeAddSemesterModal() {
        document.getElementById('addSemesterModal').style.display = 'none';
    }

    // 验证学年格式
    function validateAcademicYear(academicYear) {
        // 学年格式必须是YYYY-YYYY，且后一年比前一年大1
        const pattern = /^(\d{4})-(\d{4})$/;
        const match = academicYear.match(pattern);
        
        if (!match) {
            return false;
        }
        
        const startYear = parseInt(match[1]);
        const endYear = parseInt(match[2]);
        
        // 检查是否是连续的年份
        if (endYear !== startYear + 1) {
            return false;
        }
        
        // 检查年份是否合理（比如在1900-2100之间）
        if (startYear < 1900 || startYear > 2100) {
            return false;
        }
        
        return true;
    }

    // 验证学期格式
    function validateSemester(semester) {
        return semester === '第一学期' || semester === '第二学期';
    }

    // 表单提交验证
    document.addEventListener('DOMContentLoaded', function() {
        document.getElementById('addSemesterForm').addEventListener('submit', function(e) {
            e.preventDefault();
            
            const academicYear = document.getElementById('academicYear').value.trim();
            const semester = document.getElementById('semester').value;
            
            let isValid = true;
            
            // 验证学年
            if (!academicYear) {
                document.getElementById('academicYear-error').textContent = '请输入学年';
                document.getElementById('academicYear-error').style.display = 'block';
                isValid = false;
            } else if (!validateAcademicYear(academicYear)) {
                document.getElementById('academicYear-error').textContent = '学年格式不正确，请输入如"2025-2026"的格式';
                document.getElementById('academicYear-error').style.display = 'block';
                isValid = false;
            } else {
                document.getElementById('academicYear-error').style.display = 'none';
            }
            
            // 验证学期
            if (!semester) {
                document.getElementById('semester-error').textContent = '请选择学期';
                document.getElementById('semester-error').style.display = 'block';
                isValid = false;
            } else if (!validateSemester(semester)) {
                document.getElementById('semester-error').textContent = '学期只能是"第一学期"或"第二学期"';
                document.getElementById('semester-error').style.display = 'block';
                isValid = false;
            } else {
                document.getElementById('semester-error').style.display = 'none';
            }
            
            if (isValid) {
                // 提交表单
                this.submit();
            }
        });
    });

    // 点击模态框外部关闭
    window.onclick = function(event) {
        const modal = document.getElementById('addSemesterModal');
        if (event.target === modal) {
            closeAddSemesterModal();
        }
    };

    // 下载学生选课情况
    function downloadStudentCourses() {
        const studentId = '${student.student_id}';
        const currentAcademicYear = '${selectedYear != null ? selectedYear : ""}';
        const currentSemester = '${selectedSemester != null ? selectedSemester : ""}';
        
        // 构建下载URL
        let downloadUrl = '${pageContext.request.contextPath}/courseSelection/students/' + studentId + '/download';
        
        // 添加当前筛选的学年学期参数
        const params = new URLSearchParams();
        if (currentAcademicYear) {
            params.append('academicYear', currentAcademicYear);
        }
        if (currentSemester) {
            params.append('semester', encodeURIComponent(currentSemester));
        }
        
        if (params.toString()) {
            downloadUrl += '?' + params.toString();
        }
        
        // 创建隐藏的链接并触发下载
        const link = document.createElement('a');
        link.href = downloadUrl;
        link.style.display = 'none';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }

    function deleteCourse(listId, selectionId, studentId, academicYear, semester) {
        if (confirm('确定要删除这门课程吗？')) {
            console.log("删除课程: listId=" + listId + ", semester=" + semester);
            
            // 创建一个表单来发送DELETE请求
            var form = document.createElement('form');
            form.method = 'post';
            form.action = '${pageContext.request.contextPath}/courseSelection/courses/' + listId + '/delete';
            
            // 添加其他必要参数
            var selectionIdInput = document.createElement('input');
            selectionIdInput.type = 'hidden';
            selectionIdInput.name = 'selectionId';
            selectionIdInput.value = selectionId;
            form.appendChild(selectionIdInput);
            
            var studentIdInput = document.createElement('input');
            studentIdInput.type = 'hidden';
            studentIdInput.name = 'studentId';
            studentIdInput.value = studentId;
            form.appendChild(studentIdInput);
            
            // 添加学年参数
            if (academicYear) {
                var yearInput = document.createElement('input');
                yearInput.type = 'hidden';
                yearInput.name = 'academicYear';
                yearInput.value = academicYear;
                form.appendChild(yearInput);
            }
            
            // 添加学期参数
            if (semester) {
                var semesterInput = document.createElement('input');
                semesterInput.type = 'hidden';
                semesterInput.name = 'semester';
                semesterInput.value = semester; // 表单提交会自动编码
                form.appendChild(semesterInput);
            }
            
            // 添加表单到页面并提交
            document.body.appendChild(form);
            console.log("提交删除表单，学期：" + semester);
            form.submit();
            document.body.removeChild(form);
        }
    }
</script>
</body>
</html> 