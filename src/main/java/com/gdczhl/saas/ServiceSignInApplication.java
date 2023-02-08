package com.gdczhl.saas;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = {"com.gdczhl.saas.mapper"})
@EnableFeignClients
@Slf4j
@EnableTransactionManagement
public class ServiceSignInApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(ServiceSignInApplication.class, args);
        log.info("系统:创智-微服务-巡查签到");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        log.info("启动时间:{}", df.format(LocalDateTime.now()));

        //查询bean的名称
        String beanName = "";
        Object beanMap = getBeanMap(configurableApplicationContext, beanName);
        if (Objects.nonNull(beanMap)) {
            System.out.println(JSONObject.toJSONString(beanMap));
        }
    }

    private static Object getBeanMap(ConfigurableApplicationContext applicationContext, String name) {
        if (name == null) {
            return null;
        }
        if (applicationContext.containsBean(name)) {
            return applicationContext.getBean(name);
        }
        return null;
    }
}
