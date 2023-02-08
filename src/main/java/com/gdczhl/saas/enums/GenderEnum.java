package com.gdczhl.saas.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@ApiModel(value = "性别")
public enum GenderEnum {

    BOY(1, "男"),
    GIRL(2, "女");


    @EnumValue
    private final Integer code;

    private final String description;
}
