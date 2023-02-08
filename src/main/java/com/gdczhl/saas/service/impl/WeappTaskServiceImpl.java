package com.gdczhl.saas.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdczhl.saas.controller.weapp.bo.vo.DayTaskVo;
import com.gdczhl.saas.controller.weapp.bo.vo.RecordPageVo;
import com.gdczhl.saas.controller.weapp.bo.vo.SignTaskStatusVo;
import com.gdczhl.saas.entity.*;
import com.gdczhl.saas.enums.BodyStatusEnum;
import com.gdczhl.saas.pojo.RedisConstant;
import com.gdczhl.saas.enums.SignStatusEnum;
import com.gdczhl.saas.service.*;
import com.gdczhl.saas.service.remote.IWeappTaskService;
import com.gdczhl.saas.utils.*;
import com.gdczhl.saas.vo.PageVo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hkx
 * @since 2023-01-06
 */
@Service
@Transactional
@Slf4j
public class WeappTaskServiceImpl implements IWeappTaskService {


    @Autowired
    private ISignStatisticsService signStatisticsService;

    @Autowired
    private ISignInTaskService signInTaskService;

    @Autowired
    private ISignInRecordService signInRecordService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<DayTaskVo> daySignTask(LocalDate date,String operatorUuid) {

        List<DayTaskVo> result = new ArrayList<>();
        //为负责人的任务
        List<SignInTask> todayTasks = signInTaskService.getManageTodayTasks(date, operatorUuid);

        for (SignInTask task : todayTasks) {
            DayTaskVo dayTaskVo = CzBeanUtils.copyProperties(task, DayTaskVo::new);
            dayTaskVo.setUuid(task.getUuid());
            List<String> users = getUsersByJson(task.getUserUuids(), String.class);
            dayTaskVo.setAllSignInCount(users.size());
            SignStatistics statistics = signStatisticsService.getStatisticsByTaskUuid(task.getUuid(), date);
            if (statistics == null || StringUtils.isEmpty(statistics.getReUser())) {
                String key = RedisConstant.USER_UUID_KEY + task.getUuid();
                Set<String> userUuids = stringRedisTemplate.opsForSet().members(key);
                if (CollectionUtils.isEmpty(userUuids)) {
                    // 任务未开始
                    dayTaskVo.setSignInCount(0);
                    dayTaskVo.setNotSignInCount(users.size()-0);
                    result.add(dayTaskVo);
                    continue;
                }
                // 任务进行中
                dayTaskVo.setSignInCount(getSignInCount(statistics.getUuid(),SignStatusEnum.SINGED));
                dayTaskVo.setNotSignInCount(getSignInCount(statistics.getUuid(),SignStatusEnum.NOT_SING));
                result.add(dayTaskVo);
                continue;
            }
            dayTaskVo.setSignInCount(getUsersByJson(statistics.getAlreadyUser(), String.class).size());
            dayTaskVo.setNotSignInCount(getUsersByJson(statistics.getNotUser(), String.class).size());
            result.add(dayTaskVo);
        }
        return result;
    }

    private Integer getSignInCount(String uuid, SignStatusEnum signStatusEnum) {
        LambdaUpdateWrapper<SignInRecord> qw = new LambdaUpdateWrapper<>();
        qw.eq(SignInRecord::getSignStatisticsUuid,uuid)
                .eq(SignInRecord::getStatus,signStatusEnum);
        return signInRecordService.list(qw).size();
    }

    private <T> List<T> getUsersByJson(String json, Class<T> clazz) {
        return JSONObject.parseArray(json, clazz);
    }

    @Override
    public List<SignTaskStatusVo> weekSignTask(LocalDate startDate, LocalDate endDate ,String userUuid) {
        List<LocalDate> dateList = TimeUtil.getPeriodDate(startDate, endDate);
        List<SignTaskStatusVo> result = Lists.newArrayList();

        for (LocalDate localDate : dateList) {
            List<SignStatistics> signStatistics = signStatisticsService.getTodayTasks(localDate,userUuid);
            SignTaskStatusVo vo = new SignTaskStatusVo();
            vo.setNowDate(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            vo.setIsSignIn(getTodayStatus(signStatistics));
            result.add(vo);
        }
        return result;
    }

    private static Integer getTodayStatus(List<SignStatistics> signStatistics) {
        if (CollectionUtils.isEmpty(signStatistics)){
            //无任务
            return 2;
        }
        for (SignStatistics statistics : signStatistics) {
            if (StringUtils.hasText(statistics.getNotUser())) {
                //有未签人员
                return 1;
            }
            if (StringUtils.hasText(statistics.getReUser())) {
                //有补签人员
                return 1;
            }
        }
        return 0;
    }


    @Override
    public PageVo<RecordPageVo> StatisticsPage(LocalDate date, String taskUuid, Integer status, Integer pageNo,
                                               Integer pageSize, String username) {
        Page<SignInRecord> page = new Page<>(pageNo, pageSize);
        PageVo<RecordPageVo> result = new PageVo<>();
        ArrayList<RecordPageVo> pageVoRecord = new ArrayList<>();

        //1.查询所有已签用户
        SignStatistics signStatistics = signStatisticsService.getStatisticsByTaskUuid(taskUuid, date);

        if (signStatistics == null) {
                // 任务未开始
                BeanUtils.copyProperties(page, result, "records");
                result.setRecords(pageVoRecord);
                return result;
        }
        //进行中
        Page<SignInRecord> signInRecordPage = signInRecordService.getPageByStatisticsUuid(signStatistics.getUuid(),
                pageNo, pageSize, status, username);
        List<SignInRecord> records = signInRecordPage.getRecords();
        for (SignInRecord record : records) {
            RecordPageVo recordPageVo = new RecordPageVo();
            BeanUtils.copyProperties(record, recordPageVo);
            if (record.getBodyTemperature() != null) {
                if (record.getBodyTemperature() >= 37.3) {
                    recordPageVo.setTemperatureStatus(BodyStatusEnum.FEVER.getCode());
                } else {
                    recordPageVo.setTemperatureStatus(BodyStatusEnum.NORMAL.getCode());
                }
            } else {
                recordPageVo.setTemperatureStatus(BodyStatusEnum.NULL.getCode());
            }
            recordPageVo.setPicture(record.getSignImageUrl());
            recordPageVo.setSignStatus(record.getStatus().getCode());
            recordPageVo.setSignTime(record.getCreateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm" +
                    ":ss")));
            pageVoRecord.add(recordPageVo);
        }
        BeanUtils.copyProperties(signInRecordPage, result);
        result.setRecords(pageVoRecord);
        return result;
    }

}
