package com.gdczhl.saas.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ApiModel("任务状态")
public enum TaskStatusEnum {

    NOT_STARTED(0, "未开始"),
    UNDERWAY(1, "进行中"),
    STOP(2, "已结束");

    @EnumValue
    private final Integer code;

    private final String description;
}
