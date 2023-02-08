package com.gdczhl.saas.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@ApiModel(value = "性别")
public enum JuheDayStatusEnum {

    FESTIVAL(1, "节假日"),
    WORKDAY(2, "工作日");

    @EnumValue
    private final Integer code;

    private final String description;
}
