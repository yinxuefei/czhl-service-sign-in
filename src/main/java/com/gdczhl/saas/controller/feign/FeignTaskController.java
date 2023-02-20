package com.gdczhl.saas.controller.feign;

import com.alibaba.fastjson.JSONObject;
import com.gdczhl.saas.controller.external.vo.task.more.MoreConfig;
import com.gdczhl.saas.controller.feign.vo.DeviceSignVo;
import com.gdczhl.saas.controller.feign.vo.ReportTaskVo;
import com.gdczhl.saas.controller.feign.vo.SignInInfoVo;
import com.gdczhl.saas.entity.SignInTask;
import com.gdczhl.saas.service.IThirdTaskService;
import com.gdczhl.saas.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "班牌签到")
@Slf4j
@RestController
@RequestMapping("feign/signIn")
public class FeignTaskController {

    @Autowired
    private IThirdTaskService thirdTaskService;

    @GetMapping("todayTasks")
    @ApiOperation("当天任务计划发布")
    public ResponseVo<List<ReportTaskVo>> todayTasks(@ApiParam("日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM" +
            "-dd") LocalDate date, @ApiParam("设备uuid") String deviceUuid) {
        //拿到包含此设备的今日任务
        List<SignInTask> signInTasks = thirdTaskService.todayTasks(date, deviceUuid);

        //保留有效任务
        List<ReportTaskVo> list = signInTasks.stream().filter(signInTask -> {
                    return StringUtils.hasText(signInTask.getDeviceUuids()) && StringUtils.hasText(signInTask.getUserUuids());
                })
                //是否需要自动弹出
                .map(signInTask -> {
                    ReportTaskVo reportTaskVo = new ReportTaskVo();
                    BeanUtils.copyProperties(signInTask, reportTaskVo);
                    MoreConfig moreConfigs = JSONObject.parseObject(signInTask.getMoreConfig(), MoreConfig.class);
                    if (moreConfigs != null && moreConfigs.getAutoRun()) {
                        reportTaskVo.setPop(true);
                    } else {
                        reportTaskVo.setPop(false);
                    }
                    return reportTaskVo;
                }).collect(Collectors.toList());
        return ResponseVo.success(list);
    }

    @PostMapping("deviceSignIn")
    @ApiOperation("班牌签到")
    public ResponseVo<String> deviceSignIn(@RequestBody DeviceSignVo deviceSignVo) {
        thirdTaskService.deviceSignIn(deviceSignVo);
        return ResponseVo.success();
    }


    @PostMapping("deviceSignIns")
    @ApiOperation("批量签到")
    public ResponseVo<String> deviceSignIn(@RequestBody List<DeviceSignVo> deviceSignVos) {

        for (DeviceSignVo deviceSignVo : deviceSignVos) {
            thirdTaskService.deviceSignIn(deviceSignVo);
        }

        return ResponseVo.success();
    }


    @GetMapping("signInInfo")
    @ApiOperation("人员签到信息")
    public ResponseVo<SignInInfoVo> signInInfo(@ApiParam("机构uuid") String institutionUuid,
                                               @ApiParam("当前时间") @DateTimeFormat(pattern = "yyyy-MM-dd " +
                                                       "HH:mm:ss") LocalDateTime time,
                                               @ApiParam("机构uuid") String deviceUuid) {

        return ResponseVo.success(thirdTaskService.signInInfo(institutionUuid, time, deviceUuid));
    }

}