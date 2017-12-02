package com.insight.usercenter.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;


/**
 * @author duxl
 * @date 2017/9/27
 * @remark controller切面：访问日志/异常处理
 */
//@Aspect
//@Component
//@Order(1)
public class AspectMonitor {

    @Value("${com.apin.logging.topic:apinlog}")
    private String logtopic;

    @Pointcut("execution(* com.insight..*Controller.*(..))")
    public void point() {
    }

    /**
     * 前置通知， 参数校验
     * 访问日志
     *
     * @param joinPoint
     */
    @Before(value = "point()", argNames = "joinPoint")
    public void doBefore(JoinPoint joinPoint) throws Exception {
        Object[] objs = joinPoint.getArgs();

    }

    /**
     * 后置返回通知
     *
     * @param joinPoint
     * @param objs
     */
    @AfterReturning(value = "point()", returning = "objs")
    public void doAfterReturn(JoinPoint joinPoint, Object objs) {

    }


    /**
     * 后置异常通知
     *
     * @param joinPoint
     * @param throwable
     */
    @AfterThrowing(value = "point()", throwing = "throwable")
    public void doAfterThrow(JoinPoint joinPoint, Throwable throwable) {
        //
    }
}
