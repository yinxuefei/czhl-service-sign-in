package com.gdczhl.saas.pojo.bo.signInTask;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Data
public class TaskUserVo {

    @ApiModelProperty("用户uuid数组")
    List<String> userUuids;

    @ApiModelProperty("任务uuid")
    private String uuid;

}
