<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>实时通知 - 选课管理系统</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .notification-container {
            max-height: 600px;
            overflow-y: auto;
        }
        
        .notification-item {
            border-left: 4px solid #007bff;
            margin-bottom: 15px;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 0 8px 8px 0;
            transition: all 0.3s ease;
        }
        
        .notification-item.success {
            border-left-color: #28a745;
            background: #d4edda;
        }
        
        .notification-item.error {
            border-left-color: #dc3545;
            background: #f8d7da;
        }
        
        .notification-item.warning {
            border-left-color: #ffc107;
            background: #fff3cd;
        }
        
        .notification-item.info {
            border-left-color: #17a2b8;
            background: #d1ecf1;
        }
        
        .notification-item:hover {
            transform: translateX(5px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        
        .notification-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;
        }
        
        .notification-title {
            font-weight: bold;
            margin: 0;
        }
        
        .notification-time {
            font-size: 0.85em;
            color: #6c757d;
        }
        
        .notification-message {
            margin: 0;
            line-height: 1.5;
        }
        
        .notification-details {
            margin-top: 10px;
            font-size: 0.9em;
            color: #495057;
        }
        
        .clear-btn {
            position: fixed;
            bottom: 20px;
            right: 20px;
            z-index: 1000;
        }
        
        .notification-icon {
            margin-right: 10px;
        }
        
        .empty-state {
            text-align: center;
            padding: 50px 20px;
            color: #6c757d;
        }
        
        .empty-state i {
            font-size: 4rem;
            margin-bottom: 20px;
            opacity: 0.5;
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <div class="col-12">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <div>
                        <h2><i class="fas fa-bell"></i> 实时通知中心</h2>
                        <c:choose>
                            <c:when test="${isAdmin}">
                                <small class="text-muted">管理员视图 - 显示"新学期开放选课操作"相关记录</small>
                            </c:when>
                            <c:otherwise>
                                <small class="text-muted">用户视图 - 显示"新学期开放选课"相关记录</small>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div>
                        <a href="${pageContext.request.contextPath}/courseSelection" class="btn btn-primary">
                            <i class="fas fa-arrow-left"></i> 返回选课系统
                        </a>
                    </div>
                </div>
                
                <!-- 通知列表 -->
                <div id="notificationContainer" class="notification-container">
                    <div id="emptyState" class="empty-state">
                        <i class="fas fa-bell-slash"></i>
                        <h4>暂无通知</h4>
                        <p>当有新的选课通知时，会在这里显示</p>
                    </div>
                </div>
                
                <!-- 清空按钮 -->
                <button id="clearBtn" class="btn btn-danger clear-btn" onclick="clearNotifications()" style="display: none;">
                    <i class="fas fa-trash"></i> 清空通知
                </button>
            </div>
        </div>
    </div>

    <!-- 引入必要的JavaScript库 -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.2/sockjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
    
    <script>
        let stompClient = null;
        let notificationCount = 0;
        const userIsAdmin = <c:out value="${isAdmin}" default="false"/>; // 从服务端传递的用户角色信息
        
        // 页面加载完成后连接WebSocket
        document.addEventListener('DOMContentLoaded', function() {
            connect();
            loadHistoryNotifications();
        });
        
        // 连接WebSocket
        function connect() {
            const socket = new SockJS('<%= request.getContextPath() %>/ws-notifications');
            stompClient = Stomp.over(socket);
            
            // 禁用调试信息
            stompClient.debug = null;
            
            stompClient.connect({}, function(frame) {
                // 订阅通知主题
                stompClient.subscribe('/topic/notifications', function(notification) {
                    try {
                        const notificationData = JSON.parse(notification.body);
                        displayNotification(notificationData);
                    } catch (e) {
                        console.error('解析通知消息失败:', e);
                    }
                });
                
            }, function(error) {
                console.error('WebSocket连接错误: ' + error);
                // 5秒后重新连接
                setTimeout(connect, 5000);
            });
        }
        
        // 断开连接
        function disconnect() {
            if (stompClient !== null) {
                stompClient.disconnect();
            }
        }
        
        // 显示通知 - 根据用户角色过滤
        function displayNotification(notification) {
            // 根据用户角色过滤通知
            if (userIsAdmin) {
                // 管理员只显示"新学期开放选课操作"相关记录
                if (notification.title !== "新学期开放选课操作") {
                    return; // 不显示该通知
                }
            } else {
                // 普通用户只显示"新学期开放选课"相关记录
                if (notification.title !== "新学期开放选课") {
                    return; // 不显示该通知
                }
            }
            
            const container = document.getElementById('notificationContainer');
            const emptyState = document.getElementById('emptyState');
            const clearBtn = document.getElementById('clearBtn');
            
            // 隐藏空状态
            if (emptyState) {
                emptyState.style.display = 'none';
            }
            
            // 显示清空按钮
            clearBtn.style.display = 'block';
            
            // 创建通知元素
            const notificationElement = createNotificationElement(notification);
            
            // 添加到容器顶部
            container.insertBefore(notificationElement, container.firstChild);
            
            // 增加计数
            notificationCount++;
            
            // 添加动画效果
            setTimeout(() => {
                notificationElement.style.opacity = '1';
                notificationElement.style.transform = 'translateX(0)';
            }, 100);
        }
        
        // 创建通知元素
        function createNotificationElement(notification) {
            const div = document.createElement('div');
            div.className = 'notification-item ' + getNotificationClass(notification.type);
            div.style.opacity = '0';
            div.style.transform = 'translateX(-20px)';
            div.style.transition = 'all 0.3s ease';
            
            const icon = getNotificationIcon(notification.type);
            
            // 处理时间戳
            let time = '';
            try {
                if (notification.timestamp) {
                    let date = new Date(notification.timestamp);
                    if (date && !isNaN(date.getTime())) {
                        time = date.toLocaleString('zh-CN');
                    } else {
                        time = '刚刚';
                    }
                } else {
                    time = '刚刚';
                }
            } catch (e) {
                time = '刚刚';
            }
            
            // 处理管理员通知（没有学生信息）
            let detailsHtml = '';
            if (notification.studentName && notification.studentName !== '系统通知' && notification.studentName !== '管理员操作记录') {
                detailsHtml = '<div class="notification-details">' +
                    '<strong>学生:</strong> ' + notification.studentName + ' (' + notification.studentNumber + ')' +
                    '</div>';
            }
            
            // 添加课程信息（如果有）
            if (notification.courseInfo) {
                detailsHtml += '<div class="notification-details">' +
                    '<strong>课程信息:</strong> ' + notification.courseInfo + '<br>' +
                    '<strong>学年学期:</strong> ' + notification.academicYear + ' ' + notification.semester +
                    '</div>';
            } else if (notification.academicYear && notification.semester) {
                detailsHtml += '<div class="notification-details">' +
                    '<strong>学年学期:</strong> ' + notification.academicYear + ' ' + notification.semester +
                    '</div>';
            }
            
            div.innerHTML = '<div class="notification-header">' +
                '<h5 class="notification-title">' +
                '<i class="' + icon + ' notification-icon"></i>' +
                notification.title +
                '</h5>' +
                '<span class="notification-time">' + time + '</span>' +
                '</div>' +
                '<p class="notification-message">' + notification.message + '</p>' +
                detailsHtml;
            
            return div;
        }
        
        // 获取通知样式类
        function getNotificationClass(type) {
            switch(type) {
                case 'COURSE_SELECTION_SUCCESS':
                    return 'success';
                case 'COURSE_SELECTION_FAILED':
                    return 'error';
                case 'COURSE_CHANGE':
                    return 'warning';
                case 'COURSE_DELETION':
                    return 'info';
                case 'NEW_SEMESTER_AVAILABLE':
                    return 'success';
                default:
                    return 'info';
            }
        }
        
        // 获取通知图标
        function getNotificationIcon(type) {
            switch(type) {
                case 'COURSE_SELECTION_SUCCESS':
                    return 'fas fa-check-circle';
                case 'COURSE_SELECTION_FAILED':
                    return 'fas fa-times-circle';
                case 'COURSE_CHANGE':
                    return 'fas fa-edit';
                case 'COURSE_DELETION':
                    return 'fas fa-trash-alt';
                case 'NEW_SEMESTER_AVAILABLE':
                    return 'fas fa-calendar-plus';
                default:
                    return 'fas fa-info-circle';
            }
        }
        
        // 清空通知
        function clearNotifications() {
            const container = document.getElementById('notificationContainer');
            const emptyState = document.getElementById('emptyState');
            const clearBtn = document.getElementById('clearBtn');
            
            // 清空容器
            container.innerHTML = '';
            
            // 显示空状态
            container.appendChild(emptyState);
            emptyState.style.display = 'block';
            
            // 隐藏清空按钮
            clearBtn.style.display = 'none';
            
            // 重置计数
            notificationCount = 0;
        }
        
        // 加载历史通知
        function loadHistoryNotifications() {
            fetch('<%= request.getContextPath() %>/notifications/api/all?limit=50')
                .then(response => response.json())
                .then(notifications => {
                    if (notifications.length > 0) {
                        // 倒序显示通知（最新的在上面）
                        notifications.reverse().forEach((notification) => {
                            displayNotification(notification);
                        });
                    }
                })
                .catch(error => {
                    console.error('加载历史通知失败:', error);
                });
        }
        
        // 页面卸载时断开连接
        window.addEventListener('beforeunload', function() {
            disconnect();
        });
    </script>
</body>
</html> 