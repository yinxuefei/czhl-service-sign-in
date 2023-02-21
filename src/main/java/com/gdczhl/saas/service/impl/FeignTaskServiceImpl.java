package com.gdczhl.saas.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gdczhl.saas.controller.feign.vo.DeviceSignVo;
import com.gdczhl.saas.controller.feign.vo.SignInInfoVo;
import com.gdczhl.saas.entity.*;
import com.gdczhl.saas.constant.RedisConstant;
import com.gdczhl.saas.enums.SignStatusEnum;
import com.gdczhl.saas.service.remote.WechatRemoteService;
import com.gdczhl.saas.service.remote.vo.wechat.OfficialAccountSendVo;
import com.gdczhl.saas.service.remote.vo.wechat.OfficialAccountVo;
import com.gdczhl.saas.service.*;
import com.gdczhl.saas.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
public class FeignTaskServiceImpl implements FeignTaskService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ISignInTaskService signInTaskService;

    @Autowired
    private ISignInRecordService signInRecordService;

    @Autowired
    private ISignStatisticsService signStatisticsService;

    @Autowired
    private WechatRemoteService wechatRemoteService;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IUserService userService;

    @Value("${wechat.templateType}")
    private String templateType;


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
        List<SignInTask> signList = todayTasks(date, deviceUuid);

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
                signIn(deviceSignVo, signInTask);
            }
        }
    }


    @Override
    public SignInInfoVo signInInfo(String uuid, LocalDateTime time, String deviceUuid) {
        SignInInfoVo result = new SignInInfoVo(new ArrayList<>(), new ArrayList<>());
        if (StringUtils.isBlank(uuid)){
           log.error("机构不存在");
           return result;
        }
        SignInInfoVo signInInfoVo = new SignInInfoVo();
        //多个任务
        List<SignInTask> tasks = todayTasks(time.toLocalDate(), deviceUuid);
        List<SignInTask> signInTasks = tasks.stream().filter(signInTask -> {
            LocalTime localTime = time.toLocalTime();
            return localTime.isAfter(signInTask.getTaskStartTime()) && localTime.isBefore(signInTask.getTaskEndTime());
        }).collect(Collectors.toList());

        List<String> uuids= signInTasks.stream().map(BaseEntity::getUuid).collect(Collectors.toList());

        List<String> statisticUuids =
                signStatisticsService.getStatisticsByTaskUuid(uuids, time.toLocalDate()).stream().map(SignStatistics::getUuid).collect(Collectors.toList());

        LambdaQueryWrapper<SignInRecord> between = new LambdaQueryWrapper<SignInRecord>()
                .eq(org.springframework.util.StringUtils.hasText(uuid),SignInRecord::getInstitutionUuid, uuid)
                .in(!CollectionUtils.isEmpty(statisticUuids),SignInRecord::getSignStatisticsUuid,statisticUuids)
                .ne(SignInRecord::getStatus,SignStatusEnum.NOT_SING)
                .eq(!StringUtils.isEmpty(deviceUuid),SignInRecord::getDeviceUuid,deviceUuid)
                .between(BaseEntity::getCreateTime,LocalDateTime.of(time.toLocalDate(),LocalTime.MIN),
                        LocalDateTime.of(time.toLocalDate(),LocalTime.MAX))
                .orderByDesc(SignInRecord::getUpdateTime);

        if (CollectionUtils.isEmpty(uuids)){
            return result;
        }

        List<SignInRecord> list = signInRecordService.list(between);
        List<String> collect = list.stream().map(SignInRecord::getUsername).collect(Collectors.toList());
        Set<String> set = ListUtil.ListToSet(collect);
        List<String> usernames = set.stream().limit(20).collect(Collectors.toList());

        List<String> taskNames = new ArrayList<>();
        for (SignInTask signInTask : signInTasks) {
            taskNames.add(SignTasks.getPeriodNameResult(signInTask));
        }
        signInInfoVo.setTaskNames(taskNames);
        signInInfoVo.setUsernames(usernames);
        return signInInfoVo;
    }

    @Override
    public List<SignInTask> todayTasks(LocalDate date, String deviceUuid) {
        //1.查询当日所有任务
        return signInTaskService.getTodayTasks(date, deviceUuid);
    }


    private Boolean signIn(DeviceSignVo deviceSignVo, SignInTask signInTask) {
        //初始化表
        String signStatisticsUUid = getSignStatisticsUUid(signInTask, LocalDate.now());
        String deviceUuid = deviceSignVo.getDeviceUuid();
        String userUuid = deviceSignVo.getUserUuid();

        //保存任务uuid 已打卡的人员uuid
        String key = RedisConstant.USER_UUID_KEY + signStatisticsUUid;
        //判断是否是重复打卡
        Set<String> userUuids = stringRedisTemplate.opsForSet().members(key);
        if (userUuids.contains(userUuid)) {
            log.info("此次打卡为重复打卡,不做记录");
            return false;
        }
        User user = userService.getByUserUuid(userUuid);
        SignInRecord record = getRecordUuid(signInTask.getUuid(), userUuid);
        if (record == null) {
            log.info("当前用户无打卡任务");
            return true;
        }
        Device device = deviceService.getByDeviceUuid(deviceUuid);
        record.setDeviceUuid(deviceUuid);
        record.setAreaUuid(device.getAreaUuid());
        record.setAreaAddress(device.getAreaAddress());
        record.setAreaCode(device.getAreaCode());
        record.setNumberSn(device.getNumberSn());

        if (deviceSignVo.getSignImageUrl() != null) {
            record.setSignImageUrl(deviceSignVo.getSignImageUrl());
        }
        record.setTaskName(signInTask.getTaskName());
        record.setTaskStartTime(signInTask.getTaskStartTime());
        record.setTaskEndTime(signInTask.getTaskEndTime());
        record.setSignTaskUuid(signInTask.getUuid());
        record.setBodyTemperature(deviceSignVo.getBodyTemperature());
        record.setBodyTemperature(deviceSignVo.getBodyTemperature());
        record.setStatus(SignStatusEnum.SINGED);
        record.setPush(false);
        record.setUuid(record.getUuid());
        record.setInstitutionUuid(signInTask.getInstitutionUuid());

//        OfficialAccountVo officialAccountVo = SignTasks.checkHttpResponse(wechatRemoteService.get(signInTask.getInstitutionUuid()));
//        //个人推送
//        if (signInTask.getPush()) {
//            if (officialAccountVo.isBandMiniapp()) {
//                sendWechat(signInTask, user, device, officialAccountVo,record);
//            }
//        }
        SignStatistics statisticsByUuid = signStatisticsService.getStatisticsByUuid(signStatisticsUUid);
        String alreadyUser = statisticsByUuid.getAlreadyUser();
        List<String> users = new ArrayList<>();
        if (!StringUtils.isBlank(alreadyUser)) {
            users = JSONObject.parseArray(alreadyUser, String.class);
        }
        users.add(userUuid);
        statisticsByUuid.setAlreadyUser(JSONObject.toJSONString(users));
        List<String> allUser = JSONObject.parseArray(statisticsByUuid.getAllUser(),String.class);
        statisticsByUuid.setNotUser(JSONObject.toJSONString(ListUtil.difference(users,allUser)));
        signStatisticsService.updateById(statisticsByUuid);

        stringRedisTemplate.opsForSet().add(key, userUuid);
        stringRedisTemplate.expire(key, JuheUtil.getDistanceTomorrowSeconds(LocalDate.now()),
                TimeUnit.SECONDS);
        return signInRecordService.update(record, new LambdaQueryWrapper<SignInRecord>().eq(SignInRecord::getUuid,
                record.getUuid()));

    }


    //个人推送
    private void sendWechat(SignInTask signInTask, User user, Device device, OfficialAccountVo officialAccountVo, SignInRecord record) {

        OfficialAccountSendVo sendVo = new OfficialAccountSendVo();
        sendVo.setOfficialAccountUuid(officialAccountVo.getUuid());
        sendVo.setTemplateType(templateType);
        sendVo.setUserUuids(Arrays.asList(user.getUuid()));
        OfficialAccountSendVo.ParamsBean paramsBean = new OfficialAccountSendVo.ParamsBean();
        paramsBean.setFirst(String.format("您好,【%s】已成功签到", signInTask.getName()));
        paramsBean.setKeyword1(device.getAreaAddress());
        paramsBean.setKeyword2(user.getName());
        paramsBean.setKeyword3(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm")));
        paramsBean.setRemark("祝您工作顺利");
        sendVo.setParams(paramsBean);
        try {
            wechatRemoteService.sendListByUser(sendVo);
            record.setPush(true);
        } catch (Exception e) {
            record.setPush(false);
        }
    }


    private SignInRecord getRecordUuid(String uuid, String userUuid) {
        String recordUuid = stringRedisTemplate.opsForValue().get(RedisConstant.RECORD_KEY + uuid + userUuid);
        return signInRecordService.getByUuid(recordUuid);
    }

    /**
     * 获取统计uuid
     * @param signInTask
     * @param now
     * @return
     */
    public String getSignStatisticsUUid(SignInTask signInTask, LocalDate now) {
        //key taskUuid  value StatisticsUuid
        String key = RedisConstant.STATISTICS_UUID_KEY + signInTask.getUuid();
        String statisticsUuidJson = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(statisticsUuidJson)) {
            //统计初始化
            SignStatistics signStatistics = SignStatisticsInIt(signInTask, now, key);
            return signStatistics.getUuid();
        }
        return statisticsUuidJson.split("&&")[0];
    }

    /**
     * 初始化统计表
     * @param signInTask
     * @param now
     * @param key
     * @return
     */
    @NotNull
    private SignStatistics SignStatisticsInIt(SignInTask signInTask, LocalDate now, String key) {
        SignStatistics signStatistics = new SignStatistics();
        BeanUtils.copyProperties(signInTask, signStatistics, "uuid", "id", "version", "createTime", "updateTime",
                "creator", "editor", "delete");
        signStatistics.setTaskUuid(signInTask.getUuid());
        signStatistics.setCreateDate(now);
        signStatistics.setInstitutionUuid(signInTask.getInstitutionUuid());
        if (Objects.isNull(signInTask.getUserUuids())){
          throw new RuntimeException("任务人员未初始化");
        }
        signStatistics.setAllUser(signInTask.getUserUuids());
        signStatistics.setNotUser(signInTask.getUserUuids());
        signStatisticsService.save(signStatistics);
        stringRedisTemplate.opsForValue().set(
                key, signStatistics.getUuid() + "&&0", JuheUtil.getDistanceTomorrowSeconds(LocalDate.now()),
                TimeUnit.SECONDS);

        RecordInIt(signInTask, signStatistics);
        return signStatistics;
    }

    /**
     * 初始化流水记录
     * @param signInTask
     * @param signStatistics
     */
    private void RecordInIt(SignInTask signInTask, SignStatistics signStatistics) {
        List<String> userUuids = JSONObject.parseArray(signInTask.getUserUuids(), String.class);
        for (String userUuid : userUuids) {
            String key = RedisConstant.RECORD_KEY + signInTask.getUuid() + userUuid;
            SignInRecord record = new SignInRecord();
            BeanUtils.copyProperties(signInTask, record, "uuid", "id", "version", "createTime", "updateTime",
                    "creator", "editor", "delete");
            record.setSignTaskUuid(signInTask.getUuid());
            record.setSignStatisticsUuid(signStatistics.getUuid());
            User user = userService.getByUserUuid(userUuid);
            record.setSignImageUrl(user.getHeadUrl());
            record.setUsername(user.getName());
            record.setUserUuid(user.getUuid());
            record.setStatus(SignStatusEnum.NOT_SING);
            signInRecordService.save(record);
            stringRedisTemplate.opsForValue().set(key, record.getUuid(),
                    JuheUtil.getDistanceTomorrowSeconds((LocalDate.now())),
                    TimeUnit.SECONDS);
        }

    }

}
