package com.gdczhl.saas.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import lombok.Data;


/**
 * <p>
 * 
 * </p>
 *
 * @author hkx
 * @since 2023-01-05
 */
@Data
@TableName("sign_statistics")
public class SignStatistics extends BaseEntity {

    /**
     * 任务开始时间
     */
    private Date taskStartTime;

    /**
     * 任务开始时间
     */
    private Date taskEndTime;

    /**
     * 签到时段名称
     */
    private String taskName;

    /**
     * 应签到人数
     */
    private Integer allSign;

    /**
     * 已签到人数
     */
    private Integer signed;

    /**
     * 补签人数
     */
    private Integer reSign;

    /**
     * 未签人数
     */
    private String notSign;


}
