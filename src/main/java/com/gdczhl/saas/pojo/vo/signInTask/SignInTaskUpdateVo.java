package com.gdczhl.saas.pojo.vo.signInTask;

import com.gdczhl.saas.pojo.DatePeriod;
import com.gdczhl.saas.pojo.MoreConfig;
import com.gdczhl.saas.pojo.TimePeriod;
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
    private List<Integer> weekDays;

    private List<String> dateDays;
    @ApiModelProperty("签到时段")
    private TimePeriod timePeriod;

    @ApiModelProperty("有效周期")
    private DatePeriod datePeriod;

    @ApiModelProperty("签到方式:0 人脸,1 定位")
    private List<Integer> signInModes;

    @ApiModelProperty("更多设置")
    private MoreConfig moreConfig;

}
