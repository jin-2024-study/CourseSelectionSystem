# 课程选课系统 - 系统配置文档

## 📋 项目概述

课程选课系统是一个基于Spring Boot + MyBatis + Spring Security的企业级Web应用程序，
支持普通用户系统管理、管理员系统管理等功能。

### 基本信息
- **项目名称**: CourseSelectionSystem
- **版本**: 1.0-SNAPSHOT  
- **架构**: Spring Boot + MyBatis + Spring Security
- **部署方式**: Jar包部署
- **开发语言**: Java

## 🛠️ 系统环境要求

### 硬件要求
- **CPU**: 双核2.0GHz以上
- **内存**: 4GB以上（推荐8GB）
- **硬盘**: 20GB以上可用空间
- **网络**: 稳定的网络连接

### 软件环境
| 组件 | 版本要求 | 推荐版本 | 说明 |
|------|----------|----------|------|
| Java | JDK 17+ | OpenJDK 17.0.11 | 必须，运行环境 |
| Maven | 3.6+ | Apache Maven 3.9.4 | 必须，构建工具 |
| MySQL | 8.0+ | MySQL 8.0.28 | 必须，主数据库 |
| Redis | 6.0+ | Redis 7.0.12 | 必须，缓存服务 |
| ActiveMQ | 5.17+ | ActiveMQ 5.17.6 | 可选，消息队列 |

## 🔧 技术栈详细配置

### 1. Java 环境配置

#### JDK 安装与配置
```bash
# 检查Java版本
java -version

# 设置JAVA_HOME环境变量（Windows）
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

# 设置JAVA_HOME环境变量（Linux/Mac）
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export PATH=$JAVA_HOME/bin:$PATH
```

#### JVM 参数配置
```bash
# 开发环境JVM参数
-Xms512m -Xmx2g -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m

# 生产环境JVM参数  
-Xms2g -Xmx4g -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=1g
-XX:+UseG1GC -XX:MaxGCPauseMillis=200
-XX:+PrintGCDetails -XX:+PrintGCTimeStamps
-Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai
```

### 2. Maven 配置

#### Maven 安装
```bash
# 下载并解压Maven
wget https://archive.apache.org/dist/maven/maven-3/3.9.4/binaries/apache-maven-3.9.4-bin.tar.gz
tar -zxvf apache-maven-3.9.4-bin.tar.gz

# 设置Maven环境变量
export MAVEN_HOME=/opt/apache-maven-3.9.4
export PATH=$MAVEN_HOME/bin:$PATH
```

#### settings.xml 配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 
                              http://maven.apache.org/xsd/settings-1.0.0.xsd">
    
    <!-- 本地仓库路径 -->
    <localRepository>D:\maven\repository</localRepository>
    
    <!-- 镜像配置 -->
    <mirrors>
        <mirror>
            <id>aliyun</id>
            <name>Aliyun Maven Repository</name>
            <url>https://maven.aliyun.com/repository/public</url>
            <mirrorOf>central</mirrorOf>
        </mirror>
    </mirrors>
    
    <!-- JDK配置 -->
    <profiles>
        <profile>
            <id>jdk-17</id>
            <activation>
                <activeByDefault>true</activeByDefault>
                <jdk>17</jdk>
            </activation>
            <properties>
                <maven.compiler.source>17</maven.compiler.source>
                <maven.compiler.target>17</maven.compiler.target>
                <maven.compiler.compilerVersion>17</maven.compiler.compilerVersion>
            </properties>
        </profile>
    </profiles>
</settings>
```

#### 项目POM配置关键点
```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>

<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.14</version>
    <relativePath/>
</parent>
```

### 3. MySQL 数据库配置

#### 安装与基础配置
```sql
-- 创建数据库
CREATE DATABASE courseselectionsystem 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 创建用户（可选）
CREATE USER 'courseselection'@'localhost' IDENTIFIED BY 'StrongPassword123!';
GRANT ALL PRIVILEGES ON courseselectionsystem.* TO 'courseselection'@'localhost';
FLUSH PRIVILEGES;
```

#### my.cnf 配置建议
```ini
[mysqld]
# 基础配置
port = 3306
bind-address = 0.0.0.0
default-storage-engine = InnoDB

# 字符集配置
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci

# 连接配置
max_connections = 1000
max_connect_errors = 6000
max_allowed_packet = 64M

# InnoDB配置
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M
innodb_flush_log_at_trx_commit = 1
innodb_lock_wait_timeout = 120

# 日志配置
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 2
```

#### 应用数据源配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/courseselectionsystem?serverTimezone=UTC&characterEncoding=utf8&useUnicode=true&useSSL=false
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource
```

### 4. Redis 缓存配置

#### Redis 安装
```bash
# CentOS/RHEL
yum install redis -y

# Ubuntu/Debian  
apt-get install redis-server -y

# 启动Redis
systemctl start redis
systemctl enable redis
```

#### redis.conf 配置
```conf
# 基础配置
port 6379
bind 127.0.0.1
timeout 300
tcp-keepalive 300

# 内存配置
maxmemory 2gb
maxmemory-policy allkeys-lru

# 持久化配置
save 900 1
save 300 10
save 60 10000
rdbcompression yes
dbfilename dump.rdb

# AOF配置
appendonly yes
appendfilename "appendonly.aof"
appendfsync everysec

# 安全配置
# requirepass your_password_here

# 日志配置
loglevel notice
logfile /var/log/redis/redis-server.log
```

#### Spring Boot Redis配置
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: # 如果有密码请填写
      timeout: 2000ms
      database: 0
      lettuce:
        pool:
          max-active: 20    # 最大连接数
          max-wait: -1ms    # 最大等待时间
          max-idle: 10      # 最大空闲连接
          min-idle: 0       # 最小空闲连接
```

### 5. ActiveMQ 消息队列配置

#### ActiveMQ 安装
```bash
# 下载ActiveMQ
wget https://archive.apache.org/dist/activemq/5.17.6/apache-activemq-5.17.6-bin.tar.gz
tar -zxvf apache-activemq-5.17.6-bin.tar.gz

# 启动ActiveMQ
cd apache-activemq-5.17.6
bin/activemq start

# 管理控制台：http://localhost:8161/admin
# 默认用户名/密码：admin/admin
```

#### ActiveMQ 配置
```xml
<!-- activemq.xml 配置示例 -->
<broker xmlns="http://activemq.apache.org/schema/core" 
        brokerName="localhost" 
        dataDirectory="${activemq.data}">
    
    <destinationPolicy>
        <policyMap>
            <policyEntries>
                <policyEntry topic=">" >
                    <pendingMessageLimitStrategy>
                        <constantPendingMessageLimitStrategy limit="1000"/>
                    </pendingMessageLimitStrategy>
                </policyEntry>
            </policyEntries>
        </policyMap>
    </destinationPolicy>
    
    <managementContext>
        <managementContext createConnector="false"/>
    </managementContext>
    
    <persistenceAdapter>
        <kahaDB directory="${activemq.data}/kahadb"/>
    </persistenceAdapter>
    
    <transportConnectors>
        <transportConnector name="openwire" uri="tcp://0.0.0.0:61616?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
        <transportConnector name="amqp" uri="amqp://0.0.0.0:5672?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
        <transportConnector name="stomp" uri="stomp://0.0.0.0:61613?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
        <transportConnector name="mqtt" uri="mqtt://0.0.0.0:1883?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
        <transportConnector name="ws" uri="ws://0.0.0.0:61614?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
    </transportConnectors>
    
    <shutdownHooks>
        <bean xmlns="http://www.springframework.org/schema/beans" 
              class="org.apache.activemq.hooks.SpringContextHook" />
    </shutdownHooks>
</broker>
```

#### Spring Boot ActiveMQ配置
```yaml
spring:
  activemq:
    broker-url: tcp://localhost:61616
    user: admin
    password: admin
    in-memory: false
    pool:
      enabled: true
      max-connections: 50
      idle-timeout: 30s
```

## 🚀 部署配置

### 1. 开发环境部署

#### 克隆项目
```bash
git clone https://github.com/your-repo/CourseSelectionSystem.git
cd CourseSelectionSystem
```

#### 配置数据库
```bash
# 导入数据库脚本
mysql -u root -p < database/init.sql
```

#### 启动应用
```bash
# 方式1：使用Maven插件
mvn spring-boot:run

# 方式2：打包后运行
mvn clean package -DskipTests
java -jar target/CourseSelectionSystem.jar

# 方式3：IDE中直接运行主类
# liu.CourseSelectionSystemApplication
```

### 2. 生产环境部署

#### 系统服务配置
```bash
# 创建系统服务文件
sudo vim /etc/systemd/system/courseselection.service
```

```ini
[Unit]
Description=Course Selection System
After=network.target

[Service]
Type=simple
User=courseselection
ExecStart=/usr/bin/java -jar /opt/courseselection/CourseSelectionSystem.jar
Restart=always
RestartSec=10
StandardOutput=syslog
StandardError=syslog
SyslogIdentifier=courseselection

[Install]
WantedBy=multi-user.target
```

#### Nginx 反向代理配置
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 文件上传大小限制
        client_max_body_size 100M;
    }
    
    # 静态资源缓存
    location ~* \.(css|js|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

### 3. Docker 部署配置

#### Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim

LABEL maintainer="your-email@example.com"
LABEL description="Course Selection System"

# 工作目录
WORKDIR /app

# 复制jar文件
COPY target/CourseSelectionSystem.jar app.jar

# 创建用户
RUN addgroup --system courseselection && adduser --system --group courseselection

# 创建上传目录
RUN mkdir -p /app/uploads && chown -R courseselection:courseselection /app

# 切换用户
USER courseselection

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/CourseSelectionSystem_war/health || exit 1

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### docker-compose.yml
```yaml
version: '3.8'

services:
  courseselection:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/courseselectionsystem
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=rootpassword
      - SPRING_DATA_REDIS_HOST=redis
    depends_on:
      - mysql
      - redis
    volumes:
      - ./uploads:/app/uploads
    restart: unless-stopped

  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=rootpassword
      - MYSQL_DATABASE=courseselectionsystem
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./database/init.sql:/docker-entrypoint-initdb.d/init.sql
    restart: unless-stopped

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    restart: unless-stopped

  activemq:
    image: webcenter/activemq:5.14.3
    ports:
      - "61616:61616"
      - "8161:8161"
    environment:
      - ACTIVEMQ_ADMIN_LOGIN=admin
      - ACTIVEMQ_ADMIN_PASSWORD=admin
    restart: unless-stopped

volumes:
  mysql_data:
  redis_data:
```

## ⚙️ 应用配置详解

### 1. application.yml 完整配置
```yaml
# Spring Boot 应用配置
spring:
  application:
    name: CourseSelectionSystem
  
  # 允许循环依赖
  main:
    allow-circular-references: true
  
  # 配置文件
  profiles:
    active: development
  
  # 数据源配置
  datasource:
    url: jdbc:mysql://localhost:3306/courseselectionsystem?serverTimezone=UTC&characterEncoding=utf8&useUnicode=true&useSSL=false
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource
    
    # Druid连接池配置
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20
      max-wait: 60000
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 300000
      validation-query: SELECT 1 FROM DUAL
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      pool-prepared-statements: true
      max-pool-prepared-statement-per-connection-size: 20
      
      # 监控配置
      stat-view-servlet:
        enabled: true
        url-pattern: /druid/*
        login-username: admin
        login-password: admin
      
      web-stat-filter:
        enabled: true
        url-pattern: /*
        exclusions: "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*"
  
  # JSP视图配置
  mvc:
    view:
      prefix: /WEB-INF/views/
      suffix: .jsp
    static-path-pattern: /static/**
  
  # 静态资源配置
  web:
    resources:
      static-locations: classpath:/static/
      cache:
        period: 31536000
  
  # 文件上传配置
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 100MB
      location: ${java.io.tmpdir}
  
  # Redis配置
  data:
    redis:
      host: localhost
      port: 6379
      password: 
      timeout: 2000ms
      database: 0
      lettuce:
        pool:
          max-active: 20
          max-wait: -1ms
          max-idle: 10
          min-idle: 0
        shutdown-timeout: 100ms
  
  # ActiveMQ配置
  activemq:
    broker-url: tcp://localhost:61616
    user: admin
    password: admin
    in-memory: false
    pool:
      enabled: true
      max-connections: 50
      idle-timeout: 30s
      block-if-full: true
      block-if-full-timeout: -1ms
  
  # JMS配置
  jms:
    template:
      default-destination: course.selection.queue
      delivery-mode: persistent
      priority: 100
      qos-enabled: true
      receive-timeout: 1000ms
      time-to-live: 36000000ms

# 服务器配置
server:
  port: 8080
  servlet:
    context-path: /CourseSelectionSystem_war
    session:
      persistent: false
      timeout: 30m
      store-type: none
      cookie:
        max-age: 1800
        http-only: true
        secure: false
  
  # Tomcat配置
  tomcat:
    basedir: target/tomcat
    threads:
      max: 200
      min-spare: 10
    connection-timeout: 20000ms
    max-swallow-size: 100MB
    max-http-post-size: 100MB
    uri-encoding: UTF-8
    
  # 错误页面配置
  error:
    whitelabel:
      enabled: false
    include-message: always
    include-binding-errors: always
    include-stacktrace: never
    include-exception: true

# MyBatis配置
mybatis:
  mapper-locations: classpath:liu/mapper/*.xml
  type-aliases-package: liu.entity
  configuration:
    map-underscore-to-camel-case: true
    lazy-loading-enabled: true
    aggressive-lazy-loading: false
    cache-enabled: true
    call-setters-on-nulls: true
    jdbc-type-for-null: NULL
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

# 日志配置
logging:
  level:
    root: INFO
    liu: DEBUG
    liu.dao: DEBUG
    liu.mapper: DEBUG
    com.alibaba.druid: DEBUG
    org.springframework.security: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{50} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{50} - %msg%n"
  file:
    name: logs/courseselection.log
    max-size: 100MB
    max-history: 30

# 文件上传配置
file:
  upload-dir: uploads
  max-size: 10MB
  allowed-types: jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx

# 管理端点配置
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized
```

### 2. 不同环境配置

#### application-development.yml
```yaml
# 开发环境配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/courseselectionsystem_dev?serverTimezone=UTC&characterEncoding=utf8&useUnicode=true&useSSL=false
  
  data:
    redis:
      host: localhost
      port: 6379

logging:
  level:
    root: DEBUG
    liu: DEBUG

server:
  port: 8080
```

#### application-production.yml
```yaml
# 生产环境配置
spring:
  datasource:
    url: jdbc:mysql://prod-db-server:3306/courseselectionsystem?serverTimezone=UTC&characterEncoding=utf8&useUnicode=true&useSSL=true
    username: ${DB_USERNAME:courseselection}
    password: ${DB_PASSWORD}
  
  data:
    redis:
      host: ${REDIS_HOST:redis-server}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD}

logging:
  level:
    root: INFO
    liu: INFO
  file:
    name: /var/log/courseselection/application.log

server:
  port: ${SERVER_PORT:8080}
```

## 🔍 性能调优配置

### 1. JVM 调优参数
```bash
# 生产环境JVM参数
-server
-Xms4g -Xmx4g
-XX:NewRatio=3
-XX:SurvivorRatio=8
-XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=1g
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:G1HeapRegionSize=16m
-XX:+G1UseAdaptiveIHOP
-XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps
-XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=10M
-Xloggc:/var/log/gc/gc.log
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/var/log/heapdump/
-Dfile.encoding=UTF-8
-Duser.timezone=Asia/Shanghai
```

### 2. 数据库连接池调优
```yaml
spring:
  datasource:
    druid:
      initial-size: 10
      min-idle: 10
      max-active: 50
      max-wait: 60000
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 300000
      validation-query: SELECT 1
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      pool-prepared-statements: true
      max-pool-prepared-statement-per-connection-size: 20
```

### 3. Redis 性能配置
```yaml
spring:
  data:
    redis:
      lettuce:
        pool:
          max-active: 50
          max-wait: 3000ms
          max-idle: 20
          min-idle: 5
        shutdown-timeout: 100ms
      timeout: 3000ms
```

## 🛡️ 安全配置

### 1. Spring Security 配置要点
- 密码加密使用BCrypt
- 会话管理防止固定攻击
- CSRF保护配置
- 角色权限控制

### 2. 数据库安全
- 使用专用数据库用户
- 限制数据库用户权限
- 定期备份数据
- 启用数据库审计日志

### 3. Redis 安全
- 设置Redis密码
- 禁用危险命令
- 限制Redis访问IP
- 定期更新Redis版本

## 📊 监控配置

### 1. 应用监控
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
    metrics:
      enabled: true
```

### 2. 数据库监控
- Druid监控面板：http://localhost:8080/druid
- 慢查询日志监控
- 连接池状态监控

### 3. 日志监控
- 应用日志轮转配置
- 错误日志告警配置
- 访问日志分析

## 🔧 常见问题排查

### 1. 启动失败
```bash
# 检查端口占用
netstat -an | grep 8080

# 检查Java版本
java -version

# 检查数据库连接
mysql -h localhost -u root -p
```

### 2. 内存溢出
```bash
# 查看堆内存使用情况
jstat -gc <pid>

# 生成堆转储文件
jmap -dump:format=b,file=heapdump.hprof <pid>
```

### 3. 数据库连接异常
```bash
# 检查数据库状态
systemctl status mysql

# 检查连接数
show processlist;
```

## 📞 联系信息

- **项目地址**: https://github.com/jin-2024-study/CourseSelectionSystem.git/
- **文档更新**: 2025-06-18

---

> **注意**: 请根据实际部署环境调整配置参数，生产环境务必修改默认密码和敏感配置信息。 