package com.gdczhl.saas.pojo.bo.signInStatistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserStatisticsCountBo {

    @ApiModelProperty("应签到人数")
    private Integer allSign;

    @ApiModelProperty("已签到人数")
    private Integer signed;

    @ApiModelProperty("补签人数")
    private Integer reSign;

    @ApiModelProperty("未签人数")
    private Integer notSign;

    @ApiModelProperty("合计人数")
    private Integer countSign;
}
