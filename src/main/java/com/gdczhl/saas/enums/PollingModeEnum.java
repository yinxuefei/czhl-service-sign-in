package com.gdczhl.saas.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
@ApiModel(value = "循环模式")
public enum PollingModeEnum {

    DAY(1,"单日"),
    WEEK(7,"周循环");

    @EnumValue
    private final Integer code;

    private final String description;

    public static PollingModeEnum getByCode(Integer code){
        PollingModeEnum[] modeEnums = PollingModeEnum.values();
        for (PollingModeEnum modeEnum : modeEnums) {
            if (modeEnum.code.equals(code)){
                return modeEnum;
            }
        }
        return null;
    }
}


