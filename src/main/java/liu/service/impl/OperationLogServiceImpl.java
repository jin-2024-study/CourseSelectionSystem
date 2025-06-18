package liu.service.impl;

import liu.dao.OperationLogDao;
import liu.entity.OperationLog;
import liu.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogDao operationLogDao;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String CACHE_PAGE_PREFIX = "operation_log_page:";
    private static final String CACHE_CONDITION_PREFIX = "operation_log_condition:";
    private static final String CACHE_STATISTICS_KEY = "operation_log_statistics";

    @Override
    @CacheEvict(value = {"operationLogPage", "operationLogCondition", "operationLogStatistics"}, allEntries = true)
    public void save(OperationLog operationLog) {
        if (operationLog.getOperationTime() == null) {
            operationLog.setOperationTime(LocalDateTime.now());
        }
        operationLogDao.insert(operationLog);
        clearRelatedCache();
    }

    @Override
    @Cacheable(value = "operationLog", key = "#logId", unless = "#result == null")
    public OperationLog findById(Integer logId) {
        return operationLogDao.findById(logId);
    }

    @Override
    public List<OperationLog> findAll() {
        return operationLogDao.findAll();
    }

    @Override
    public List<OperationLog> findByPage(int page, int size) {
        String cacheKey = CACHE_PAGE_PREFIX + page + ":" + size;
        
        @SuppressWarnings("unchecked")
        List<OperationLog> cachedResult = (List<OperationLog>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        int offset = (page - 1) * size;
        List<OperationLog> result = operationLogDao.findByPage(offset, size);
        
        redisTemplate.opsForValue().set(cacheKey, result, 10, TimeUnit.MINUTES);
        
        return result;
    }

    @Override
    @Cacheable(value = "operationLogCount", unless = "#result <= 0")
    public int count() {
        return operationLogDao.count();
    }

    @Override
    public List<OperationLog> findByCondition(String operationType, String operationModule,
                                             String operationUser, LocalDateTime startTime,
                                             LocalDateTime endTime, int page, int size) {
        String cacheKey = generateConditionCacheKey(operationType, operationModule, operationUser, 
                                                    startTime, endTime, page, size);
        
        @SuppressWarnings("unchecked")
        List<OperationLog> cachedResult = (List<OperationLog>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        int offset = (page - 1) * size;
        List<OperationLog> result = operationLogDao.findByCondition(operationType, operationModule, 
                                                                   operationUser, startTime, endTime, offset, size);
        
        redisTemplate.opsForValue().set(cacheKey, result, 15, TimeUnit.MINUTES);
        
        return result;
    }

    @Override
    public int countByCondition(String operationType, String operationModule,
                               String operationUser, LocalDateTime startTime,
                               LocalDateTime endTime) {
        String cacheKey = generateConditionCountCacheKey(operationType, operationModule, operationUser, 
                                                         startTime, endTime);
        
        Integer cachedResult = (Integer) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        int result = operationLogDao.countByCondition(operationType, operationModule, operationUser,
                                                     startTime, endTime);
        
        redisTemplate.opsForValue().set(cacheKey, result, 15, TimeUnit.MINUTES);
        
        return result;
    }

    @Override
    @CacheEvict(value = {"operationLogPage", "operationLogCondition", "operationLogStatistics", "operationLogCount"}, allEntries = true)
    public int deleteBeforeTime(LocalDateTime time) {
        int deletedCount = operationLogDao.deleteBeforeTime(time);
        clearRelatedCache();
        return deletedCount;
    }

    @Override
    @Cacheable(value = "operationLogTarget", key = "#targetId + ':' + #targetType", unless = "#result == null or #result.isEmpty()")
    public List<OperationLog> findByTarget(String targetId, String targetType) {
        return operationLogDao.findByTarget(targetId, targetType);
    }

    @Override
    @CacheEvict(value = {"operationLogPage", "operationLogCondition", "operationLogStatistics", "operationLogCount"}, allEntries = true)
    public int cleanExpiredLogs() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        int deletedCount = deleteBeforeTime(thirtyDaysAgo);
        clearRelatedCache();
        return deletedCount;
    }

    @Override
    public Map<String, Object> getOperationStatistics() {
        @SuppressWarnings("unchecked")
        Map<String, Object> cachedResult = (Map<String, Object>) redisTemplate.opsForValue().get(CACHE_STATISTICS_KEY);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        Map<String, Object> stats = new java.util.HashMap<>();
        
        stats.put("totalLogs", count());
        
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime todayEnd = todayStart.plusDays(1);
        int todayLogs = countByCondition(null, null, null, todayStart, todayEnd);
        stats.put("todayLogs", todayLogs);
        
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
        int weekLogs = countByCondition(null, null, null, weekStart, null);
        stats.put("weekLogs", weekLogs);
        
        redisTemplate.opsForValue().set(CACHE_STATISTICS_KEY, stats, 5, TimeUnit.MINUTES);
        
        return stats;
    }

    private String generateConditionCacheKey(String operationType, String operationModule,
                                            String operationUser, LocalDateTime startTime,
                                            LocalDateTime endTime, int page, int size) {
        StringBuilder sb = new StringBuilder(CACHE_CONDITION_PREFIX);
        sb.append(operationType != null ? operationType : "null").append(":");
        sb.append(operationModule != null ? operationModule : "null").append(":");
        sb.append(operationUser != null ? operationUser : "null").append(":");
        sb.append(startTime != null ? startTime.toString() : "null").append(":");
        sb.append(endTime != null ? endTime.toString() : "null").append(":");
        sb.append(page).append(":").append(size);
        return sb.toString();
    }

    private String generateConditionCountCacheKey(String operationType, String operationModule,
                                                 String operationUser, LocalDateTime startTime,
                                                 LocalDateTime endTime) {
        StringBuilder sb = new StringBuilder(CACHE_CONDITION_PREFIX + "count:");
        sb.append(operationType != null ? operationType : "null").append(":");
        sb.append(operationModule != null ? operationModule : "null").append(":");
        sb.append(operationUser != null ? operationUser : "null").append(":");
        sb.append(startTime != null ? startTime.toString() : "null").append(":");
        sb.append(endTime != null ? endTime.toString() : "null");
        return sb.toString();
    }

    private void clearRelatedCache() {
        try {
            Set<String> pageKeys = redisTemplate.keys(CACHE_PAGE_PREFIX + "*");
            if (pageKeys != null && !pageKeys.isEmpty()) {
                redisTemplate.delete(pageKeys);
            }
            
            Set<String> conditionKeys = redisTemplate.keys(CACHE_CONDITION_PREFIX + "*");
            if (conditionKeys != null && !conditionKeys.isEmpty()) {
                redisTemplate.delete(conditionKeys);
            }
            
            redisTemplate.delete(CACHE_STATISTICS_KEY);
        } catch (Exception e) {
            System.err.println("Failed to clear cache: " + e.getMessage());
        }
    }
} 