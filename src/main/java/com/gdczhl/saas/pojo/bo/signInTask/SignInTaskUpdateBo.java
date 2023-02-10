package com.gdczhl.saas.pojo.bo.signInTask;

import com.gdczhl.saas.pojo.MoreConfig;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInTaskUpdateBo {

    private String uuid;

    @ApiModelProperty("签到任务名称")
    private String name;

    @ApiModelProperty("模式")
    private Integer pollingMode;

    @ApiModelProperty("签到日")
    private List<Integer> weekDays;

    private List<String> dateDays;
    // 签到任务开始时间
    private LocalTime taskStartTime;

    //签到任务结束时间
    private LocalTime taskEndTime;

    //签到时间段名称
    private String taskName;

    //有效开始日期
    private LocalDate taskStartDate;

    //有效结束日期
    private LocalDate taskEndDate;

    //签到推送
    private Boolean push;

    //是否过滤节假日
    private Boolean filterFestival;

    //签到方式
    private List<Integer> signInModes;

    //更多设置
    private MoreConfig moreConfig;

    private String institutionUuid;

}