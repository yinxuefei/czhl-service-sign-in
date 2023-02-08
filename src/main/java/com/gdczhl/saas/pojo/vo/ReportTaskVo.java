package com.gdczhl.saas.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalTime;

@Data
public class ReportTaskVo {

    @ApiModelProperty("签到时间段名称")
    private String taskName;

    @ApiModelProperty("任务uuid")
    private String uuid;

    @ApiModelProperty("任务开始时间")
    private LocalTime taskStartTime;

    @ApiModelProperty("任务结束时间")
    private LocalTime taskEndTime;

    @ApiModelProperty("弹窗")
    private Boolean pop;

}
