package com.gdczhl.saas.config;

import com.gdczhl.saas.interceptor.HeaderInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author hkx
 */
@Component
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HeaderInterceptor())
                .addPathPatterns("/**")
                //swagger
                .excludePathPatterns("/favicon.ico", "/doc.html", "/webjars/**", "/swagger-resources","/v2/api-docs")
                .excludePathPatterns("/feign/**");
    }
}
