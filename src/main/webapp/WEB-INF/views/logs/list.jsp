<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>操作日志管理</title>
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

        .search-section {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }

        .search-section h3 {
            margin-bottom: 15px;
            color: #333;
        }

        .search-form-controls {
            display: flex;
            gap: 10px;
            align-items: center;
            flex-wrap: wrap;
        }

        .search-input {
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            flex: 1;
            min-width: 200px;
        }

        .search-btn {
            background: #007bff;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 4px;
            cursor: pointer;
        }

        .search-btn:hover {
            background: #0056b3;
        }

        .clear-btn {
            background: #6c757d;
            color: white;
            text-decoration: none;
            padding: 10px 20px;
            border-radius: 4px;
        }

        .clear-btn:hover {
            background: #545b62;
        }

        /* 缓存管理按钮样式 */
        .cache-controls {
            margin-top: 15px;
            padding-top: 15px;
            border-top: 1px solid #dee2e6;
        }

        .cache-btn {
            background: #28a745;
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 4px;
            cursor: pointer;
            margin-right: 10px;
            text-decoration: none;
            display: inline-block;
            font-size: 12px;
        }

        .cache-btn:hover {
            background: #218838;
            color: white;
        }

        .cache-btn.danger {
            background: #dc3545;
        }

        .cache-btn.danger:hover {
            background: #c82333;
        }

        .cache-btn.info {
            background: #17a2b8;
        }

        .cache-btn.info:hover {
            background: #138496;
        }

        .cache-status {
            margin-top: 10px;
            padding: 10px;
            border-radius: 4px;
            font-size: 12px;
            display: none;
        }

        .cache-status.success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .cache-status.error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .results-info {
            text-align: center;
            margin: 15px 0;
            padding: 10px;
            background: #f8f9fa;
            border-radius: 6px;
            color: #495057;
        }

        .logs-table {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
            margin-bottom: 20px;
        }

        .logs-table table {
            width: 100%;
            border-collapse: collapse;
        }

        .logs-table th,
        .logs-table td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #eee;
        }

        .logs-table th {
            background: #f8f9fa;
            font-weight: 600;
            color: #495057;
        }

        .logs-table tbody tr:hover {
            background: #f8f9fa;
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
            <h1>操作日志管理</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/students">学生管理</a>
                <a href="${pageContext.request.contextPath}/admin/dashboard">返回仪表板</a>
                <a href="${pageContext.request.contextPath}/logout">登出</a>
            </div>
        </div>
    </div>

    <div class="container">
        <!-- 搜索区域 -->
        <div class="search-section">
            <h3>操作日志列表</h3>
            <form class="search-form-controls" method="get" action="${pageContext.request.contextPath}/logs">
                <input type="text" 
                       name="operationUser" 
                       class="search-input" 
                       placeholder="请输入操作用户进行搜索..." 
                       value="${operationUser}">
                       
                <select name="operationType" class="search-input" style="flex: 0 0 150px;">
                    <option value="">所有操作类型</option>
                    <option value="INSERT" ${operationType == 'INSERT' ? 'selected' : ''}>INSERT</option>
                    <option value="UPDATE" ${operationType == 'UPDATE' ? 'selected' : ''}>UPDATE</option>
                    <option value="DELETE" ${operationType == 'DELETE' ? 'selected' : ''}>DELETE</option>
                </select>
                
                <select name="operationModule" class="search-input" style="flex: 0 0 150px;">
                    <option value="">所有模块</option>
                    <option value="学生选课管理" ${operationModule == '学生选课管理' ? 'selected' : ''}>学生选课管理</option>
                    <option value="学生信息管理" ${operationModule == '学生信息管理' ? 'selected' : ''}>学生信息管理</option>
                </select>
                
                <button type="submit" class="search-btn">🔍 搜索</button>
                <a href="${pageContext.request.contextPath}/logs" class="clear-btn">清除搜索</a>
            </form>

            <!-- 缓存管理控制 -->
            <div class="cache-controls">
                <div style="margin-bottom: 10px; color: #666; font-size: 14px;">
                    <strong>Redis缓存管理：</strong>
                    <span style="font-size: 12px; color: #999;">日志查询结果会被缓存以提高性能</span>
                </div>
                <button class="cache-btn info" onclick="getCacheInfo()">🔍 查看缓存状态</button>
                <button class="cache-btn danger" onclick="clearCache()">🗑️ 清除缓存</button>
                <button class="cache-btn" onclick="refreshPage()">🔄 刷新页面</button>
                
                <div id="cacheStatus" class="cache-status"></div>
            </div>
        </div>

        <!-- 搜索结果信息 -->
        <c:if test="${totalCount > 0}">
            <div class="results-info">
                <c:choose>
                    <c:when test="${not empty operationUser || not empty operationType || not empty operationModule}">
                        搜索结果：共找到 ${totalCount} 条日志记录，当前显示第 ${currentPage} 页
                    </c:when>
                    <c:otherwise>
                        共有 ${totalCount} 条日志记录，当前显示第 ${currentPage} 页
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${empty logs}">
                <div class="logs-table">
                    <p style="text-align: center; padding: 40px;">暂无日志记录</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="logs-table">
                    <table>
                        <thead>
                            <tr>
                                <th>操作时间</th>
                                <th>操作类型</th>
                                <th>操作模块</th>
                                <th>操作用户</th>
                                <th>操作方法</th>
                                <th>结果</th>
                                <th>IP地址</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${logs}" var="log">
                                <tr>
                                    <td>${log.formattedOperationTime}</td>
                                    <td>${log.operationType}</td>
                                    <td>${log.operationModule}</td>
                                    <td>${log.operationUser}</td>
                                    <td>${log.operationMethod}</td>
                                    <td>${log.operationResult}</td>
                                    <td>${log.operationIp}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
        
        <!-- 分页功能 -->
        <c:if test="${totalCount > 0 && totalPages > 1}">
            <div class="pagination-section">
                <div class="pagination">
                    <!-- 构建查询参数 -->
                    <c:set var="queryParams" value="" />
                    <c:if test="${not empty operationUser}">
                        <c:set var="queryParams" value="${queryParams}&operationUser=${operationUser}" />
                    </c:if>
                    <c:if test="${not empty operationType}">
                        <c:set var="queryParams" value="${queryParams}&operationType=${operationType}" />
                    </c:if>
                    <c:if test="${not empty operationModule}">
                        <c:set var="queryParams" value="${queryParams}&operationModule=${operationModule}" />
                    </c:if>
                    
                    <!-- 上一页 -->
                    <c:choose>
                        <c:when test="${currentPage > 1}">
                            <a href="${pageContext.request.contextPath}/logs?page=${currentPage - 1}${queryParams}">‹ 上一页</a>
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
                                <a href="${pageContext.request.contextPath}/logs?page=${pageNum}${queryParams}">${pageNum}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    
                    <!-- 下一页 -->
                    <c:choose>
                        <c:when test="${currentPage < totalPages}">
                            <a href="${pageContext.request.contextPath}/logs?page=${currentPage + 1}${queryParams}">下一页 ›</a>
                        </c:when>
                        <c:otherwise>
                            <span class="disabled">下一页 ›</span>
                        </c:otherwise>
                    </c:choose>
                </div>
                
                <div style="margin-top: 10px; color: #6c757d; font-size: 14px;">
                    第 ${currentPage} 页，共 ${totalPages} 页，每页显示 10 条记录
                </div>
            </div>
        </c:if>
    </div>

    <script>
        // 获取缓存状态信息
        function getCacheInfo() {
            fetch('${pageContext.request.contextPath}/logs/cache/info')
                .then(response => response.json())
                .then(data => {
                    const statusDiv = document.getElementById('cacheStatus');
                    if (data.success) {
                        statusDiv.className = 'cache-status success';
                        statusDiv.style.display = 'block';
                        statusDiv.innerHTML = `
                            <strong>缓存状态：</strong><br>
                            分页缓存: ${data.hasPageCache ? '✅ 已缓存' : '❌ 未缓存'}<br>
                            统计信息缓存: ${data.hasStatisticsCache ? '✅ 已缓存' : '❌ 未缓存'}
                        `;
                    } else {
                        statusDiv.className = 'cache-status error';
                        statusDiv.style.display = 'block';
                        statusDiv.innerHTML = '<strong>获取缓存信息失败：</strong>' + data.message;
                    }
                })
                .catch(error => {
                    const statusDiv = document.getElementById('cacheStatus');
                    statusDiv.className = 'cache-status error';
                    statusDiv.style.display = 'block';
                    statusDiv.innerHTML = '<strong>请求失败：</strong>' + error.message;
                });
        }

        // 清除缓存
        function clearCache() {
            if (confirm('确定要清除所有日志相关的缓存吗？')) {
                fetch('${pageContext.request.contextPath}/logs/cache/clear')
                    .then(response => response.json())
                    .then(data => {
                        const statusDiv = document.getElementById('cacheStatus');
                        if (data.success) {
                            statusDiv.className = 'cache-status success';
                            statusDiv.style.display = 'block';
                            statusDiv.innerHTML = '<strong>✅ ' + data.message + '</strong>';
                        } else {
                            statusDiv.className = 'cache-status error';
                            statusDiv.style.display = 'block';
                            statusDiv.innerHTML = '<strong>❌ ' + data.message + '</strong>';
                        }
                    })
                    .catch(error => {
                        const statusDiv = document.getElementById('cacheStatus');
                        statusDiv.className = 'cache-status error';
                        statusDiv.style.display = 'block';
                        statusDiv.innerHTML = '<strong>请求失败：</strong>' + error.message;
                    });
            }
        }

        // 刷新页面
        function refreshPage() {
            location.reload();
        }

        // 页面加载完成后隐藏状态信息
        document.addEventListener('DOMContentLoaded', function() {
            setTimeout(function() {
                const statusDiv = document.getElementById('cacheStatus');
                if (statusDiv.style.display === 'block') {
                    statusDiv.style.display = 'none';
                }
            }, 5000);
        });
    </script>
</body>
</html> 