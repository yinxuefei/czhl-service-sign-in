package com.gdczhl.saas.controller.external.vo.statistics;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("用户详情")
public class UserSignStatisticsVo {

    @ApiModelProperty("记录日期")
    private String createDate;

    @ApiModelProperty("姓名")
    private String username;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("签到时间")
    private String createTime;

    @ApiModelProperty("签到地点")
    private String address;
}
