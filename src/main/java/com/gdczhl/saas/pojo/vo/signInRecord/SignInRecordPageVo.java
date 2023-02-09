package com.gdczhl.saas.pojo.vo.signInRecord;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("签到流水")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInRecordPageVo {

    @ApiModelProperty("流水uuid")
    private String uuid;

    @ApiModelProperty("打卡日期")
    private String createDate;

    @ApiModelProperty("人员")
    private String username;

    @ApiModelProperty("签到时段")
    private String periodName;

    @ApiModelProperty("签到时间")
    private String createTime;

    @ApiModelProperty("体温检测")
    private Float bodyTemperature;

    @ApiModelProperty("体温状态")
    private Integer bodyStatus;

    @ApiModelProperty("签到地点")
    private String areaAddress;

    @ApiModelProperty("抓拍照片")
    private String signImageUrl;

    @ApiModelProperty("推送结果")
    private Boolean push;
}
