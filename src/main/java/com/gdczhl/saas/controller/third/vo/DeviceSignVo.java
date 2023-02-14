package com.gdczhl.saas.controller.third.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("分页设备信息")
public class DeviceSignVo {

    @ApiModelProperty(value = "设备uuid", required = true)
    private String deviceUuid;

    @ApiModelProperty(value = "用户uuid", required = true)
    private String userUuid;

    @ApiModelProperty(value = "人脸照片url", required = true)
    private String signImageUrl;

    @ApiModelProperty(value = "打卡时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    @ApiModelProperty("体温")
    private Float bodyTemperature;


}