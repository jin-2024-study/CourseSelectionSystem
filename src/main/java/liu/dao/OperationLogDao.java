package liu.dao;

import liu.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志数据访问接口
 */
@Mapper
public interface OperationLogDao {
    
    /**
     * 插入操作日志
     */
    int insert(OperationLog operationLog);
    
    /**
     * 根据ID查询操作日志
     */
    OperationLog findById(Integer logId);
    
    /**
     * 查询所有操作日志
     */
    List<OperationLog> findAll();
    
    /**
     * 分页查询操作日志
     */
    List<OperationLog> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询操作日志总数
     */
    int count();
    
    /**
     * 根据条件查询操作日志
     */
    List<OperationLog> findByCondition(
        @Param("operationType") String operationType,
        @Param("operationModule") String operationModule,
        @Param("operationUser") String operationUser,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("offset") int offset,
        @Param("limit") int limit
    );
    
    /**
     * 根据条件查询操作日志数量
     */
    int countByCondition(
        @Param("operationType") String operationType,
        @Param("operationModule") String operationModule,
        @Param("operationUser") String operationUser,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 删除指定时间之前的日志
     */
    int deleteBeforeTime(LocalDateTime time);
    
    /**
     * 根据目标ID和类型查询操作日志
     */
    List<OperationLog> findByTarget(@Param("targetId") String targetId, 
                                   @Param("targetType") String targetType);
} 