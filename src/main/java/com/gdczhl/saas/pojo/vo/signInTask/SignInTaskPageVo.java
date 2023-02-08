package com.gdczhl.saas.pojo.vo.signInTask;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@ApiModel("分页任务")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInTaskPageVo {

    @ApiModelProperty("签到任务uuid")
    private String uuid;

    @ApiModelProperty("签到任务名称")
    private String name;

    @ApiModelProperty("模式:1 日签,7 周循环")
    private Integer pollingMode;

    @ApiModelProperty("签到日 日期格式: (\"yyyy-MM-dd\"),123")
    private String weekDays;

    @ApiModelProperty("签到时段")
    private String timePeriod;

    @ApiModelProperty("有效周期")
    private String datePeriod;

    @ApiModelProperty("状态,进行中为1,已失效0")
    private Integer status;

    @ApiModelProperty("人员")
    private Integer userCount;

    @ApiModelProperty("终端")
    private Integer deviceCount;

    @ApiModelProperty("签到方式:0 人脸,1 定位")
    private List<Integer> signInModes;

    @ApiModelProperty("最后修改人")
    private String editor;

    @ApiModelProperty("最后修改时间")
    private String lastUpdateTime;

    @ApiModelProperty("启用")
    private Boolean isEnable;
}

