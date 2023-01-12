package com.gdczhl.saas.controller.external.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("分页设备信息")
public class DevicePageVo {
    @ApiModelProperty("设备uuid")
    private String uuid;
    @ApiModelProperty("设备名称")
    private String name;
    @ApiModelProperty("设备编号")
    private String number;
    @ApiModelProperty("所在场地")
    private String areaAddress;
}