<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>上传学生照片</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .upload-container {
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
            background-color: #f9f9f9;
        }
        .preview-container {
            margin-top: 20px;
            text-align: center;
        }
        .preview-image {
            max-width: 300px;
            max-height: 300px;
            border: 1px solid #ddd;
            display: none;
        }
        .form-row {
            margin-bottom: 15px;
        }
        .form-row label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        .btn-container {
            margin-top: 20px;
            display: flex;
            justify-content: space-between;
        }
        .current-photo {
            margin: 20px 0;
            padding: 15px;
            background-color: white;
            border-radius: 5px;
            border: 1px solid #eee;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        .photo-display {
            text-align: center;
            margin-bottom: 15px;
        }
        .photo-actions {
            text-align: center;
            margin-top: 10px;
        }
        .btn-download {
            display: inline-block;
            background-color: #17a2b8;
            color: white;
            padding: 8px 16px;
            text-decoration: none;
            border-radius: 4px;
            font-weight: bold;
            transition: all 0.3s;
        }
        .btn-download:hover {
            background-color: #138496;
            transform: translateY(-2px);
            box-shadow: 0 2px 5px rgba(0,0,0,0.2);
        }
        .icon-download {
            display: inline-block;
            width: 16px;
            height: 16px;
            margin-right: 5px;
            background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="white"><path d="M8 9.44l3.22-3.22 1.06 1.06L8 11.56 3.72 7.28l1.06-1.06L8 9.44z"/><path d="M2 14h12v-2H2v2z"/></svg>');
            background-size: cover;
            vertical-align: text-top;
        }
        .error-message {
            color: #721c24;
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 4px;
        }
    </style>
</head>
<body>
    <div class="upload-container">
        <h2>上传学生照片</h2>
        
        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>
        
        <div class="student-info">
            <h3>学生信息</h3>
            <p><strong>学号:</strong> ${student.student_number}</p>
            <p><strong>姓名:</strong> ${student.student_name}</p>
            <p><strong>学院:</strong> ${student.college}</p>
        </div>
        
        <c:if test="${not empty student.photo_path}">
            <div class="current-photo">
                <h3>当前照片</h3>
                <div class="photo-display">
                    <img src="${pageContext.request.contextPath}/students/${student.student_id}/photo" 
                         alt="学生照片" style="max-width: 300px; max-height: 300px; border: 1px solid #ddd; border-radius: 4px;">
                </div>
                <div class="photo-actions">
                    <a href="${pageContext.request.contextPath}/students/${student.student_id}/photo/download" 
                       class="btn-download">
                       <i class="icon-download"></i> 下载照片
                    </a>
                </div>
            </div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/students/${student.student_id}/photo" method="post" enctype="multipart/form-data">
            <div class="form-row">
                <label for="photo">选择新照片:</label>
                <input type="file" id="photo" name="photo" accept="image/*" onchange="previewImage(this)">
            </div>
            
            <div class="preview-container">
                <h3>照片预览</h3>
                <img id="preview" class="preview-image" alt="照片预览">
            </div>
            
            <div class="btn-container">
                <a href="${pageContext.request.contextPath}/students/${student.student_id}" class="btn btn-secondary">取消</a>
                <button type="submit" class="btn btn-primary">上传照片</button>
            </div>
        </form>
    </div>
    
    <script>
        function previewImage(input) {
            const preview = document.getElementById('preview');
            if (input.files && input.files[0]) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    preview.src = e.target.result;
                    preview.style.display = 'block';
                };
                reader.readAsDataURL(input.files[0]);
            } else {
                preview.style.display = 'none';
            }
        }
    </script>
</body>
</html> 