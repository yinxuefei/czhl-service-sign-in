package com.gdczhl.saas.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;


@Data
@ApiModel("签到时段")
@NoArgsConstructor
@AllArgsConstructor
public class TimePeriod {

    @ApiModelProperty("小任务名称")
    private String taskName;

    @ApiModelProperty("开始时间 (pattern = HH:mm)")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime taskStartTime;

    @ApiModelProperty("结束时间 (pattern = HH:mm)")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime taskEndTime;

    @ApiModelProperty("推送")
    private Boolean push;


}
