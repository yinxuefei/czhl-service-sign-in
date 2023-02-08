package com.gdczhl.saas.controller.weapp.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalTime;


@Data
@ApiModel("小程序 任务")
public class DayTaskBo {

    //    @ApiModelProperty("周几")
//    private String weekDay;
//
//    @ApiModelProperty("日期  yyyy年MM月dd日")
//    private String nowDate;
//
//    @ApiModelProperty("签到任务开始时间")
//    private LocalTime taskStartTime;
//
//    @ApiModelProperty("签到任务结束时间")
//    private LocalTime taskEndTime;
//
//    @ApiModelProperty("签到时间段名称")
//    private String taskName;
//
    @ApiModelProperty("应签人次")
    private Integer allUser;
//
//    @ApiModelProperty("已签")
//    private Integer alreadyUser;
//
//    @ApiModelProperty("未签")
//    private Integer reUser;

}


