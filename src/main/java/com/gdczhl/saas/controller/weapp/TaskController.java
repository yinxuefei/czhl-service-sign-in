package com.gdczhl.saas.controller.weapp;

import com.gdczhl.saas.controller.weapp.bo.vo.*;

import com.gdczhl.saas.service.ISignStatisticsService;
import com.gdczhl.saas.service.remote.IWeappTaskService;


import com.gdczhl.saas.utils.ContextCache;
import com.gdczhl.saas.utils.StringUtils;
import com.gdczhl.saas.vo.PageVo;
import com.gdczhl.saas.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Api(tags = "签到任务统计")
@Slf4j
@RestController
@RequestMapping("weapp/taskStatistics")
public class TaskController {

    @Autowired
    private IWeappTaskService weappTaskService;

    @GetMapping("weekSignTask")
    @ApiOperation("签到状态")
    public ResponseVo<List<SignTaskStatusVo>> weekSignTask(@ApiParam("开始日期") @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate startDate,
                                                     @ApiParam("结束日期 yyyy-MM-dd") @DateTimeFormat(pattern = "yyyy-MM" +
                                                             "-dd") @RequestParam LocalDate endDate,String userUuid) {
        if (StringUtils.isBlank(userUuid)){
            userUuid = ContextCache.getOperatorUuid();
        }
        return ResponseVo.success(weappTaskService.weekSignTask(startDate, endDate,userUuid));
    }


    @GetMapping("daySignTask")
    @ApiOperation("当天内任务")
    public ResponseVo<List<DayTaskVo>> daySignTask(@ApiParam("日期 yyyy-MM-dd") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        String operatorUuid = ContextCache.getOperatorUuid();
        List<DayTaskVo> dayTaskVos = weappTaskService.daySignTask(date,operatorUuid);
        return ResponseVo.success(dayTaskVos);
    }


    @GetMapping("StatisticsPage")
    @ApiOperation("签到情况分页")
    public ResponseVo<PageVo<RecordPageVo>> StatisticsPage(@ApiParam("日期 yyyy-MM-dd") @DateTimeFormat(pattern =
            "yyyy-MM-dd") LocalDate date,
                                                           @ApiParam("任务uuid") String taskUuid,
                                                           @ApiParam("0未签 1已签 2补签 null全部") Integer status,
                                                           @ApiParam("pageNo") @RequestParam(defaultValue = "1") Integer pageNo,
                                                           @ApiParam("pageSize") @RequestParam(defaultValue = "20") Integer pageSize,
                                                           @ApiParam("用户名称") String username) {
        return ResponseVo.success(weappTaskService.StatisticsPage(date, taskUuid,status,pageNo,pageSize,username));
    }


}