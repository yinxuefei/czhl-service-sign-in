package com.gdczhl.saas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdczhl.saas.enums.SignInModeEnum;
import com.gdczhl.saas.pojo.vo.signInRecord.SignInRecordPageVo;
import com.gdczhl.saas.entity.BaseEntity;
import com.gdczhl.saas.entity.SignInRecord;
import com.gdczhl.saas.entity.SignInTask;
import com.gdczhl.saas.enums.BodyStatusEnum;
import com.gdczhl.saas.enums.SignStatusEnum;
import com.gdczhl.saas.mapper.SignInRecordMapper;
import com.gdczhl.saas.service.ISignInRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdczhl.saas.service.ISignInTaskService;
import com.gdczhl.saas.utils.SignTasks;
import com.gdczhl.saas.vo.PageVo;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
public class SignInRecordServiceImpl extends ServiceImpl<SignInRecordMapper, SignInRecord> implements ISignInRecordService {

    @Autowired
    private ISignInTaskService signInTaskService;


    @Override
    public PageVo<SignInRecordPageVo> getSignRecordPage(LocalDate startDate, LocalDate endDate, String taskUuid, String name, String areaCode,
                                                        Integer pageNo,
                                                        Integer pageSize) {
        Page<SignInRecord> signInRecordPage = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<SignInRecord> lambda = new LambdaQueryWrapper<>();
        lambda.like(!StringUtils.isEmpty(name), SignInRecord::getUsername, name)
                .eq(!StringUtils.isEmpty(taskUuid), SignInRecord::getSignTaskUuid, taskUuid)
                .likeRight(Objects.nonNull(areaCode), SignInRecord::getAreaUuid, areaCode);


        if (Objects.nonNull(startDate) && Objects.nonNull(endDate)) {
            lambda.between(BaseEntity::getCreateTime, LocalDateTime.of(startDate, LocalTime.MIN),
                    LocalDateTime.of(endDate, LocalTime.MAX));
        }

        page(signInRecordPage, lambda);


        //封装记录为vo
        PageVo<SignInRecordPageVo> result = new PageVo<>();
        List<SignInRecord> records = signInRecordPage.getRecords();
        ArrayList<SignInRecordPageVo> arrayList = new ArrayList<>();
        BeanUtils.copyProperties(signInRecordPage, result, "records");
        if (CollectionUtils.isEmpty(records)) {
            result.setRecords(arrayList);
            return result;
        }

        for (SignInRecord signInRecord : records) {
            SignInRecordPageVo signInRecordPageVo = new SignInRecordPageVo();
            BeanUtils.copyProperties(signInRecord, signInRecordPageVo);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            signInRecordPageVo.setCreateDate(signInRecord.getCreateTime().toLocalDate().format(dateFormatter));
            signInRecordPageVo.setCreateTime(signInRecord.getCreateTime().toLocalTime().format(timeFormatter));
            SignInTask task = signInTaskService.getTaskByUuid(signInRecord.getSignTaskUuid());
            signInRecordPageVo.setTimePeriod(SignTasks.getTaskNameResult(task));

            Float bodyTemperature = signInRecord.getBodyTemperature();
            if (null == bodyTemperature || bodyTemperature < 0.0) {
                signInRecordPageVo.setBodyStatus(BodyStatusEnum.NULL.getCode());
            } else if (0.0 <= bodyTemperature && bodyTemperature <= 37.3) {
                signInRecordPageVo.setBodyStatus(BodyStatusEnum.NORMAL.getCode());
            } else if (bodyTemperature > 37.3) {
                signInRecordPageVo.setBodyStatus(BodyStatusEnum.FEVER.getCode());
            }
            arrayList.add(signInRecordPageVo);
        }

        result.setRecords(arrayList);

        return result;
    }

    @Override
    public Page<SignInRecord> getUserSignStatistics(Integer status, String name, String statisticsUuid,
                                                    Integer pageNo, Integer pageSize, LocalDate startDate, LocalDate endDate, String taskUuid) {

        Page<SignInRecord> result = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<SignInRecord> qw = new LambdaQueryWrapper<SignInRecord>()
                .eq(StringUtils.hasText(statisticsUuid), SignInRecord::getSignStatisticsUuid, statisticsUuid)
                .like(StringUtils.hasText(name), SignInRecord::getUsername, name)
                .eq(Objects.nonNull(status), SignInRecord::getStatus, SignStatusEnum.getByCode(status))
                .between(Objects.nonNull(startDate) && Objects.nonNull(endDate), SignInRecord::getCreateTime,
                        LocalDateTime.of(startDate, LocalTime.MIN),
                        LocalDateTime.of(endDate, LocalTime.MAX))
                .eq(StringUtils.isEmpty(taskUuid), SignInRecord::getSignTaskUuid, taskUuid)
                .eq(SignInRecord::getIsEnable,true);

        return page(result, qw);
    }

    @Override
    public SignInRecord getByStatisticsUserUuid(String statisticsUuid, String userUuid) {
        if (Objects.isNull(statisticsUuid) && Objects.isNull(userUuid)) {
            throw new RuntimeException("参数为空");
        }
        LambdaQueryWrapper<SignInRecord> qw = new LambdaQueryWrapper<SignInRecord>()
                .eq(SignInRecord::getSignStatisticsUuid, statisticsUuid)
                .eq(SignInRecord::getUserUuid, userUuid);
        return getOne(qw);
    }

    @Override
    public void deleteByTaskUuid(String taskUuid) {

        LambdaQueryWrapper<SignInRecord> eq = new LambdaQueryWrapper<SignInRecord>().eq(SignInRecord::getSignTaskUuid, taskUuid);
       remove(eq);
    }

    @Override
    public List<SignInRecord> getListByStatisticsUuid(String signStatisticsUuid) {
        LambdaQueryWrapper<SignInRecord> eq = new LambdaQueryWrapper<SignInRecord>().eq(SignInRecord::getSignStatisticsUuid,
                signStatisticsUuid);
        return list(eq);
    }

    @Override
    public Page<SignInRecord> getPageByStatisticsUuid(String uuid, Integer pageNo, Integer pageSize, Integer status,
                                                      String username) {
        Page<SignInRecord> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<SignInRecord> eq = new LambdaQueryWrapper<SignInRecord>()
                .eq(SignInRecord::getSignStatisticsUuid, uuid)
                .eq(Objects.nonNull(status),SignInRecord::getStatus, status)
                .like(StringUtils.hasText(username),SignInRecord::getUsername, username);
        page(page,eq);
        return page;
    }

    @Override
    public SignInRecord getByUuid(String recordUuid) {
        LambdaQueryWrapper<SignInRecord> eq = new LambdaQueryWrapper<SignInRecord>().eq(SignInRecord::getUuid,
                recordUuid);
        return getOne(eq);
    }

    @Override
    public SignInRecord getTaskByDateUuid(LocalDate date,LocalTime taskStartTime, LocalTime taskEndTime,
                                                String taskUuid,String uuid) {
        LambdaQueryWrapper<SignInRecord> qw = new LambdaQueryWrapper<>();
        qw.eq(SignInRecord::getSignTaskUuid,taskUuid)
                .between(SignInRecord::getCreateTime,LocalDateTime.of(date,taskStartTime),LocalDateTime.of(date,
                        taskEndTime))
                .eq(SignInRecord::getUserUuid,uuid)
                .last("limit 0,1");
        return getOne(qw);
    }

    @Override
    public void updateByUuid(SignInRecord record) {
        LambdaUpdateWrapper<SignInRecord> eq = new LambdaUpdateWrapper<SignInRecord>().eq(SignInRecord::getUuid, record.getUuid());
        update(record,eq);
    }
}
