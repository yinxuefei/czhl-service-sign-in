package com.gdczhl.saas.aspect.annotation;

import java.lang.annotation.*;

/**
 * 注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Report {
    String value() default "";
}