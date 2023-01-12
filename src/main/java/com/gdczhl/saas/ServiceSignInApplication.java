package com.gdczhl.saas;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = {"com.gdczhl.saas.mapper"})
@EnableFeignClients
@Slf4j
@EnableTransactionManagement
public class ServiceSignInApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceSignInApplication.class, args);
        log.info("系统:创智-微服务-签到服务");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        log.info("启动时间:{}", df.format(LocalDateTime.now()));

    }
    }
