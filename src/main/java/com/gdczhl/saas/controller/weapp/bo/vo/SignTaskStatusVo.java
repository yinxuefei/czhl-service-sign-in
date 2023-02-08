package com.gdczhl.saas.controller.weapp.bo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;


@Data
@ApiModel("签到状态")
public class SignTaskStatusVo {

    @ApiModelProperty("yyyy-MM-dd")
    private String nowDate;

    @ApiModelProperty("0,无异常,1,有异常,2,无任务")
    private Integer isSignIn;

}
