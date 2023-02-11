package com.gdczhl.saas.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdczhl.saas.controller.weapp.bo.vo.RecordPageVo;
import com.gdczhl.saas.controller.weapp.bo.vo.SignTaskStatusVo;
import com.gdczhl.saas.pojo.bo.signInStatistics.SignStatisticsPageBo;
import com.gdczhl.saas.pojo.bo.signInStatistics.UserStatisticsCountBo;
import com.gdczhl.saas.entity.SignStatistics;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gdczhl.saas.vo.PageVo;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hkx
 * @since 2023-01-14
 */
public interface ISignStatisticsService extends IService<SignStatistics> {

    PageVo<SignStatisticsPageBo> pageBo(LocalDate startDate, LocalDate endDate, String uuid, Integer pageNo, Integer pageSize);

    SignStatistics getStatisticsByUuid(String uuid);

    UserStatisticsCountBo getUserCountStatistics(LocalDate startDate, LocalDate endDate, String uuid);

    List<SignStatistics> getTodayTasks(LocalDate date, String operatorUuid);

    SignStatistics getStatisticsByTaskUuid(String taskUuid, LocalDate date);

    void updateByUuid(SignStatistics statistics);
}
