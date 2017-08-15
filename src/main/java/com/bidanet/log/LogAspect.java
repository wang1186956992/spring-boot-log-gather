package com.bidanet.log;


import com.bidanet.log.base.BdLog;
import com.bidanet.log.base.LogHandler;
import javassist.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by xuejike on 2017/8/3.
 */
@Aspect
public class LogAspect {
    @Autowired
    LogFactory logFactory;

    @Around("@annotation(bdLog)")
    public Object Interceptor(ProceedingJoinPoint pjp,BdLog bdLog) throws Throwable {
        Class<? extends LogHandler>[] value = bdLog.value();

        return logFactory.execPoint(pjp,value);


    }


}
