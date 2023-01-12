package com.gdczhl.saas.controller.weapp;

import com.gdczhl.saas.service.ISignInTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Api(tags = "执行任务")
@Slf4j
@RestController
@RequestMapping("external/task")
public class TaskController {

    @Autowired
    private ISignInTaskService signInTaskService;

    @GetMapping("testSign")
    @ApiOperation("后端测试-测试班牌打卡")
    public void runTask(@ApiParam("选择日期") LocalDate date,
                        @ApiParam("选择任务名称") String name) {


    }



}