package liu.listener;

import liu.event.LogEvent;
import liu.service.OperationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 日志事件监听器
 */
@Component
public class LogEventListener {

    private static final Logger logger = LoggerFactory.getLogger(LogEventListener.class);

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 异步处理日志事件
     */
    @EventListener
    @Async
    public void handleLogEvent(LogEvent logEvent) {
        try {
            // 保存操作日志到数据库
            operationLogService.save(logEvent.getOperationLog());
            logger.info("操作日志已记录: {}", logEvent.getOperationLog().toString());
        } catch (Exception e) {
            logger.error("保存操作日志失败: {}", e.getMessage(), e);
        }
    }
} 