package com.gdczhl.saas.service;

import com.gdczhl.saas.entity.SignInTask;
import com.gdczhl.saas.pojo.vo.SignInInfoVo;
import com.gdczhl.saas.pojo.vo.signInTask.DeviceSignVo;

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
public interface IThirdTaskService {

    List<SignInTask> todayTasks(LocalDate date,String deviceUuid);

    void deviceSignIn(DeviceSignVo deviceSignVo);

    SignInInfoVo signInInfo(String uuid, LocalDateTime time,String deviceUuid );
}
