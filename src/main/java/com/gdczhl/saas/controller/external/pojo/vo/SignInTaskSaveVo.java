package com.gdczhl.saas.controller.external.pojo.vo;

import com.gdczhl.saas.controller.external.pojo.MoreConfig;
import com.gdczhl.saas.controller.external.pojo.TimePeriod;
import com.gdczhl.saas.controller.external.pojo.DatePeriod;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@ApiModel("签到任务")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInTaskSaveVo {

    @ApiModelProperty("签到任务uuid")
    private String uuid;

    @ApiModelProperty("签到任务名称")
    private String name;

    @ApiModelProperty("模式:1 日签,7 周循环")
    private Integer pollingMode;

    @ApiModelProperty("签到日 日期格式: (\"yyyy-MM-dd\"),星期格式: (周几)")
    private List<String> weekDays;

    @ApiModelProperty("签到时段")
    private TimePeriod timePeriod;

    @ApiModelProperty("有效周期")
    private DatePeriod datePeriod;

    @ApiModelProperty("签到方式:0 人脸,1 定位")
    private List<Integer> signInMode;

    @ApiModelProperty("更多设置")
    private MoreConfig moreConfig;

     @ApiModelProperty("启用:true")
    private Boolean status;
}






