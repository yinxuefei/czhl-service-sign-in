package com.gdczhl.saas.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "`uuid`", fill = FieldFill.INSERT)
    private String uuid;

    @Version
    @TableField(value = "`version`", fill = FieldFill.INSERT)
    private Integer version;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @TableField(fill = FieldFill.UPDATE)
    private String editor;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

}
