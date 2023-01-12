package com.gdczhl.saas.controller.external.pojo.vo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@ApiModel("签到任务")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInTaskPageVo {

    @ApiModelProperty("签到任务uuid")
    private String uuid;

    @ApiModelProperty("签到任务名称")
    private String name;

    @ApiModelProperty("模式:1 日签,7 周循环")
    private String pollingMode;

    @ApiModelProperty("签到日 日期格式: (\"yyyy-MM-dd\"),星期格式: (周几)")
    private String weekDays;

    @ApiModelProperty("签到时段")
    private String timePeriod;

    @ApiModelProperty("有效周期")
    private String datePeriod;

    @ApiModelProperty("人员")
    private Integer userUuidList;

    @ApiModelProperty("终端")
    private Integer deviceUuidList;

    @ApiModelProperty("签到方式:0 人脸,1 定位")
    private String signInMode;

    @ApiModelProperty("最后修改人")
    private String editor;

    @ApiModelProperty("最后修改时间")
    private String lastUpdateTime;

    @ApiModelProperty("启用:true")
    private Boolean status;
}

