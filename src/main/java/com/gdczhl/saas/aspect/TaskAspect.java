package com.gdczhl.saas.aspect;

//import com.gdczhl.saas.mq.SyncProducer;

import com.alibaba.fastjson.JSONObject;
import com.gdczhl.saas.mq.SyncProducer;
import com.gdczhl.saas.service.ISignInTaskService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

import javax.servlet.http.HttpServletResponseWrapper;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * 系统任务切面
 */
//@Aspect
//@Component
//@Slf4j
//public class TaskAspect {
//
//    @Autowired
//    private ISignInTaskService signInTaskService;
//
//    @Autowired
//    private SyncProducer syncProducer;
//
//    /**
//     * 这里我们使用注解的形式
//     * 当然，我们也可以通过切点表达式直接指定需要拦截的package,需要拦截的class 以及 method
//     * 切点表达式:   execution(...)
//     * <p>
//     * execution(public * *(..)) 任意的公共方法
//     * execution（* set*（..）） 以set开头的所有的方法
//     * execution（* com.LoggerApply.*（..））com.LoggerApply这个类里的所有的方法
//     * execution（* com.annotation.*.*（..））com.annotation包下的所有的类的所有的方法
//     * execution（* com.annotation..*.*（..））com.annotation包及子包下所有的类的所有的方法
//     * execution(* com.annotation..*.*(String,?,Long)) com.annotation包及子包下所有的类的有三个参数，第一个参数为String类型，第二个参数为任意类型，第三个参数为Long类型的方法
//     * execution(@annotation(com.lingyejun.annotation.Lingyejun))
//     */
//    @Pointcut("@annotation(com.gdczhl.saas.aspect.annotation.Report)")
//    public void ReportPointCut() {
//    }
//
//    /**
//     * 环绕通知 @Around  ， 当然也可以使用 @Before (前置通知)  @After (后置通知)
//     *
//     * @param point
//     * @return
//     * @throws Throwable
//     */
//    @Around("ReportPointCut()")
//    public Object around(ProceedingJoinPoint point) throws Throwable {
//        //执行
//        Object result = point.proceed();
//        try {
////            下发人脸信息到设备
//            reportBindUserDevice(point);
//        } catch (Exception e) {
//        }
//        return result;
//    }
//
//
//    private void reportBindUserDevice(ProceedingJoinPoint joinPoint) {
//
////
//}