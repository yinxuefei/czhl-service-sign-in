package com.gdczhl.saas.service.bo.task;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TaskNameVo {

    @ApiModelProperty("名称")
    private String periodName;

    @ApiModelProperty("任务uuid")
    private String uuid;


}
