package com.gdczhl.saas.controller.weapp.bo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("打卡记录分页vo")
public class RecordPageVo {

    @ApiModelProperty("签到时间 HH:mm:ss")
    private String signTime;

    @ApiModelProperty("体温")
    private Float temperature;

    @ApiModelProperty("体温状态 0 正常,1 发热")
    private Integer temperatureStatus;

    @ApiModelProperty("签到状态 0 未,1 已,2 补")
    private Integer signStatus;

    @ApiModelProperty("补签人名")
    private String reSignUsername;

    @ApiModelProperty("打卡用户名称  补签未做")
    private String username;

    @ApiModelProperty("签到照片url")
    private String picture;

}
