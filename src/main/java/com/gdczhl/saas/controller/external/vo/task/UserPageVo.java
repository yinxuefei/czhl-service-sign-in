package com.gdczhl.saas.controller.external.vo.task;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("分页用户信息")
public class UserPageVo {

    @ApiModelProperty("用户uuid")
    private String uuid;

    @ApiModelProperty("用户名称")
    private String name;

    @ApiModelProperty("组织uuid")
    private String organizationUuid;

    @ApiModelProperty("架构名")
    private String organizationName;


}
