package liu.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 操作日志实体类
 */
public class OperationLog {
    
    private Integer logId;
    private String operationType;     // INSERT, UPDATE, DELETE
    private String operationModule;   // 操作模块
    private String operationMethod;   // 操作方法
    private String operationParams;   // 操作参数
    private String operationResult;   // SUCCESS, FAILURE
    private String errorMessage;      // 错误信息
    private String operationUser;     // 操作用户
    private String operationIp;       // 操作IP
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTime; // 操作时间
    private String targetId;          // 目标对象ID
    private String targetType;        // 目标对象类型
    private String oldData;           // 操作前数据
    private String newData;           // 操作后数据

    public OperationLog() {}

    public OperationLog(String operationType, String operationModule, String operationMethod,
                       String operationParams, String operationResult, String operationUser,
                       String operationIp, String targetId, String targetType) {
        this.operationType = operationType;
        this.operationModule = operationModule;
        this.operationMethod = operationMethod;
        this.operationParams = operationParams;
        this.operationResult = operationResult;
        this.operationUser = operationUser;
        this.operationIp = operationIp;
        this.targetId = targetId;
        this.targetType = targetType;
    }

    // Getters and Setters
    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationModule() {
        return operationModule;
    }

    public void setOperationModule(String operationModule) {
        this.operationModule = operationModule;
    }

    public String getOperationMethod() {
        return operationMethod;
    }

    public void setOperationMethod(String operationMethod) {
        this.operationMethod = operationMethod;
    }

    public String getOperationParams() {
        return operationParams;
    }

    public void setOperationParams(String operationParams) {
        this.operationParams = operationParams;
    }

    public String getOperationResult() {
        return operationResult;
    }

    public void setOperationResult(String operationResult) {
        this.operationResult = operationResult;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getOperationUser() {
        return operationUser;
    }

    public void setOperationUser(String operationUser) {
        this.operationUser = operationUser;
    }

    public String getOperationIp() {
        return operationIp;
    }

    public void setOperationIp(String operationIp) {
        this.operationIp = operationIp;
    }

    public LocalDateTime getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(LocalDateTime operationTime) {
        this.operationTime = operationTime;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getOldData() {
        return oldData;
    }

    public void setOldData(String oldData) {
        this.oldData = oldData;
    }

    public String getNewData() {
        return newData;
    }

    public void setNewData(String newData) {
        this.newData = newData;
    }

    /**
     * 获取格式化的操作时间
     */
    public String getFormattedOperationTime() {
        if (operationTime != null) {
            return operationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return "";
    }

    @Override
    public String toString() {
        return "OperationLog{" +
                "logId=" + logId +
                ", operationType='" + operationType + '\'' +
                ", operationModule='" + operationModule + '\'' +
                ", operationMethod='" + operationMethod + '\'' +
                ", operationResult='" + operationResult + '\'' +
                ", operationUser='" + operationUser + '\'' +
                ", operationTime=" + operationTime +
                ", targetId='" + targetId + '\'' +
                ", targetType='" + targetType + '\'' +
                '}';
    }
} 