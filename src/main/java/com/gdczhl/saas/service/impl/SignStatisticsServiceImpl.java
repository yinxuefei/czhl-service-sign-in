package com.gdczhl.saas.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdczhl.saas.controller.weapp.bo.vo.RecordPageVo;
import com.gdczhl.saas.controller.weapp.bo.vo.SignTaskStatusVo;
import com.gdczhl.saas.entity.*;
import com.gdczhl.saas.enums.BodyStatusEnum;
import com.gdczhl.saas.pojo.MoreConfig;
import com.gdczhl.saas.pojo.bo.signInStatistics.SignStatisticsPageBo;
import com.gdczhl.saas.pojo.bo.signInStatistics.UserStatisticsCountBo;
import com.gdczhl.saas.utils.ContextCache;
import com.gdczhl.saas.mapper.SignStatisticsMapper;
import com.gdczhl.saas.service.ISignInRecordService;
import com.gdczhl.saas.service.ISignInTaskService;
import com.gdczhl.saas.service.ISignStatisticsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdczhl.saas.service.IUserService;
import com.gdczhl.saas.vo.PageVo;
import com.google.common.collect.Sets;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hkx
 * @since 2023-01-14
 */
@Service
@Transactional
public class SignStatisticsServiceImpl extends ServiceImpl<SignStatisticsMapper, SignStatistics> implements ISignStatisticsService {


    @Autowired
    private ISignInRecordService signInRecordService;

    @Autowired
    private ISignInTaskService signInTaskService;

    @Autowired
    private IUserService userService;

    @Override
    public PageVo<SignStatisticsPageBo> pageBo(LocalDate startDate, LocalDate endDate, String uuid, Integer pageNo,
                                             Integer pageSize) {

        Page<SignStatistics> page = new Page<>(pageNo, pageSize);

        PageVo<SignStatisticsPageBo> result = new PageVo<>();

        LambdaQueryWrapper<SignStatistics> qw = new LambdaQueryWrapper<>();
        buildQueryWrapper(startDate, endDate, uuid, qw);
        //分页
        page(page, qw);

        List<SignStatisticsPageBo> records = page.getRecords().stream().map(signStatistics -> {
            SignStatisticsPageBo signStatisticsPageBo = new SignStatisticsPageBo();
            BeanUtils.copyProperties(signStatistics, signStatisticsPageBo);
            signStatisticsPageBo.setAllUser(parseJsonToList(signStatistics.getAllUser()));
            signStatisticsPageBo.setNotUser(parseJsonToList(signStatistics.getNotUser()));
            signStatisticsPageBo.setReUser(parseJsonToList(signStatistics.getReUser()));
            signStatisticsPageBo.setAlreadyUser(parseJsonToList(signStatistics.getAlreadyUser()));
            return signStatisticsPageBo;
        }).collect(Collectors.toList());

        BeanUtils.copyProperties(page, result,"records");
        result.setRecords(records);
        return result;
    }

    private void buildQueryWrapper(LocalDate startDate, LocalDate endDate, String uuid,
                                   LambdaQueryWrapper<SignStatistics> qw) {
        //按日
        if (startDate != null && endDate != null) {
            qw.between(SignStatistics::getCreateDate, startDate,
                    endDate);
        }

        if (StringUtils.hasText(uuid)) {
            qw.eq(SignStatistics::getTaskUuid, uuid);
        }

        qw.eq(SignStatistics::getIsEnable, true)
          .eq(SignStatistics::getInstitutionUuid,ContextCache.getInstitutionUuid());
    }

    @Override
    public SignStatistics getStatisticsByUuid(String uuid) {
        LambdaQueryWrapper<SignStatistics> qw = new LambdaQueryWrapper<>();
        qw.eq(BaseEntity::getUuid, uuid)
                .last("limit 0,1");
        return getOne(qw);
    }

    @Override
    public UserStatisticsCountBo getUserCountStatistics(LocalDate startDate, LocalDate endDate, String uuid) {
        HashSet<String> set = Sets.newHashSet();
        LambdaQueryWrapper<SignStatistics> qw = new LambdaQueryWrapper<>();
        buildQueryWrapper(startDate, endDate, uuid, qw);
        List<SignStatistics> list = list(qw);
        if (CollectionUtils.isEmpty(list)) {
            return UserStatisticsCountBo.builder().signed(0).notSign(0).reSign(0).allSign(0).countSign(0).build();
        }
        UserStatisticsCountBo result = new UserStatisticsCountBo();
        Integer allSign = 0;//应签
        Integer resign = 0;
        Integer notSign = 0;
        Integer signed = 0;

        for (SignStatistics signStatistics : list) {
            if (StringUtils.hasText(signStatistics.getAllUser())){
            set.addAll(JSONObject.parseArray(signStatistics.getAllUser(),String.class));}
            allSign += parseJsonToList(signStatistics.getAllUser()).size();
            resign += parseJsonToList(signStatistics.getReUser()).size();
            notSign += (parseJsonToList(signStatistics.getNotUser()).size() - parseJsonToList(signStatistics.getAskLeaveUser()).size());
            signed += parseJsonToList(signStatistics.getAlreadyUser()).size();
        }

        result.setSigned(signed);
        result.setNotSign(notSign);
        result.setReSign(resign);
        result.setAllSign(allSign);
        result.setCountSign(set.size());
        return result;
    }

    @Override
    public List<SignStatistics> getTodayTasks(LocalDate date, String operatorUuid) {
        LambdaQueryWrapper<SignStatistics> qw = new LambdaQueryWrapper<>();
        qw.eq(SignStatistics::getCreateDate, date);
        List<SignStatistics> signStatisticsList = list(qw);
        //为任务负责人保留
        List<SignStatistics> statisticsList = signStatisticsList.stream().filter(signStatistics -> {
            String taskUuid = signStatistics.getTaskUuid();
            SignInTask task = signInTaskService.getTaskByUuid(taskUuid);
            MoreConfig moreConfig = JSONObject.parseObject(task.getMoreConfig(), MoreConfig.class);
            if (moreConfig != null && moreConfig.getManager() != null && moreConfig.getManager().getIsManager()) {
                if (moreConfig.getManager().getManagerUuids().contains(operatorUuid)) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
        return statisticsList;
    }

    @Override
    public SignStatistics getStatisticsByTaskUuid(String taskUuid,LocalDate date) {
        LambdaQueryWrapper<SignStatistics> qw = new LambdaQueryWrapper<>();
        qw.eq(SignStatistics::getTaskUuid, taskUuid)
                .eq(SignStatistics::getCreateDate,date);
        return getOne(qw);
    }

    @Override
    public void updateByUuid(SignStatistics statistics) {
        LambdaQueryWrapper<SignStatistics> eq = new LambdaQueryWrapper<SignStatistics>().eq(SignStatistics::getUuid, statistics.getUuid());
        update(statistics,eq);
    }

    private static List<String> parseJsonToList(String json) {
        if (StringUtils.isEmpty(json)) {
            return new ArrayList<>();
        }
        return JSONObject.parseArray(json, String.class);
    }
}
