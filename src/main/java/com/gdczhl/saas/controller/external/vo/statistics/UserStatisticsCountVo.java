package com.gdczhl.saas.controller.external.vo.statistics;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("用户统计")
public class UserStatisticsCountVo {

    @ApiModelProperty("应签到人次")
    private Integer allSign;

    @ApiModelProperty("已签到人次")
    private Integer signed;

    @ApiModelProperty("补签人次")
    private Integer reSign;

    @ApiModelProperty("未签人次")
    private Integer notSign;

    @ApiModelProperty("合计人数")
    private Integer countSign;
}
