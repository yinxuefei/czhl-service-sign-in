package com.gdczhl.saas.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@ApiModel(value = "打卡模式")
public enum SignInModeEnum {

    FACE(0,"人脸打卡"),
    ADDRESS(1,"定位打卡");

    @EnumValue
    private final Integer code;

    private final String description;

    public static SignInModeEnum getByCode(Integer code){
        SignInModeEnum[] modeEnums = SignInModeEnum.values();
        for (SignInModeEnum modeEnum : modeEnums) {
            if (modeEnum.code.equals(code)){
                return modeEnum;
            }
        }
        return null;
    }
}