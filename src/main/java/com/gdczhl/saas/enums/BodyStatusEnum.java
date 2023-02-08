package com.gdczhl.saas.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ApiModel("任务状态")
public enum BodyStatusEnum {

    NORMAL(0, "体温正常"),
    FEVER(1, "体温过高"),
    NULL(2, "未检测");

    @EnumValue
    private final Integer code;

    private final String description;
}
