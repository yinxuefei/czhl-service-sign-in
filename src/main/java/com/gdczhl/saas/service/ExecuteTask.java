package com.gdczhl.saas.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExecuteTask {

    @Scheduled(cron = "0/59 * * * * ? ")
    public void main() {
        //获取所有启用,并合法的项目(未设置用户,未设置设备)
        //模拟小程序端获取数据


    }
}
