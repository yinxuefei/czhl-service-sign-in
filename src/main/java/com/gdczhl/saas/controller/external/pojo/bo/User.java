package com.gdczhl.saas.controller.external.pojo.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gdczhl.saas.enums.GenderEnum;
import lombok.Data;

import java.io.Serializable;

@TableName("user")
@Data
public class User implements Serializable {
    /**
     * 姓名
     */
    @ExcelProperty("姓名")
    private String name;

    /**
     * 性别,0:女,1,男
     */
    @ExcelProperty("性别")
    private GenderEnum gender;

    /**
     * 用户类型
     */
    @ExcelProperty("用户类型")
    private Integer userType;

}