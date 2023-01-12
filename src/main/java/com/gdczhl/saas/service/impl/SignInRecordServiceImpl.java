package com.gdczhl.saas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdczhl.saas.controller.external.pojo.vo.SignInRecordPageVo;
import com.gdczhl.saas.controller.external.pojo.vo.SignInTaskPageVo;
import com.gdczhl.saas.entity.BaseEntity;
import com.gdczhl.saas.entity.SignInRecord;
import com.gdczhl.saas.entity.SignInTask;
import com.gdczhl.saas.mapper.SignInRecordMapper;
import com.gdczhl.saas.service.ISignInRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdczhl.saas.service.ISignInTaskService;
import com.gdczhl.saas.utils.SignTasks;
import com.gdczhl.saas.vo.PageVo;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
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
    public PageVo<SignInRecordPageVo> getSignRecordPage(LocalDate date, String taskName, String name, String address,Integer pageNo,
                                                       Integer pageSize) {
        Page<SignInRecord> signInRecordPage = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<SignInRecord> lambda = new LambdaQueryWrapper<>();
        lambda.between(Objects.nonNull(date), BaseEntity::getCreateTime, LocalDateTime.of(date, LocalTime.MIN),LocalDateTime.of(date, LocalTime.MAX))
                .like(Objects.nonNull(name),SignInRecord::getUsername,name)
                .eq(Objects.nonNull(address),SignInRecord::getAreaAddress,address)
                .eq(Objects.nonNull(taskName),SignInRecord::getTaskName,taskName);

        page(signInRecordPage,lambda);

        //封装记录为vo
        PageVo<SignInRecordPageVo> result = new PageVo<>();
        List<SignInRecord> records = signInRecordPage.getRecords();
        ArrayList<SignInRecordPageVo> arrayList = new ArrayList<>();
        BeanUtils.copyProperties(signInRecordPage,result,"records");
        if (CollectionUtils.isEmpty(records)){
            result.setRecords(arrayList);
            return result;
        }

        for (SignInRecord signInRecord : records) {
            SignInRecordPageVo signInRecordPageVo = new SignInRecordPageVo();
            BeanUtils.copyProperties(signInRecord,signInRecordPageVo);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            signInRecordPageVo.setCreateDate(signInRecord.getCreateTime().toLocalDate().format(dateFormatter));
            signInRecordPageVo.setCreateTime(signInRecord.getCreateTime().toLocalTime().format(timeFormatter));
            SignInTask task = signInTaskService.getTaskByUuid(signInRecord.getSignTaskUuid());
            signInRecordPageVo.setTimePeriod(SignTasks.getTaskNameResult(task.getName(),task.getTaskName(),
                    task.getTaskStartTime(),task.getTaskEndTime(),DateTimeFormatter.ofPattern("HH:mm")));

            Float bodyTemperature = signInRecord.getBodyTemperature();
            if (null == bodyTemperature || bodyTemperature < 0.0){
                signInRecordPageVo.setBodyTemperature("未检测");
            }else if ( 0.0 <= bodyTemperature && bodyTemperature <= 37.3){
                signInRecordPageVo.setBodyTemperature("体温正常");
            }else if ( bodyTemperature > 37.3 ){
                signInRecordPageVo.setBodyTemperature("发热 ("+bodyTemperature+"℃)");
            }
            arrayList.add(signInRecordPageVo);
        }

        result.setRecords(arrayList);

        return result;
    }
}
