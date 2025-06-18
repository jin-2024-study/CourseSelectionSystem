package liu.event;

import liu.entity.OperationLog;
import org.springframework.context.ApplicationEvent;

/**
 * 日志事件类
 */
public class LogEvent extends ApplicationEvent {
    
    private OperationLog operationLog;
    
    public LogEvent(Object source, OperationLog operationLog) {
        super(source);
        this.operationLog = operationLog;
    }
    
    public OperationLog getOperationLog() {
        return operationLog;
    }
    
    public void setOperationLog(OperationLog operationLog) {
        this.operationLog = operationLog;
    }
} 