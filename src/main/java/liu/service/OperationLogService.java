package liu.service;

import liu.entity.OperationLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 操作日志服务接口
 * 
 * @author Liu
 * @version 1.0
 * @since 2024
 */
public interface OperationLogService {

    /**
     * 保存操作日志
     * 
     * @param operationLog 操作日志对象
     */
    void save(OperationLog operationLog);

    /**
     * 根据ID查询操作日志
     * 
     * @param logId 日志ID
     * @return 操作日志对象
     */
    OperationLog findById(Integer logId);

    /**
     * 查询所有操作日志
     * 
     * @return 操作日志列表
     */
    List<OperationLog> findAll();

    /**
     * 分页查询操作日志
     * 
     * @param page 页码
     * @param size 每页大小
     * @return 操作日志列表
     */
    List<OperationLog> findByPage(int page, int size);

    /**
     * 查询操作日志总数
     * 
     * @return 日志总数
     */
    int count();

    /**
     * 根据条件查询操作日志
     * 
     * @param operationType 操作类型
     * @param operationModule 操作模块
     * @param operationUser 操作用户
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 页码
     * @param size 每页大小
     * @return 操作日志列表
     */
    List<OperationLog> findByCondition(String operationType, String operationModule,
                                      String operationUser, LocalDateTime startTime,
                                      LocalDateTime endTime, int page, int size);

    /**
     * 根据条件查询操作日志数量
     * 
     * @param operationType 操作类型
     * @param operationModule 操作模块
     * @param operationUser 操作用户
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志数量
     */
    int countByCondition(String operationType, String operationModule,
                        String operationUser, LocalDateTime startTime,
                        LocalDateTime endTime);

    /**
     * 删除指定时间之前的日志
     * 
     * @param time 指定时间
     * @return 删除的记录数
     */
    int deleteBeforeTime(LocalDateTime time);

    /**
     * 根据目标ID和类型查询操作日志
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 操作日志列表
     */
    List<OperationLog> findByTarget(String targetId, String targetType);

    /**
     * 清理过期日志（保留最近30天的日志）
     * 
     * @return 清理的记录数
     */
    int cleanExpiredLogs();

    /**
     * 获取操作统计信息
     * 
     * @return 统计信息Map
     */
    Map<String, Object> getOperationStatistics();
} 