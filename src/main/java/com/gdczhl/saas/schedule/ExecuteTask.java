package com.gdczhl.saas.schedule;

import com.gdczhl.saas.entity.SignInTask;
import com.gdczhl.saas.service.IThirdTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@Slf4j
public class ExecuteTask {

    @Autowired
    private IThirdTaskService thirdTaskService;

    @Scheduled(cron = "0/59 * * * * ? ")
    public void main() {
        //获取所有启用,并合法的项目(未设置用户,未设置设备)
        //模拟小程序端获取数据
        LocalDate localDate = LocalDate.now();
        List<SignInTask> signInTasks = thirdTaskService.todayTasks(localDate, null);
        for (SignInTask signInTask : signInTasks) {
            if (signInTask.getTaskStartTime().isAfter(LocalTime.now())&&signInTask.getTaskStartTime().isBefore(LocalTime.now())){
            thirdTaskService.getSignStatisticsUUid(signInTask,localDate);
            }
        }

    }
}
