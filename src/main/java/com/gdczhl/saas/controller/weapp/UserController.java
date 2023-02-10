package com.gdczhl.saas.controller.weapp;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gdczhl.saas.controller.weapp.bo.vo.RecordPageVo;
import com.gdczhl.saas.controller.weapp.bo.vo.UserTaskVo;
import com.gdczhl.saas.entity.SignInRecord;
import com.gdczhl.saas.entity.SignInTask;
import com.gdczhl.saas.entity.User;
import com.gdczhl.saas.enums.SignStatusEnum;
import com.gdczhl.saas.pojo.MoreConfig;
import com.gdczhl.saas.pojo.RedisConstant;
import com.gdczhl.saas.service.ISignInRecordService;
import com.gdczhl.saas.service.ISignInTaskService;
import com.gdczhl.saas.service.ISignStatisticsService;
import com.gdczhl.saas.service.IUserService;
import com.gdczhl.saas.utils.ContextCache;
import com.gdczhl.saas.utils.CzBeanUtils;
import com.gdczhl.saas.utils.StringUtils;
import com.gdczhl.saas.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Api(tags = "用户统计")
@Slf4j
@RestController
@RequestMapping("weapp/userStatistics")
public class UserController {

    @Autowired
    private ISignInTaskService signInTaskService;

    @Autowired
    private ISignInRecordService signInRecordService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private IUserService userService;

    @GetMapping("userInfo")
    @ApiOperation("当前用户名称")
    public ResponseVo<String> userInfo() {
     return ResponseVo.success(userService.getByUserUuid(ContextCache.getOperatorUuid()).getName());
    }

    @GetMapping("isManager")
    @ApiOperation("是否为任务负责人")
    public ResponseVo<Boolean> isManager(){
        //当前用户Uuid
        String userUuid = ContextCache.getOperatorUuid();
        List<SignInTask> signInTasks = signInTaskService.list();
        for (SignInTask signInTask : signInTasks) {
            MoreConfig moreConfig = JSONObject.parseObject(signInTask.getMoreConfig(), MoreConfig.class);
            if (moreConfig.getManager()!=null && moreConfig.getManager().getIsManager()){
                List<String> managerUuids = moreConfig.getManager().getManagerUuids();
                if (!CollectionUtils.isEmpty(managerUuids) && managerUuids.contains(userUuid)){
                    return ResponseVo.success(true);
                }
            }
        }
        return ResponseVo.success(false);
    }

    @GetMapping("userSignTask")
    @ApiOperation("我的签到")
    public ResponseVo<List<UserTaskVo>> userSignTask(@ApiParam("日期 yyyy-MM-dd") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ,String userUuid) {
        //获取当前用户 某天内所有签到 按开始时间正序排序
        if (StringUtils.isBlank(userUuid)){
            userUuid = ContextCache.getOperatorUuid();
        }

       List<SignInTask> todayTasks = signInTaskService.getUserTodayTasks(date, userUuid);

        ArrayList<UserTaskVo> result = new ArrayList<>();
        for (SignInTask todayTask : todayTasks) {
            UserTaskVo vo = new UserTaskVo();
            CzBeanUtils.copyProperties(todayTask,vo);
            vo.setUserUuid(userUuid);
            LocalDateTime startTime = LocalDateTime.of(date,todayTask.getTaskStartTime());
            LocalDateTime endTime =  LocalDateTime.of(date,todayTask.getTaskEndTime());
            LocalDateTime now = LocalDateTime.now();

            SignInRecord record = signInRecordService.getTaskByDateUuid(date, todayTask.getTaskStartTime(), todayTask.getTaskEndTime(),
                    todayTask.getUuid(), userUuid);

            if (now.isBefore(startTime)){
                vo.setTaskStatus(1);
                result.add(vo);
                continue;
            }

            if (now.isAfter(startTime)&&now.isBefore(endTime)){
                //进行中
                vo.setTaskStatus(1);
                if (record==null){
                    vo.setSignStatus(SignStatusEnum.NOT_SING.getCode());
                    continue;
                }
                vo.setSignStatus(record.getStatus().getCode());
                BeanUtils.copyProperties(record,vo);
                if (Objects.nonNull(record.getUpdateTime())){
                vo.setSignTime(record.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
                vo.setPicture(record.getSignImageUrl());
                result.add(vo);
                continue;
            }
                //已结束
                vo.setTaskStatus(2);
                if (record==null){
                vo.setSignStatus(SignStatusEnum.NOT_SING.getCode());
                result.add(vo);
                continue;
                 }
                vo.setSignStatus(record.getStatus().getCode());
                BeanUtils.copyProperties(record,vo);
                vo.setSignTime(record.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                vo.setPicture(record.getSignImageUrl());
                result.add(vo);
        }
        return ResponseVo.success(result);
    }

    private SignInRecord getRecordUuid(String uuid, String userUuid) {
        String recordUuid = stringRedisTemplate.opsForValue().get(RedisConstant.RECORD_KEY + uuid + userUuid);
        return signInRecordService.getByUuid(recordUuid);
    }
}