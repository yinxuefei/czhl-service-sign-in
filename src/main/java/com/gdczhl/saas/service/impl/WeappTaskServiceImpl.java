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
    public List<DayTaskVo> daySignTask(LocalDate date, String operatorUuid) {

        List<DayTaskVo> result = new ArrayList<>();
        //为负责人的任务
        List<SignInTask> todayTasks = signInTaskService.getManageTodayTasks(date, operatorUuid);

        for (SignInTask task : todayTasks) {
            DayTaskVo dayTaskVo = CzBeanUtils.copyProperties(task, DayTaskVo::new);
            dayTaskVo.setUuid(task.getUuid());
            List<String> users = new ArrayList<>();
            if (StringUtils.hasText(task.getUserUuids())) {
                users = getUsersByJson(task.getUserUuids(), String.class);
            }
            dayTaskVo.setAllSignInCount(users.size());

            SignStatistics statistics = signStatisticsService.getStatisticsByTaskUuid(task.getUuid(), date);
            if (statistics == null) {
                // 任务未开始
                dayTaskVo.setSignInCount(0);
                dayTaskVo.setNotSignInCount(users.size());
                result.add(dayTaskVo);
                continue;
            }

            if (StringUtils.isEmpty(statistics.getAlreadyUser())) {
                // 任务进行中
                String key = RedisConstant.USER_UUID_KEY + task.getUuid();
                Set<String> userUuids = stringRedisTemplate.opsForSet().members(key);
                if (CollectionUtils.isEmpty(userUuids)) {
                    //已签为0
                    dayTaskVo.setSignInCount(0);
                    dayTaskVo.setNotSignInCount(users.size());
                    result.add(dayTaskVo);
                    continue;
                } else {
                    dayTaskVo.setSignInCount(userUuids.size());
                    dayTaskVo.setNotSignInCount(users.size() - userUuids.size());
                    result.add(dayTaskVo);
                    continue;
                }
            }
            //任务已结束
            dayTaskVo.setSignInCount(getUsersByJson(statistics.getAlreadyUser(), String.class).size());
            dayTaskVo.setNotSignInCount(getUsersByJson(statistics.getNotUser(), String.class).size());
            result.add(dayTaskVo);
        }
        return result;
    }

    private Integer getSignInCount(String uuid, SignStatusEnum signStatusEnum) {
        LambdaUpdateWrapper<SignInRecord> qw = new LambdaUpdateWrapper<>();
        qw.eq(SignInRecord::getSignStatisticsUuid, uuid)
                .eq(SignInRecord::getStatus, signStatusEnum);
        return signInRecordService.list(qw).size();
    }

    private <T> List<T> getUsersByJson(String json, Class<T> clazz) {
        return JSONObject.parseArray(json, clazz);
    }

    @Override
    public List<SignTaskStatusVo> weekSignTask(LocalDate startDate, LocalDate endDate, String userUuid) {
        List<LocalDate> dateList = TimeUtil.getPeriodDate(startDate, endDate);
        List<SignTaskStatusVo> result = Lists.newArrayList();

        for (LocalDate localDate : dateList) {
            List<SignInTask> todayTasks = signInTaskService.getManageTodayTasks(localDate, userUuid);
            SignTaskStatusVo vo = new SignTaskStatusVo();
            vo.setNowDate(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            vo.setIsSignIn(getTodayStatus(todayTasks, localDate));
            result.add(vo);
        }
        return result;
    }


    private Integer getTodayStatus(List<SignInTask> signInTasks, LocalDate localDate) {
        if (CollectionUtils.isEmpty(signInTasks)) {
            //无任务
            return 2;
        }
        for (SignInTask signInTask : signInTasks) {
            SignStatistics statistics = signStatisticsService.getStatisticsByTaskUuid(signInTask.getUuid(), localDate);
            if (statistics == null) {
                //正常记录
                return 0;
            }
            List<SignInRecord> signInRecordList = signInRecordService.getByStatisticsUuid(statistics.getUuid());
            List<SignInRecord> collect = signInRecordList.stream().filter(signInRecord -> {
                if (signInRecord.getStatus().equals(SignStatusEnum.RESIGN) || signInRecord.getStatus().equals(SignStatusEnum.NOT_SING)) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(collect)) {
                return 0;
            } else {
                return 1;
            }
        }
        return null;
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
            recordPageVo.setUserUuid(record.getUserUuid());
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
