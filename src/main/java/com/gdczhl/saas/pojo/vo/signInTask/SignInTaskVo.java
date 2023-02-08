package com.gdczhl.saas.pojo.vo.signInTask;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@ApiModel("签到任务")
@Data
public class SignInTaskVo extends SignInTaskSaveVo {

    @ApiModelProperty("任务uuid")
    private String uuid;


}






