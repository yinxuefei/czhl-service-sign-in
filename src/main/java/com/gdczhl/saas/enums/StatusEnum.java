package com.gdczhl.saas.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ApiModel("任务状态")
public enum StatusEnum {

    EXPIRED(0, "已失效"),
    ACTIVATE(1, "进行中");

    @EnumValue
    private final Integer code;

    private final String description;
}
