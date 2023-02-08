package com.gdczhl.saas.pojo.bo.signInTask;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TaskUserPageVo {

    @ApiModelProperty("用户类型")
    List<Integer> userTypes;

    @ApiModelProperty("用户uuid")
    private String uuid;

    @ApiModelProperty("用户名")
    private String name;

}
