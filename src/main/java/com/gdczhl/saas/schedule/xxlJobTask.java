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
import com.gdczhl.saas.utils.JuheUtil;
import com.gdczhl.saas.utils.SignTasks;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

            if (signInTask.getTaskStartTime().isBefore(LocalTime.now())&&signInTask.getTaskEndTime().isAfter(LocalTime.now())){
                createTask(signInTask);
            }

            //结束任务在之前一分钟,统计
            if (signInTask.getTaskEndTime().plusMinutes(1).isAfter(LocalTime.now())){
                String statisticsKey = RedisConstant.STATISTICS_UUID_KEY+signInTask.getUuid();
                String statisticsJson = stringRedisTemplate.opsForValue().get(statisticsKey);
                if (statisticsJson==null){
                    //漏创建的
                    createTask(signInTask);
                    return;
                }
                String[] split = statisticsJson.split("&&");
                String statisticsUuid = split[0];
                stringRedisTemplate.opsForValue().set(
                        statisticsKey, statisticsUuid+"&&1", JuheUtil.getDistanceTomorrowSeconds(LocalDate.now()),
                        TimeUnit.SECONDS);
                //统计打卡信息
                if (split[1].equals("1")){
                    //已统计
                    return;
                }
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

    @XxlJob("autoEndTask")
    public void autoEndTask() {
        //获取所有启用,并合法的项目(未设置用户,未设置设备)
        //模拟小程序端获取数据
        LambdaQueryWrapper<SignInTask> signs = new LambdaQueryWrapper<>();
        signs.eq(SignInTask::getStatus,true);
        List<SignInTask> signInTasks = signInTaskService.list(signs);
        for (SignInTask signInTask : signInTasks) {
            if (signInTask.getTaskStartDate().isAfter(LocalDate.now())&&signInTask.getTaskEndDate().isBefore(LocalDate.now())){
                signInTask.setStatus(false);
                signInTaskService.updateById(signInTask);
            }
        }
    }


    public void createTask(SignInTask signInTask) {
        //获取所有启用,并合法的项目(未设置用户,未设置设备)
        //模拟小程序端获取数据
        thirdTaskService.getSignStatisticsUUid(signInTask,LocalDate.now());
    }
}
