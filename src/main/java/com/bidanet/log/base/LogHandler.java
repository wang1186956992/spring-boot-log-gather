package com.bidanet.log.base;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Created by xuejike on 2017/8/3.
 */
public interface LogHandler<T> {
    /**
     * 执行在方法调用线程,主要用于需要在该线程执行的操作
     *
     */
    default void beforeSync(ProceedingJoinPoint point){}

    /**
     * 方法调用结束后 在其他线程异步执行
     */
    void beforeAsync(Class cls, String method, String[] argsName, Object[] args);

    void endAsync(boolean success, Throwable ex);

}
