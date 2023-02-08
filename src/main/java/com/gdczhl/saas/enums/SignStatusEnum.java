package com.gdczhl.saas.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
@ApiModel("人员签到状态")
public enum SignStatusEnum {

    NOT_SING(0, "未签"),
    SINGED(1, "已签"),
    RESIGN(2, "补签");

    @EnumValue
    private final Integer code;

    private final String description;

    public static SignStatusEnum getByCode(Integer code) {
        SignStatusEnum[] modeEnums = SignStatusEnum.values();
        for (SignStatusEnum modeEnum : modeEnums) {
            if (modeEnum.code.equals(code)) {
                return modeEnum;
            }
        }
        return null;
    }
}