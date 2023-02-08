package com.gdczhl.saas.pojo.bo.signInTask;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TaskDeviceVo {

    @ApiModelProperty("设备uuid数组")
    List<String> deviceUuids;

    @ApiModelProperty("任务uuid")
    private String uuid;


}
