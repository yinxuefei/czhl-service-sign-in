package com.gdczhl.saas.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ApiModel("签到状态")
public enum WeekEnum{
    SUNDAY(6,"周日"),
    MONDAY(7,"周一"),
    TUESDAY(1,"周二"),
    RESIGN(2,"周三"),
    THURSDAY(3,"周四"),
    FRIDAY(4,"周五"),
    SATURDAY(5,"周六"),
    EVERYDAY(8,"每一天");


    @EnumValue
    private final Integer code;

    private final String description;

    public static Integer getCode(String description){
        WeekEnum[] weekEnums = WeekEnum.values();
        for (WeekEnum weekEnum : weekEnums) {
            if (weekEnum.getDescription().equals(description)){
                return weekEnum.getCode();
            }
        }
        return null;
    }

    public static String getDescription(Integer code){
        WeekEnum[] weekEnums = WeekEnum.values();
        for (WeekEnum weekEnum : weekEnums) {
            if (weekEnum.getCode().equals(code)){
                return weekEnum.getDescription();
            }
        }
        return null;
    }
}