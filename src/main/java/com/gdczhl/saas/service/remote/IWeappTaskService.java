package com.gdczhl.saas.service.remote;

import com.gdczhl.saas.controller.weapp.vo.task.DayTaskVo;
import com.gdczhl.saas.controller.weapp.vo.task.RecordPageVo;
import com.gdczhl.saas.controller.weapp.vo.task.SignTaskStatusVo;
import com.gdczhl.saas.vo.PageVo;

import java.time.LocalDate;
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

    List<SignTaskStatusVo> weekSignTask(LocalDate startDate, LocalDate endDate, String userUuid);

    List<DayTaskVo> daySignTask(LocalDate date, String operatorUuid);

    PageVo<RecordPageVo> StatisticsPage(LocalDate date, String taskUuid, Integer status, Integer pageNo, Integer pageSize, String username);
}
