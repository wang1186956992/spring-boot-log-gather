package com.bidanet.log;

import com.bidanet.log.base.LogHandler;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by xuejike on 2017/8/3.
 */

public class LogFactory  {


    protected ExecutorService threadPool;
    protected Logger logger= LoggerFactory.getLogger(LogFactory.class);


    public LogFactory(int poolNum) {
        threadPool = Executors.newFixedThreadPool(poolNum);
    }

    public LogFactory() {
        this(3);
    }

    /**
     * 日志处理类
     * @param pjp
     * @param handlers
     * @return
     * @throws Throwable
     */

    public Object execPoint(ProceedingJoinPoint pjp, Class<? extends LogHandler>... handlers) throws Throwable {
        Object[] args = pjp.getArgs();//参数
        List<LogHandler> logHandlers=new ArrayList<>(handlers.length);
        //1.创建处理类
        for (Class<? extends LogHandler> aClass : handlers) {
            LogHandler logHandler = BeanUtils.instantiate(aClass);
            logHandler.beforeSync(pjp);
            logHandlers.add(logHandler);
        }

        //2.异步提交处理方法



        Future<?> submit = threadPool.submit(() -> {

            try {
                String classType = pjp.getTarget().getClass().getName();
//                 clazz = null;
                Class<?> clazz = Class.forName(classType);
                String clazzName = clazz.getName();
                String methodName = pjp.getSignature().getName(); //获取方法名称
                String[] fieldsName = getFieldsName(clazz, clazzName, methodName);
                logHandlers.forEach(h -> h.beforeAsync(clazz, methodName, fieldsName, args));
            } catch (Exception e) {
                e.printStackTrace();
            }


        });


        //3.执行内容
        Object proceed=null;
        try {
            proceed = pjp.proceed(args);


            threadPool.submit(()-> {
                try {
                    submit.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                logHandlers.forEach(h->{
                    h.endAsync(true,null);
                });
            });
            return proceed;
        }catch (Throwable ex){
//            logFactory.end(s,pjp,false,ex);
            threadPool.submit(()-> logHandlers.forEach(h->{
                try {
                    submit.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                h.endAsync(false,ex);
            }));
            throw ex;
        }finally {
            //同步执行结束操作
            for (LogHandler handler : logHandlers) {
                handler.endSync(pjp,proceed);
            }
        }
    }


    /**
     * 得到方法参数的名称
     * @param cls
     * @param clazzName
     * @param methodName
     * @return
     * @throws NotFoundException
     */
    private static String[] getFieldsName(Class cls, String clazzName, String methodName) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        //ClassClassPath classPath = new ClassClassPath(this.getClass());
        ClassClassPath classPath = new ClassClassPath(cls);
        pool.insertClassPath(classPath);

        CtClass cc = pool.get(clazzName);
        CtMethod cm = cc.getDeclaredMethod(methodName);
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (attr == null) {
            // exception
        }
        String[] paramNames = new String[cm.getParameterTypes().length];
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        for (int i = 0; i < paramNames.length; i++){
            paramNames[i] = attr.variableName(i + pos); //paramNames即参数名
        }
        return paramNames;
    }

}
