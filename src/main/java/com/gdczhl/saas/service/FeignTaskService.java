package com.gdczhl.saas.service;

import com.gdczhl.saas.controller.feign.vo.DeviceSignVo;
import com.gdczhl.saas.controller.feign.vo.SignInInfoVo;
import com.gdczhl.saas.entity.SignInTask;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hkx
 * @since 2023-01-06
 */
public interface FeignTaskService {


    /**
     *获取今日任务
     * @param date
     * @param deviceUuid
     * @return
     */
    List<SignInTask> todayTasks(LocalDate date, String deviceUuid);


    /**
     * 设备签到
     * @param deviceSignVo
     */
    void deviceSignIn(DeviceSignVo deviceSignVo);

    /**
     * 签到详情
     * @param uuid
     * @param time
     * @param deviceUuid
     * @return
     */
    SignInInfoVo signInInfo(String uuid, LocalDateTime time, String deviceUuid);

    /**
     * 获取统计uuid
     * @param signInTask
     * @param now
     * @return
     */
    public String getSignStatisticsUUid(SignInTask signInTask, LocalDate now);
}
