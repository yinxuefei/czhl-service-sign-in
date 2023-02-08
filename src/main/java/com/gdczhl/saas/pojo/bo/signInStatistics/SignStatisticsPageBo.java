package com.gdczhl.saas.pojo.bo.signInStatistics;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignStatisticsPageBo {

    @ApiModelProperty("统计uuid")
    private String uuid;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("任务开始时间")
    private LocalTime taskStartTime;

    @ApiModelProperty("任务结束时间")
    private LocalTime taskEndTime;

    @ApiModelProperty("签到时段名称")
    private String taskName;

    @ApiModelProperty("任务uuid")
    private String taskUuid;

    @ApiModelProperty("应签用户")
    private List<String> allUser;

    @ApiModelProperty("已签用户")
    private List<String> alreadyUser;

    @ApiModelProperty("已签用户")
    private List<String> reUser;

    @ApiModelProperty("已签用户")
    private List<String> notUser;


}