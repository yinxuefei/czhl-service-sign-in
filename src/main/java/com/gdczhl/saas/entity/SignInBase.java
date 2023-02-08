package com.gdczhl.saas.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class SignInBase extends BaseEntity {

    //签到任务开始时间
    @ApiModelProperty("签到任务开始时间")
    private LocalTime taskStartTime;

    // 签到任务结束时间
    @ApiModelProperty("签到任务结束时间")
    private LocalTime taskEndTime;

    //签到时间段名称
    @ApiModelProperty("签到时间段名称")
    private String taskName;
}
