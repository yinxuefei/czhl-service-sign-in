package com.gdczhl.saas.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author hkx
 * @since 2023-01-14
 */
@Getter
@Setter
@TableName("sign_statistics")
public class SignStatistics extends SignInBase {

    /**
     * 任务uuid
     */
    private String taskUuid;

    /**
     * 应签用户
     */
    private String allUser;

    /**
     * 已签用户
     */
    private String alreadyUser;

    /**
     * 补签用户
     */
    private String reUser;

    /**
     * 未签用户
     */
    private String notUser;

    /**
     * 请假用户
     */
    private String askLeaveUser;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createDate;

    //开启
    private Boolean isEnable;

    //机构
    private String institutionUuid;

}
