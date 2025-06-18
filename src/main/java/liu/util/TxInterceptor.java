package liu.util;

import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.Method;

/**
 * Spring AOP事务拦截器
 */
@Component
public class TxInterceptor implements MethodBeforeAdvice, AfterReturningAdvice, ThrowsAdvice {
    
    @Autowired
    private DataSourceTransactionManager transactionManager;
    
    private ThreadLocal<TransactionStatus> transactionStatusThreadLocal = new ThreadLocal<>();
    
    /**
     * 在方法执行前开启事务
     */
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        transactionStatusThreadLocal.set(status);
        System.out.println("开启事务: " + method.getName());
    }
    
    /**
     * 方法执行成功后提交事务
     */
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        TransactionStatus status = transactionStatusThreadLocal.get();
        if (status != null) {
            transactionManager.commit(status);
            transactionStatusThreadLocal.remove();
            System.out.println("提交事务: " + method.getName());
        }
    }
    
    /**
     * 方法执行抛出异常后回滚事务
     */
    public void afterThrowing(Method method, Object[] args, Object target, Exception ex) {
        TransactionStatus status = transactionStatusThreadLocal.get();
        if (status != null) {
            transactionManager.rollback(status);
            transactionStatusThreadLocal.remove();
            System.out.println("回滚事务: " + method.getName() + ", 异常: " + ex.getMessage());
        }
    }
} 