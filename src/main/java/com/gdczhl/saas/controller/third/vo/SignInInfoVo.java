package com.gdczhl.saas.controller.third.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("分页设备信息")
public class SignInInfoVo {

    @ApiModelProperty("任务名称")
    private List<String> taskNames;

    @ApiModelProperty("人员名称")
    private List<String> usernames;
}