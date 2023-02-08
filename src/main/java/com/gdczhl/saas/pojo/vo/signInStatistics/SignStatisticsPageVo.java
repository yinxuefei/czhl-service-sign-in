package com.gdczhl.saas.pojo.vo.signInStatistics;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.gdczhl.saas.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * <p>
 *
 * </p>
 *
 * @author hkx
 * @since 2023-01-05
 */
@Data
@ApiModel("签到统计")
@AllArgsConstructor
@NoArgsConstructor
public class SignStatisticsPageVo {

    @ApiModelProperty("统计uuid")
    private String uuid;

    @ApiModelProperty("签到日期")
    private LocalDate createDate;

    @ApiModelProperty("任务名称")
    private String name;

    @ApiModelProperty("时段名称")
    private String taskName;

    @ApiModelProperty("应签到人数")
    private Integer allSign;

    @ApiModelProperty("已签到人数")
    private Integer signed;

    @ApiModelProperty("补签人数")
    private Integer reSign;

    @ApiModelProperty("未签人数")
    private Integer notSign;

}



