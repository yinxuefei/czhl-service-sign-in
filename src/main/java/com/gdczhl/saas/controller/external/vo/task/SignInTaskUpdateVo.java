package com.gdczhl.saas.controller.external.vo.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gdczhl.saas.controller.external.vo.task.more.DatePeriod;
import com.gdczhl.saas.controller.external.vo.task.more.MoreConfig;
import com.gdczhl.saas.controller.external.vo.task.more.TimePeriod;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@ApiModel("更新任务")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInTaskUpdateVo {

    private String uuid;

    @ApiModelProperty("签到任务名称")
    private String name;

    @ApiModelProperty("模式:1 日签,7 周循环")
    private Integer pollingMode;

    @ApiModelProperty("签到日")
    @JsonFormat
    private List<Integer> weekDays;

    private List<String> dateDays;
    @ApiModelProperty("签到时段")
    private TimePeriod timePeriod;

    @ApiModelProperty("有效周期")
    private DatePeriod datePeriod;

    @ApiModelProperty("签到方式:0 人脸,1 定位")
    @JsonFormat
    private List<Integer> signInModes;

    @ApiModelProperty("更多设置")
    private MoreConfig moreConfig;

}
