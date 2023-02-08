package com.gdczhl.saas.controller.weapp.bo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalTime;


@Data
@ApiModel("签到状态")
public class UserStatusVo {

    @ApiModelProperty("用户姓名")
    private String username;

    @ApiModelProperty("签到照片url")
    private String picture;

    @ApiModelProperty("签到状态 0未,1已,2补,3未开始")
    private Integer status;

    @ApiModelProperty("打卡时间")
    private String signTime;

//    @ApiModelProperty("流水uuid")
//    private String RecordUuid;

}