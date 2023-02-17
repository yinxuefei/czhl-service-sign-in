package com.gdczhl.saas.service;

import com.gdczhl.saas.service.bo.statistics.SignStatisticsPageBo;
import com.gdczhl.saas.service.bo.statistics.UserStatisticsCountBo;
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

    /**
     * 任务分页
     * @param startDate
     * @param endDate
     * @param uuid
     * @param pageNo
     * @param pageSize
     * @return
     */

    PageVo<SignStatisticsPageBo> pageBo(LocalDate startDate, LocalDate endDate, String uuid, Integer pageNo, Integer pageSize);


    /**
     * 根据统计uuid获取统计实体
     * @param uuid
     * @return
     */
    SignStatistics getStatisticsByUuid(String uuid);


    /**
     * 统计签到人数详情
     * @param startDate
     * @param endDate
     * @param uuid
     * @return
     */
    UserStatisticsCountBo getUserCountStatistics(LocalDate startDate, LocalDate endDate, String uuid);

    List<SignStatistics> getTodayTasks(LocalDate date, String operatorUuid);

    /**
     * 根据任务uuid和日期,获取当日统计实体
     * @param taskUuid
     * @param date
     * @return
     */
    SignStatistics getStatisticsByTaskUuid(String taskUuid, LocalDate date);

    List<SignStatistics> getStatisticsByTaskUuid(List<String> taskUuids, LocalDate date);

    /**
     * 依据统计uuid更新统计
     * @param statistics
     */
    void updateByUuid(SignStatistics statistics);
}
