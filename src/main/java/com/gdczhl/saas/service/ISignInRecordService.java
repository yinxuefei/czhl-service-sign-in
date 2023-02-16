package com.gdczhl.saas.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdczhl.saas.controller.external.vo.record.SignInRecordPageVo;
import com.gdczhl.saas.entity.SignInRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gdczhl.saas.vo.PageVo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hkx
 * @since 2023-01-06
 */
public interface ISignInRecordService extends IService<SignInRecord> {

    /**
     * 获取分页vo
     * @return
     */
    PageVo<SignInRecordPageVo> getSignRecordPage(LocalDate startDate, LocalDate endDate, String taskUuid, String name,
                                                 String areaCode,
                                                 Integer pageNo,
                                                 Integer pageSize);

    /**
     * 获取记录分页
     * @param status 1有效 0无效
     * @param name 任务名称
     * @param pageNo
     * @param pageSize
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param taskUuid 任务uuid
     * @param uuid 统计uuid
     * @return
     */
    Page<SignInRecord> getUserSignStatistics(Integer status, String name, Integer pageNo,
                                             Integer pageSize, LocalDate startDate, LocalDate endDate,
                                             String taskUuid, String uuid);


    SignInRecord getByStatisticsUserUuid(String statisticsUuid, String userUuid);

    /**
     * 根据任务uuid删除
     * @param taskUuid
     */
    void deleteByTaskUuid(String taskUuid);

    /**
     * 根据统计uuid获取统计记录
     * @param signStatisticsUuid
     * @return
     */
    List<SignInRecord> getListByStatisticsUuid(String signStatisticsUuid);


    Page<SignInRecord> getPageByStatisticsUuid(String uuid, Integer pageNo, Integer pageSize, Integer status,
                                               String username);

    /**
     * 获取打卡记录
     * @param recordUuid 打卡记录uuid
     * @return
     */
    SignInRecord getByUuid(String recordUuid);

    /**
     * 根据日期uuid获取多个任务
     * @param date
     * @param taskStartTime
     * @param taskEndTime
     * @param uuid
     * @param operatorUuid
     * @return
     */
    SignInRecord getTaskByDateUuid(LocalDate date, LocalTime taskStartTime, LocalTime taskEndTime, String uuid,
                                   String operatorUuid);

    /**
     * 更新任务
     * @param record
     */
    void updateByUuid(SignInRecord record);

    /**
     * 统计记录
     * @param statisticsUuid
     * @return
     */
    List<SignInRecord> getByStatisticsUuid(String statisticsUuid);
}
