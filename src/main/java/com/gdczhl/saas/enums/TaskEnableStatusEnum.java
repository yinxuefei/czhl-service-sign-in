package com.gdczhl.saas.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ApiModel("任务状态")
public enum TaskEnableStatusEnum {

    AUTO_CLOSE(0, "自动禁用"),
    CLOSE(1, "手动禁用"),
    ENABLE(2, "启用");

    @EnumValue
    private final Integer code;

    private final String description;

}
