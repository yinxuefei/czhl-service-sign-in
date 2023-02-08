package com.gdczhl.saas.service.remote;

import com.gdczhl.saas.controller.weapp.bo.vo.DayTaskVo;
import com.gdczhl.saas.controller.weapp.bo.vo.RecordPageVo;
import com.gdczhl.saas.controller.weapp.bo.vo.SignTaskStatusVo;
import com.gdczhl.saas.entity.SignInTask;
import com.gdczhl.saas.pojo.vo.SignInInfoVo;
import com.gdczhl.saas.pojo.vo.signInTask.DeviceSignVo;
import com.gdczhl.saas.vo.PageVo;

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
public interface IWeappTaskService {

    List<SignTaskStatusVo> weekSignTask(LocalDate startDate, LocalDate endDate,String userUuid);

    List<DayTaskVo> daySignTask(LocalDate date,String operatorUuid);

    PageVo<RecordPageVo> StatisticsPage(LocalDate date, String taskUuid, Integer status, Integer pageNo, Integer pageSize, String username);
}
