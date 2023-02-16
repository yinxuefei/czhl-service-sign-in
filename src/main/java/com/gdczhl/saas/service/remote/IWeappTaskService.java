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

    /**
     * 一周内签到情况
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param userUuid 用户uuid
     * @return
     */
    List<SignTaskStatusVo> weekSignTask(LocalDate startDate, LocalDate endDate, String userUuid);

    /**
     * 当前用户拥有的当日任务
     * @param date
     * @param operatorUuid
     * @return
     */
    List<DayTaskVo> daySignTask(LocalDate date, String operatorUuid);

    /**
     * 获取签到记录分页
     * @param date 日期
     * @param taskUuid 任务uuid
     * @param status
     * @param pageNo
     * @param pageSize
     * @param username
     * @return
     */
    PageVo<RecordPageVo> StatisticsPage(LocalDate date, String taskUuid, Integer status, Integer pageNo, Integer pageSize, String username);
}
