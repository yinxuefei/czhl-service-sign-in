package com.gdczhl.saas.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.gdczhl.saas.enums.PollingModeEnum;
import com.gdczhl.saas.enums.SignInModeEnum;
import com.gdczhl.saas.enums.TaskEnableStatusEnum;
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
@TableName("sign_in_task")
public class SignInTask extends SignInBase {

    //签到任务名称
    private String name;

    //所有待签到人员
    private String userUuids;

    //循环模式
    private PollingModeEnum pollingMode;

    //打卡模式
    private String signInMode;

    //签到日
    private String week;


    /**
     * 有效开始日期
     */
    private LocalDate taskStartDate;

    //有效结束日期

    private LocalDate taskEndDate;

    //签到推送
    private Boolean push;

    //终端
    private String deviceUuids;

    //状态,默认为false
    private Boolean status;

    //过滤节假日
    private Boolean filterFestival;

    //终端sn
    private String deviceSns;

    //开启
    private TaskEnableStatusEnum isEnable;

    //更多设置
    private String moreConfig;

    //机构uuid
    private String institutionUuid;
}
