package com.gdczhl.saas.controller.weapp.bo.vo;

import com.gdczhl.saas.entity.BaseEntity;
import com.gdczhl.saas.entity.SignInBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;


@Data
@ApiModel("当日单个任务")
public class DayTaskVo extends SignInVoBase {

    @ApiModelProperty("任务uuid")
    private String uuid;

    @ApiModelProperty("已签人次")
    private Integer SignInCount;

    @ApiModelProperty("应签人次")
    private Integer AllSignInCount;

    @ApiModelProperty("未签人次")
    private Integer NotSignInCount;

    @ApiModelProperty("0,无异常,1,有异常,2,无任务")
    private Integer isSignIn;


}
