package liu.util;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * SQL执行时间拦截器
 * 记录执行时间超过60ms的SQL到日志文件
 */
@Intercepts({
    @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class SqlTimeInterceptor implements Interceptor {
    
    private static final Logger logger = LogManager.getLogger(SqlTimeInterceptor.class);
    private static final long SLOW_SQL_THRESHOLD = 60; // 慢SQL阈值，单位：毫秒
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取StatementHandler对象
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        
        // 获取SQL信息
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();
        Object parameterObject = boundSql.getParameterObject();
        
        // 获取MappedStatement对象，用于获取mapper信息
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        String mapperMethod = mappedStatement.getId(); // 获取执行的mapper方法
        
        // 记录开始时间
        long startTime = System.currentTimeMillis();
        Date startDate = new Date(startTime);
        String startTimeStr = dateFormat.format(startDate);
        
        // 执行目标方法
        Object result = invocation.proceed();
        
        // 记录结束时间
        long endTime = System.currentTimeMillis();
        Date endDate = new Date(endTime);
        String endTimeStr = dateFormat.format(endDate);
        
        // 计算执行时间
        long executionTime = endTime - startTime;
        
        // 如果执行时间超过阈值，记录慢SQL日志
        if (executionTime > SLOW_SQL_THRESHOLD) {
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("慢SQL检测: \n");
            logMessage.append("执行对象: ").append(mapperMethod).append("\n");
            logMessage.append("执行开始时间: ").append(startTimeStr).append("\n");
            logMessage.append("执行结束时间: ").append(endTimeStr).append("\n");
            logMessage.append("执行时间: ").append(executionTime).append("ms\n");
            logMessage.append("执行的SQL: ").append(sql).append("\n");
            logMessage.append("参数: ").append(parameterObject).append("\n");
            
            logger.info(logMessage.toString());
        }
        
        return result;
    }
    
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
    
    @Override
    public void setProperties(Properties properties) {
    }
} 