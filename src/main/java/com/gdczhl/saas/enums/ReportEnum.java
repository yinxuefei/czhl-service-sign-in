package com.gdczhl.saas.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ApiModel("任务状态")
public enum ReportEnum {

    ADD(0, "删除人脸"),
    DELETE(1, "添加人脸");

    @EnumValue
    private final Integer code;

    private final String description;
}
