package com.gdczhl.saas.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdczhl.saas.pojo.vo.signInRecord.SignInRecordPageVo;
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

    PageVo<SignInRecordPageVo> getSignRecordPage(LocalDate startDate, LocalDate endDate, String taskUuid, String name,
                                                 String areaCode,
                                                 Integer pageNo,
                                                 Integer pageSize);

    /**
     * @param
     * @param status
     * @param name
     * @param pageNo
     * @param pageSize
     * @return
     */
    Page<SignInRecord> getUserSignStatistics(Integer status, String name, Integer pageNo,
                                             Integer pageSize, LocalDate startDate, LocalDate endDate,
                                             String taskUuid,String uuid);


    SignInRecord getByStatisticsUserUuid(String statisticsUuid, String userUuid);

    void deleteByTaskUuid(String taskUuid);

    List<SignInRecord> getListByStatisticsUuid(String signStatisticsUuid);


    Page<SignInRecord> getPageByStatisticsUuid(String uuid, Integer pageNo, Integer pageSize, Integer status,
                                               String username);


    SignInRecord getByUuid(String recordUuid);

    SignInRecord getTaskByDateUuid(LocalDate date,LocalTime taskStartTime, LocalTime taskEndTime, String uuid,
                                         String operatorUuid);
    void updateByUuid(SignInRecord record);

    List<SignInRecord> getByStatisticsUuid(String statisticsUuid);
}
