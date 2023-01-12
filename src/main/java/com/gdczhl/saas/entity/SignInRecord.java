package com.gdczhl.saas.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.Date;

import com.gdczhl.saas.enums.SignStatusEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author hkx
 * @since 2023-01-05
 */
@Getter
@Setter
@TableName("sign_in_record")
public class SignInRecord extends BaseEntity{

    /**
     * 任务uuid
     */
    private String signTaskUuid;

    /**
     * 打卡用户uuid
     */
    private String userUuid;

    /**
     * 签到时段名称
     */
    private String taskName;

    /**
     * 签到地点
     */
    private String areaAddress;

    /**
     * 签到照片url
     */
    private String signImageUrl;

    /**
     * 签到状态:0未签,1已签,2补签
     */
    private SignStatusEnum status;

    /**
     * 默认为 0未推送
     */
    private Boolean push;

    /**
     * 签到设备sn
     */
    private String signDeviceSn;

    /**
     * 体感温度
     */
    private Float bodyTemperature;

    private String username;
}
