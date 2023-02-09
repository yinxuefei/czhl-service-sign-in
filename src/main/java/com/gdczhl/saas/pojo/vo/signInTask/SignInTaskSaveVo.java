package com.gdczhl.saas.pojo.vo.signInTask;

import com.gdczhl.saas.pojo.MoreConfig;
import com.gdczhl.saas.pojo.TimePeriod;
import com.gdczhl.saas.pojo.DatePeriod;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@ApiModel("保存任务")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInTaskSaveVo {

    @ApiModelProperty("签到任务名称")
    private String name;

    @ApiModelProperty("模式:1 日签,7 周循环")
    private Integer pollingMode;

    @ApiModelProperty("星期 :1,2,3...")
    private List<Integer> weekDays;

    @ApiModelProperty("日期 :")
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






