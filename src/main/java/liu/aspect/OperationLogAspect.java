package liu.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import liu.entity.OperationLog;
import liu.entity.Student;
import liu.event.LogEvent;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 操作日志AOP切面
 */
@Aspect
@Component
public class OperationLogAspect {

    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private final ThreadLocal<String> oldDataThreadLocal = new ThreadLocal<>();
    private static final Logger logger = LoggerFactory.getLogger(OperationLogAspect.class);

    public OperationLogAspect(ApplicationEventPublisher eventPublisher, ObjectMapper objectMapper) {
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    /**
     * 定义切点 - 拦截学生管理相关的操作
     */
    @Pointcut("execution(* liu.controller.StudentController.add(..)) || " +
              "execution(* liu.controller.StudentController.update(..)) || " +
              "execution(* liu.controller.StudentController.delete(..))")
    public void studentOperationPointcut() {}

    /**
     * 定义切点 - 拦截选课管理相关的操作
     */
    @Pointcut("execution(* liu.controller.CourseSelectionController.addCourseSelection(..)) || " +
              "execution(* liu.controller.CourseSelectionController.editCourseSelection(..)) || " +
              "execution(* liu.controller.CourseSelectionController.deleteCourseSelection(..)) || " +
              "execution(* liu.controller.CourseSelectionController.editCourse(..)) || " +
              "execution(* liu.controller.CourseSelectionController.deleteCourse(..))")
    public void courseSelectionOperationPointcut() {}

    /**
     * 组合切点 - 拦截所有相关操作
     */
    @Pointcut("studentOperationPointcut() || courseSelectionOperationPointcut()")
    public void operationPointcut() {}

    /**
     * 前置通知 - 记录操作前的数据
     */
    @Before("operationPointcut()")
    public void beforeOperation(JoinPoint joinPoint) {
        try {
            String methodName = joinPoint.getSignature().getName();
            Object[] args = joinPoint.getArgs();
            
            // 对于编辑和删除操作，需要记录操作前的数据
            if (methodName.contains("edit") || methodName.contains("delete") || 
                methodName.equals("editCourseSelection") || methodName.equals("deleteCourseSelection") ||
                methodName.equals("editCourse") || methodName.equals("deleteCourse")) {
                // 从参数中获取ID信息
                for (Object arg : args) {
                    if (arg instanceof Integer id) {
                        // 根据方法名确定ID类型
                        if (methodName.contains("Course") || methodName.contains("Selection")) {
                            oldDataThreadLocal.set("courseSelectionId:" + id);
                        } else {
                            oldDataThreadLocal.set("studentId:" + id);
                        }
                        break;
                    } else if (arg instanceof Student student) {
                        if (student.getStudent_id() > 0) {
                            oldDataThreadLocal.set("studentId:" + student.getStudent_id());
                            break;
                        }
                    } else if (arg.getClass().getSimpleName().contains("CourseSelection")) {
                        // 处理选课相关的对象
                        try {
                            Object idField = arg.getClass().getMethod("getSelection_id").invoke(arg);
                            if (idField != null) {
                                oldDataThreadLocal.set("courseSelectionId:" + idField);
                                break;
                            }
                        } catch (Exception e) {
                            // 忽略反射异常
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 不影响主业务流程
            e.printStackTrace();
        }
    }

    /**
     * 后置返回通知 - 操作成功时记录日志
     */
    @AfterReturning(pointcut = "operationPointcut()", returning = "result")
    public void afterReturningOperation(JoinPoint joinPoint, Object result) {
        try {
            recordOperation(joinPoint, "SUCCESS", null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            oldDataThreadLocal.remove();
        }
    }

    /**
     * 异常通知 - 操作失败时记录日志
     */
    @AfterThrowing(pointcut = "operationPointcut()", throwing = "exception")
    public void afterThrowingOperation(JoinPoint joinPoint, Exception exception) {
        try {
            recordOperation(joinPoint, "FAILURE", exception.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            oldDataThreadLocal.remove();
        }
    }

    /**
     * 记录操作日志
     */
    private void recordOperation(JoinPoint joinPoint, String result, String errorMessage) {
        try {
            // 获取方法信息
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            Object[] args = joinPoint.getArgs();

            // 确定操作类型和模块
            String operationType;
            String operationModule;
            
            // 根据方法名确定操作类型
            if (methodName.contains("add") || methodName.equals("addCourseSelection")) {
                operationType = "INSERT";
            } else if (methodName.contains("update") || methodName.equals("editCourseSelection") || methodName.equals("editCourse")) {
                operationType = "UPDATE";
            } else if (methodName.contains("delete") || methodName.equals("deleteCourseSelection") || methodName.equals("deleteCourse")) {
                operationType = "DELETE";
            } else {
                operationType = "OTHER";
            }
            
            // 根据类名确定操作模块
            if (className.contains("Student")) {
                operationModule = "学生信息管理";
            } else if (className.contains("CourseSelection")) {
                operationModule = "学生选课管理";
            } else {
                operationModule = "其他模块";
            }

            // 获取用户信息
            String operationUser = getCurrentUser();

            // 获取IP地址
            String operationIp = getClientIpAddress();

            // 构建操作参数
            String operationParams = Arrays.toString(args);

            // 获取目标对象信息
            String targetId = null;
            String targetType = operationModule.contains("学生信息") ? "Student" : 
                              operationModule.contains("选课") ? "CourseSelection" : "Other";
            String newData = null;

            // 从参数中提取目标信息
            for (Object arg : args) {
                if (arg instanceof Integer) {
                    targetId = arg.toString();
                    break;
                } else if (arg instanceof Student student) {
                    targetId = student.getStudent_id() > 0 ? String.valueOf(student.getStudent_id()) : null;
                    try {
                        newData = objectMapper.writeValueAsString(student);
                    } catch (Exception e) {
                        newData = "Failed to serialize student data";
                    }
                    break;
                } else if (arg.getClass().getSimpleName().contains("CourseSelection")) {
                    // 处理选课相关的对象
                    try {
                        newData = objectMapper.writeValueAsString(arg);
                        // 尝试获取ID（通过反射获取可能的ID字段）
                        try {
                            Object idField = arg.getClass().getMethod("getSelection_id").invoke(arg);
                            if (idField != null) {
                                targetId = idField.toString();
                            }
                        } catch (Exception e) {
                            // 忽略反射异常
                        }
                    } catch (Exception e) {
                        newData = "Failed to serialize course selection data";
                    }
                    break;
                }
            }

            // 获取操作前数据
            String oldData = oldDataThreadLocal.get();

            // 创建操作日志对象
            OperationLog operationLog = new OperationLog();
            operationLog.setOperationType(operationType);
            operationLog.setOperationModule(operationModule);
            operationLog.setOperationMethod(className + "." + methodName);
            operationLog.setOperationParams(operationParams);
            operationLog.setOperationResult(result);
            operationLog.setErrorMessage(errorMessage);
            operationLog.setOperationUser(operationUser);
            operationLog.setOperationIp(operationIp);
            operationLog.setOperationTime(LocalDateTime.now());
            operationLog.setTargetId(targetId);
            operationLog.setTargetType(targetType);
            operationLog.setOldData(oldData);
            operationLog.setNewData(newData);

            // 发布日志事件
            LogEvent logEvent = new LogEvent(this, operationLog);
            eventPublisher.publishEvent(logEvent);

        } catch (Exception e) {
            logger.error("记录操作日志失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取当前登录用户
     */
    private String getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                return authentication.getName();
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return "unknown";
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("Proxy-Client-IP");
                }
                if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("WL-Proxy-Client-IP");
                }
                if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return "unknown";
    }
} 