package com.gdczhl.saas.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gdczhl.saas.entity.*;
import com.gdczhl.saas.enums.PollingModeEnum;
import com.gdczhl.saas.pojo.RedisConstant;
import com.gdczhl.saas.enums.SignStatusEnum;
import com.gdczhl.saas.utils.ContextCache;
import com.gdczhl.saas.netty.remote.INettyServiceRemote;
import com.gdczhl.saas.pojo.vo.SignInInfoVo;
import com.gdczhl.saas.pojo.vo.signInTask.DeviceSignVo;
import com.gdczhl.saas.service.*;
import com.gdczhl.saas.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hkx
 * @since 2023-01-06
 */
@Service
@Slf4j
@Transactional
public class ThirdTaskServiceImpl implements IThirdTaskService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ISignInTaskService signInTaskService;

    @Autowired
    private ISignInRecordService signInRecordService;

    @Autowired
    private ISignStatisticsService signStatisticsService;

    @Autowired
    private INettyServiceRemote nettyServiceRemote;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IUserService userService;

    @Override
    public void deviceSignIn(DeviceSignVo deviceSignVo) {
        //1.记录打卡流水(ps:根据打卡时间,拿到所有区间内的任务,更改人员打卡状态)
        String deviceUuid = deviceSignVo.getDeviceUuid();
        String userUuid = deviceSignVo.getUserUuid();
        LocalDateTime localDateTime = deviceSignVo.getTime();

        //打卡日期
        LocalDate date = localDateTime.toLocalDate();
        //打卡时间
        LocalTime time = localDateTime.toLocalTime();

        //1.查询日期所有任务
        List<SignInTask> signList = todayTasks(date,deviceUuid);

        //判断需要对那些任务打卡
        List<SignInTask> signInTasks = signList.stream().filter(signInTask -> {
            List<String> devices = JSONObject.parseArray(signInTask.getDeviceUuids(), String.class);
            if (time.isBefore(signInTask.getTaskEndTime()) && time.isAfter(signInTask.getTaskStartTime()) && devices.contains(deviceUuid)) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());

        //打卡流水
        for (SignInTask signInTask : signInTasks) {
            List<String> userUuids = JSONObject.parseArray(signInTask.getUserUuids(), String.class);
            if (userUuids.contains(userUuid)) {
                //打卡
                if (signIn(deviceSignVo, signInTask)) {
                    //3.打卡成功,任务是否需要推送
                    if (signInTask.getPush()) {
                        //ToDO::差推送接口和模板
                        // 推送给自己 拥有userUuid
                    }

//                    if (deviceSignVo.getBodyTemperature()>=37.3){
                    //ToDO::差推送接口和模板
                    // 推送给体温异常推送人 拥有userUuid
//                    }
                }
            }
        }
    }

    @Override
    public SignInInfoVo signInInfo(String uuid, LocalDateTime time, String deviceUuid) {

        SignInInfoVo signInInfoVo = new SignInInfoVo();

        LambdaQueryWrapper<SignInRecord> between = new LambdaQueryWrapper<SignInRecord>().eq(SignInRecord::getInstitutionUuid, uuid)
                .between(SignInRecord::getCreateTime, LocalDateTime.of(LocalDate.now(), LocalTime.MIN),
                        LocalDateTime.of(LocalDate.now(), LocalTime.MAX))
                .orderByDesc(SignInRecord::getCreateTime);
        List<SignInRecord> list = signInRecordService.list(between);

        List<String> usernames = list.stream().map(signInRecord -> {
            return signInRecord.getUsername();
        }).limit(20).collect(Collectors.toList());

        Map<String, SignInTask> map = signInTaskService.getTasksByUuids(list.stream().map(SignInRecord -> {
            return SignInRecord.getSignTaskUuid();
        }).limit(20).collect(Collectors.toList()));

        List<String> taskNames = new ArrayList<>();
        map.forEach((s, signInTask) -> {
            taskNames.add(SignTasks.getPeriodNameResult(signInTask));
        });

        signInInfoVo.setTaskNames(taskNames);
        signInInfoVo.setUsernames(usernames);
        return signInInfoVo;
    }

    @Override
    public List<SignInTask> todayTasks(LocalDate date,String deviceUuid) {
        //1.查询当日所有任务
        return signInTaskService.getTodayTasks(date,deviceUuid);
    }


    private Boolean signIn(DeviceSignVo deviceSignVo, SignInTask signInTask) {
        //初始化表
        String signStatisticsUUid = getSignStatisticsUUid(signInTask, LocalDate.now());
        String deviceUuid = deviceSignVo.getDeviceUuid();
        String userUuid = deviceSignVo.getUserUuid();

        //保存任务uuid 已打卡的人员uuid
        String key = RedisConstant.USER_UUID_KEY + signInTask.getUuid();
        //判断是否是重复打卡
        Set<String> userUuids = stringRedisTemplate.opsForSet().members(key);
        if (userUuids.contains(userUuid)) {
            log.info("此次打卡为重复打卡,不做记录");
            return false;
        }

        SignInRecord record = getRecordUuid(signInTask.getUuid(),userUuid);
        if (record==null){
            log.info("当前用户无打卡任务");
            return true;
        }
        Device device = deviceService.getByDeviceUuid(deviceUuid);
        record.setDeviceUuid(deviceUuid);
        record.setAreaUuid(device.getAreaUuid());
        record.setAreaAddress(device.getAreaAddress());
        record.setNumberSn(device.getNumberSn());

        if (deviceSignVo.getSignImageUrl()!=null){
            record.setSignImageUrl(deviceSignVo.getSignImageUrl());
        }
        record.setTaskName(signInTask.getTaskName());
        record.setTaskStartTime(signInTask.getTaskStartTime());
        record.setTaskEndTime(signInTask.getTaskEndTime());
        record.setSignTaskUuid(signInTask.getUuid());
        record.setBodyTemperature(deviceSignVo.getBodyTemperature());
        record.setBodyTemperature(deviceSignVo.getBodyTemperature());
        record.setCreateTime(deviceSignVo.getTime());
        record.setStatus(SignStatusEnum.SINGED);
        record.setPush(false);
        record.setUuid(record.getUuid());
        record.setInstitutionUuid(signInTask.getInstitutionUuid());

        stringRedisTemplate.opsForSet().add(key, userUuid);
        stringRedisTemplate.expire(key,JuheUtil.getDistanceTomorrowSeconds(LocalDateTime.of(LocalDate.now(),
                        signInTask.getTaskEndTime())),
                TimeUnit.SECONDS);
        return signInRecordService.update(record,new LambdaQueryWrapper<SignInRecord>().eq(SignInRecord::getUuid,
                record.getUuid()));

    }

    private SignInRecord getRecordUuid(String uuid, String userUuid) {
        String recordUuid = stringRedisTemplate.opsForValue().get(RedisConstant.RECORD_KEY + uuid + userUuid);
        return signInRecordService.getByUuid(recordUuid);
    }

    public String getSignStatisticsUUid(SignInTask signInTask, LocalDate now) {
        //key taskUuid  value StatisticsUuid
        String key = RedisConstant.STATISTICS_UUID_KEY + signInTask.getUuid();
        String statisticsUuid = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(statisticsUuid)) {
            //统计初始化
            SignStatistics signStatistics = SignStatisticsInIt(signInTask, now, key);
            return signStatistics.getUuid();
        }
        return statisticsUuid;
    }

    @NotNull
    private SignStatistics SignStatisticsInIt(SignInTask signInTask, LocalDate now, String key) {
        SignStatistics signStatistics = new SignStatistics();
        BeanUtils.copyProperties(signInTask, signStatistics,"uuid","id","version","createTime","updateTime");
        signStatistics.setTaskUuid(signInTask.getUuid());
        signStatistics.setCreateDate(now);
        signStatistics.setIsEnable(false);
        signStatistics.setInstitutionUuid(ContextCache.getInstitutionUuid());
        signStatistics.setAllUser(signInTask.getUserUuids());
        signStatisticsService.save(signStatistics);
        stringRedisTemplate.opsForValue().set(
                key, signStatistics.getUuid(),JuheUtil.getDistanceTomorrowSeconds(LocalDateTime.of(now,
                        signInTask.getTaskEndTime())), TimeUnit.SECONDS);

        RecordInIt(signInTask,signStatistics);
        return signStatistics;
    }

    private void RecordInIt(SignInTask signInTask, SignStatistics signStatistics) {
        List<String> userUuids = JSONObject.parseArray(signInTask.getUserUuids(), String.class);
        for (String userUuid : userUuids) {
            String key = RedisConstant.RECORD_KEY+signInTask.getUuid()+userUuid;
            SignInRecord record = new SignInRecord();
            record.setSignTaskUuid(signInTask.getUuid());
            record.setSignStatisticsUuid(signStatistics.getUuid());
            User user = userService.getByUserUuid(userUuid);
            record.setSignImageUrl(user.getHeadUrl());
            record.setUsername(user.getName());
            record.setUserUuid(user.getUuid());
            record.setStatus(SignStatusEnum.NOT_SING);
            record.setIsEnable(false);
            signInRecordService.save(record);
            stringRedisTemplate.opsForValue().set(key,record.getUuid(),JuheUtil.getDistanceTomorrowSeconds(LocalDateTime.of(LocalDate.now(),
                    signInTask.getTaskEndTime())), TimeUnit.SECONDS);
        }

    }

}
