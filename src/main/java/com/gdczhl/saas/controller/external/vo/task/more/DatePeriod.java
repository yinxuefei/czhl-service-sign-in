package com.gdczhl.saas.controller.external.vo.task.more;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@ApiModel("签到周期")
@NoArgsConstructor
@AllArgsConstructor
public class DatePeriod {

    @ApiModelProperty("开始日期 (pattern = \"yyyy-MM-dd\")")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate taskStartDate;

    @ApiModelProperty("结束日期 (pattern = \"yyyy-MM-dd\")")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate taskEndDate;

    @ApiModelProperty("是否过滤节假日")
    private Boolean filterFestival;


}