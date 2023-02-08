package com.gdczhl.saas.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ApiModel("签到状态")
public enum WeekEnum {
    MONDAY(1, "周一"),
    TUESDAY(2, "周二"),
    RESIGN(3, "周三"),
    THURSDAY(4, "周四"),
    FRIDAY(5, "周五"),
    SATURDAY(6, "周六"),
    SUNDAY(7, "周日"),
    EVERYDAY(8, "每一天");

    @EnumValue
    private final Integer code;

    private final String description;

    public static Integer getCode(String description) {
        WeekEnum[] weekEnums = WeekEnum.values();
        for (WeekEnum weekEnum : weekEnums) {
            if (weekEnum.getDescription().equals(description)) {
                return weekEnum.getCode();
            }
        }
        throw new RuntimeException("状态码不存在");
    }

    public static String getDescription(Integer code) {
        WeekEnum[] weekEnums = WeekEnum.values();
        for (WeekEnum weekEnum : weekEnums) {
            if (weekEnum.getCode().equals(code)) {
                return weekEnum.getDescription();
            }
        }
        throw new RuntimeException("状态码不存在");
    }
}