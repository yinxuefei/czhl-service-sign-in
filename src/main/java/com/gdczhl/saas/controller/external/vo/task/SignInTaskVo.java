package com.gdczhl.saas.controller.external.vo.task;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel("签到任务")
@Data
public class SignInTaskVo extends SignInTaskSaveVo {

    @ApiModelProperty("任务uuid")
    private String uuid;


}






