package com.gdczhl.saas.controller.weapp.vo.user;

import com.gdczhl.saas.controller.weapp.vo.task.RecordPageVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalTime;


@Data
@ApiModel("签到详情")
public class UserTaskVo extends RecordPageVo {
    //签到任务开始时间
    @ApiModelProperty("签到任务开始时间")
    private LocalTime taskStartTime;

    // 签到任务结束时间
    @ApiModelProperty("签到任务结束时间")
    private LocalTime taskEndTime;

    //签到时间段名称
    @ApiModelProperty("签到时间段名称")
    private String taskName;

    //签到时间段名称
    @ApiModelProperty("0,未开始,1进行中,2已结束")
    private Integer taskStatus;
}