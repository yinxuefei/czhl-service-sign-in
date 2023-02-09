package com.gdczhl.saas.controller.external;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdczhl.saas.entity.SignInTask;
import com.gdczhl.saas.pojo.bo.signInStatistics.SignStatisticsPageBo;
import com.gdczhl.saas.pojo.bo.signInStatistics.UserStatisticsCountBo;
import com.gdczhl.saas.pojo.vo.signInStatistics.SignStatisticsPageVo;
import com.gdczhl.saas.pojo.vo.signInStatistics.UserSignStatisticsVo;
import com.gdczhl.saas.pojo.vo.signInStatistics.UserStatisticsCountVo;
import com.gdczhl.saas.entity.SignInRecord;
import com.gdczhl.saas.service.ISignInRecordService;
import com.gdczhl.saas.service.ISignInTaskService;
import com.gdczhl.saas.service.ISignStatisticsService;
import com.gdczhl.saas.utils.CzBeanUtils;
import com.gdczhl.saas.utils.SignTasks;
import com.gdczhl.saas.vo.PageVo;
import com.gdczhl.saas.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hkx
 * @since 2023-01-14
 */
@RestController
@RequestMapping("external/signStatistics")
@Api(tags = "签到任务统计")
public class SignStatisticsController {

    @Autowired
    private ISignStatisticsService signStatisticsService;

    @Autowired
    private ISignInTaskService signInTaskService;

    @Autowired
    private ISignInRecordService signInRecordService;


    @GetMapping("page")
    @ApiOperation("统计分页")
    public ResponseVo<PageVo<SignStatisticsPageVo>> page(@ApiParam("开始日期") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                         @ApiParam("结束日期") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                         @ApiParam("任务uuid") String taskUuid,
                                                         @ApiParam("pageNo") @RequestParam(defaultValue = "1") Integer pageNo,
                                                         @ApiParam("pageSize") @RequestParam(defaultValue = "20") Integer pageSize) {

        PageVo<SignStatisticsPageVo> result = new PageVo<>();
        PageVo<SignStatisticsPageBo> pageBo = signStatisticsService.pageBo(startDate, endDate, taskUuid, pageNo, pageSize);

        List<SignStatisticsPageBo> records = pageBo.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            BeanUtils.copyProperties(pageBo, result);
            return ResponseVo.success(result);
        }
        List<SignStatisticsPageVo> recordVos = records.stream().map(bo -> {
            SignStatisticsPageVo vo = new SignStatisticsPageVo();
            BeanUtils.copyProperties(bo, vo);
            vo.setCreateDate(bo.getCreateTime().toLocalDate());
            vo.setAllSign(bo.getAllUser().size());
            vo.setReSign(bo.getReUser().size());
            vo.setNotSign(bo.getNotUser().size());
            vo.setSigned(bo.getAlreadyUser().size());
            String uuid = bo.getTaskUuid();
            SignInTask signInTask = signInTaskService.getTaskByUuid(uuid);
            vo.setName(signInTask.getName());
            vo.setPeriodName(SignTasks.getTaskNameResult(signInTask));
            return vo;
        }).collect(Collectors.toList());

        BeanUtils.copyProperties(pageBo,result);
        result.setRecords(recordVos);
        return ResponseVo.success(result);
    }

    @GetMapping("getUserCountStatistics")
    @ApiOperation("签到总数统计")
    public ResponseVo<UserStatisticsCountVo> getUserCountStatistics(@ApiParam("开始日期") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                    @ApiParam("结束日期") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                                    @ApiParam("任务uuid") String uuid) {
        UserStatisticsCountBo bo = signStatisticsService.getUserCountStatistics(startDate, endDate, uuid);
        UserStatisticsCountVo result = CzBeanUtils.copyProperties(bo, UserStatisticsCountVo::new);
        return ResponseVo.success(result);
    }

    @GetMapping("getUserSignStatistics")
    @ApiOperation("签到用户详情")
    public ResponseVo<PageVo<UserSignStatisticsVo>> getUserSignStatistics(@ApiParam("签到状态 0未,1已,2补") Integer status,
                                                                          @ApiParam("人员名称") String name,
                                                                          @ApiParam("统计uuid") String uuid,
                                                                          @ApiParam("pageNo") @RequestParam(defaultValue = "1") Integer pageNo,
                                                                          @ApiParam("pageSize") @RequestParam(defaultValue =
                                                                                  "20") Integer pageSize,
                                                                          @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                                          @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                          @ApiParam("任务uuid") @RequestParam(defaultValue =
                                                                                  "20") String taskUuid
    ) {
        Page<SignInRecord> signInRecordList = signInRecordService.getUserSignStatistics(status, name, uuid, pageNo,
                pageSize, startDate, endDate, taskUuid);
        List<SignInRecord> records = signInRecordList.getRecords();
        PageVo<UserSignStatisticsVo> result = new PageVo<>();
        if (CollectionUtils.isEmpty(records)) {
            BeanUtils.copyProperties(signInRecordList, result);
            return ResponseVo.success(result);
        }

        List<UserSignStatisticsVo> recordVos = records.stream().map(signInRecord -> {
            UserSignStatisticsVo vo = new UserSignStatisticsVo();
            BeanUtils.copyProperties(signInRecord, vo);
            vo.setCreateDate(signInRecord.getCreateTime().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy.MM" +
                    ".dd")));
            vo.setCreateTime(signInRecord.getCreateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            vo.setStatus(signInRecord.getStatus().getCode());
            return vo;
        }).collect(Collectors.toList());
        BeanUtils.copyProperties(signInRecordList,result);
        result.setRecords(recordVos);
        return ResponseVo.success(result);
    }


}
