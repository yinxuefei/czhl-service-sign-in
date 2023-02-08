package com.gdczhl.saas.schedule;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gdczhl.saas.entity.SignInRecord;
import com.gdczhl.saas.entity.SignInTask;
import com.gdczhl.saas.entity.SignStatistics;
import com.gdczhl.saas.entity.User;
import com.gdczhl.saas.enums.PollingModeEnum;
import com.gdczhl.saas.enums.SignStatusEnum;
import com.gdczhl.saas.enums.WeekEnum;
import com.gdczhl.saas.pojo.RedisConstant;
import com.gdczhl.saas.service.*;
import com.gdczhl.saas.utils.SignTasks;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;

@Component
@Slf4j
public class xxlJobTask {

    @Autowired
    private ISignInTaskService signInTaskService;

    @Autowired
    private ISignStatisticsService signStatisticsService;

    @Autowired
    private ISignInRecordService signInRecordService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private IThirdTaskService thirdTaskService;


    @Autowired
    private IUserService userService;

    @XxlJob("statisticsTask")
    public void statisticsTask() {
        List<SignInTask> signInTasks = thirdTaskService.todayTasks(LocalDate.now(), null);
        //时间过期 前一分钟统计
        for (SignInTask signInTask : signInTasks) {
            //结束任务在之前一分钟,统计
            if (signInTask.getTaskEndTime().minusMinutes(1).isAfter(LocalTime.now())){
                String statisticsKey = RedisConstant.STATISTICS_UUID_KEY+signInTask.getUuid();
                String statisticsUuid = stringRedisTemplate.opsForValue().get(statisticsKey);
                //统计打卡信息
                List<SignInRecord> signInRecordList = signInRecordService.getListByStatisticsUuid(statisticsUuid);
                HashSet<String> alreadyUser = new HashSet<>();
                HashSet<String> notUser = new HashSet<>();
                for (SignInRecord record : signInRecordList) {
                    if (record.getStatus().equals(SignStatusEnum.NOT_SING)){
                        notUser.add(record.getUserUuid());
                    }
                    if (record.getStatus().equals(SignStatusEnum.RESIGN)){
                        alreadyUser.add(record.getUserUuid());
                    }
                    record.setIsEnable(true);
                    signInRecordService.updateByUuid(record);
                }
                SignStatistics statistics = signStatisticsService.getStatisticsByUuid(statisticsUuid);
                statistics.setAlreadyUser(JSONObject.toJSONString(alreadyUser));
                statistics.setNotUser(JSONObject.toJSONString(notUser));
                statistics.setIsEnable(true);
                signStatisticsService.updateByUuid(statistics);
            }
        }
    }

    @XxlJob("createTask")
    public void createTask() {
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
