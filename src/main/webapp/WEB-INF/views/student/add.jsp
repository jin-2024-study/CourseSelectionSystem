<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>添加学生信息</title>
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
            max-width: 800px;
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

        .alert-warning {
            background-color: #fff3cd;
            border-color: #ffc107;
            color: #856404;
        }

        .form-card {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            overflow: hidden;
        }

        .card-header {
            background: #f8f9fa;
            padding: 20px 30px;
            border-bottom: 1px solid #dee2e6;
            font-weight: 600;
            color: #495057;
            font-size: 18px;
        }

        .card-body {
            padding: 30px;
        }

        .form-group {
            margin-bottom: 25px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #495057;
        }

        .required {
            color: #dc3545;
        }

        .form-control {
            width: 100%;
            padding: 12px 15px;
            border: 2px solid #e9ecef;
            border-radius: 6px;
            font-size: 14px;
            transition: border-color 0.3s, box-shadow 0.3s;
            background-color: white;
        }

        .form-control:focus {
            border-color: #28a745;
            box-shadow: 0 0 0 0.2rem rgba(40,167,69,0.25);
            outline: none;
        }

        .form-text {
            margin-top: 5px;
            font-size: 12px;
            color: #6c757d;
        }

        .photo-upload {
            display: flex;
            align-items: center;
            gap: 15px;
            flex-wrap: wrap;
        }

        .photo-preview {
            width: 80px;
            height: 80px;
            border-radius: 50%;
            object-fit: cover;
            border: 3px solid #dee2e6;
        }

        .no-photo-preview {
            width: 80px;
            height: 80px;
            border-radius: 50%;
            background-color: #e9ecef;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #6c757d;
            font-size: 28px;
            border: 3px solid #dee2e6;
        }

        .file-input-wrapper {
            position: relative;
            overflow: hidden;
            display: inline-block;
        }

        .file-input-wrapper input[type=file] {
            position: absolute;
            left: -9999px;
        }

        .file-input-label {
            padding: 8px 16px;
            background: #6c757d;
            color: white;
            border-radius: 4px;
            cursor: pointer;
            transition: background-color 0.3s;
            font-size: 14px;
            font-weight: 600;
        }

        .file-input-label:hover {
            background: #545b62;
        }

        .form-actions {
            display: flex;
            gap: 15px;
            justify-content: flex-start;
            margin-top: 30px;
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

        .btn-success {
            background-color: #28a745;
            color: white;
        }

        .btn-success:hover {
            background-color: #1e7e34;
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
            .photo-upload {
                flex-direction: column;
                align-items: flex-start;
            }
            
            .form-actions {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="header-content">
            <h1>添加学生信息</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/students">返回列表</a>
                <a href="${pageContext.request.contextPath}/admin/dashboard">仪表板</a>
            </div>
        </div>
    </div>

    <div class="container">
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

        <div class="form-card">
            <div class="card-header">
                学生基本信息
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/students/add" 
                      method="post" enctype="multipart/form-data">
                    
                    <div class="form-group">
                        <label for="student_number">学号 <span class="required">*</span></label>
                        <input type="text" 
                               id="student_number" 
                               name="student_number" 
                               class="form-control" 
                               placeholder="请输入学生学号" 
                               value="${student.student_number}" 
                               required>
                        <div class="form-text">学号必须唯一，不能与其他学生重复</div>
                    </div>

                    <div class="form-group">
                        <label for="student_name">姓名 <span class="required">*</span></label>
                        <input type="text" 
                               id="student_name" 
                               name="student_name" 
                               class="form-control" 
                               placeholder="请输入学生姓名" 
                               value="${student.student_name}" 
                               required>
                    </div>

                    <div class="form-group">
                        <label for="college">学院 <span class="required">*</span></label>
                        <select id="college" name="college" class="form-control" required>
                            <option value="">-- 请选择学院 --</option>
                            <option value="计算机学院" ${student.college == '计算机学院' ? 'selected' : ''}>计算机学院</option>
                            <option value="软件学院" ${student.college == '软件学院' ? 'selected' : ''}>软件学院</option>
                            <option value="艺术学院" ${student.college == '艺术学院' ? 'selected' : ''}>艺术学院</option>
                            <option value="电影学院" ${student.college == '电影学院' ? 'selected' : ''}>电影学院</option>
                            <option value="音乐学院" ${student.college == '音乐学院' ? 'selected' : ''}>音乐学院</option>
                            <option value="经济学院" ${student.college == '经济学院' ? 'selected' : ''}>经济学院</option>
                            <option value="管理学院" ${student.college == '管理学院' ? 'selected' : ''}>管理学院</option>
                            <option value="外语学院" ${student.college == '外语学院' ? 'selected' : ''}>外语学院</option>
                        </select>
                        <div class="form-text">请从下拉列表中选择学生所在学院</div>
                    </div>

                    <div class="form-group">
                        <label for="enrollment_date">入学日期 <span class="required">*</span></label>
                        <input type="date" 
                               id="enrollment_date" 
                               name="enrollment_date" 
                               class="form-control" 
                               value="${student.enrollment_date}" 
                               required>
                        <div class="form-text">请选择学生的入学日期</div>
                    </div>

                    <div class="form-group">
                        <label for="photo">学生照片</label>
                        <div class="photo-upload">
                            <div class="no-photo-preview" id="noPhotoPreview">👤</div>
                            <img src="" alt="照片预览" class="photo-preview" id="photoPreview" style="display: none;">
                            
                            <div class="file-input-wrapper">
                                <input type="file" 
                                       id="photo" 
                                       name="photo" 
                                       accept="image/*"
                                       onchange="previewPhoto(this)">
                                <label for="photo" class="file-input-label">选择照片</label>
                            </div>
                        </div>
                        <div class="form-text">支持 JPG、PNG、WEBP 格式，文件大小不超过 5MB，留空则不上传照片</div>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn btn-success">保存学生信息</button>
                        <a href="${pageContext.request.contextPath}/students" class="btn btn-secondary">取消</a>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        function previewPhoto(input) {
            const preview = document.getElementById('photoPreview');
            const noPhoto = document.getElementById('noPhotoPreview');
            
            if (input.files && input.files[0]) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    preview.src = e.target.result;
                    preview.style.display = 'block';
                    noPhoto.style.display = 'none';
                };
                reader.readAsDataURL(input.files[0]);
            }
        }

        // 设置默认入学日期为今天
        document.addEventListener('DOMContentLoaded', function() {
            const today = new Date();
            const dateString = today.getFullYear() + '-' + 
                             String(today.getMonth() + 1).padStart(2, '0') + '-' + 
                             String(today.getDate()).padStart(2, '0');
            document.getElementById('enrollment_date').value = dateString;
        });
    </script>
</body>
</html> 