package com.gdczhl.saas.schedule;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gdczhl.saas.bo.feign.institution.InstitutionVo;
import com.gdczhl.saas.entity.*;
import com.gdczhl.saas.enums.SignStatusEnum;
import com.gdczhl.saas.controller.external.vo.task.more.MoreConfig;
import com.gdczhl.saas.constant.RedisConstant;
import com.gdczhl.saas.enums.TaskEnableStatusEnum;
import com.gdczhl.saas.service.*;
import com.gdczhl.saas.service.remote.BaseServiceRemote;
import com.gdczhl.saas.service.remote.WechatRemoteService;
import com.gdczhl.saas.service.remote.vo.wechat.OfficialAccountSendVo;
import com.gdczhl.saas.service.remote.vo.wechat.OfficialAccountVo;
import com.gdczhl.saas.utils.JuheUtil;
import com.gdczhl.saas.utils.SignTasks;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class xxlJobTask {

    @Autowired
    private ISignInTaskService signInTaskService;

    @Autowired
    private ISignStatisticsService signStatisticsService;

    @Autowired
    private ISignInRecordService signInRecordService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private IThirdTaskService thirdTaskService;

    @Autowired
    private WechatRemoteService wechatRemoteService;
    @Autowired
    private BaseServiceRemote baseServiceRemote;


    @Value("wechat.templateType")
    private String templateType;

    @XxlJob("statisticsTask")
    public void statisticsTask() {
        LocalDate now = LocalDate.now();
        List<SignInTask> signInTasks = thirdTaskService.todayTasks(now, null);
        //时间过期 前一分钟统计
        for (SignInTask signInTask : signInTasks) {

            if (LocalTime.now().isAfter(signInTask.getTaskStartTime()) && LocalTime.now().isBefore(signInTask.getTaskEndTime())) {
                createTask(signInTask);
            }

            //结束任务在之前一分钟,统计
            if (LocalTime.now().isAfter(signInTask.getTaskEndTime().plusMinutes(1))) {
                String statisticsKey = RedisConstant.STATISTICS_UUID_KEY + signInTask.getUuid();
                String statisticsJson = stringRedisTemplate.opsForValue().get(statisticsKey);
                if (statisticsJson == null) {
                    //漏创建的
                    createTask(signInTask);
                    continue;
                }
                String[] split = statisticsJson.split("&&");
                String statisticsUuid = split[0];
                stringRedisTemplate.opsForValue().set(
                        statisticsKey, statisticsUuid + "&&1", JuheUtil.getDistanceTomorrowSeconds(now),
                        TimeUnit.SECONDS);
                //统计打卡信息
                if (split[1].equals("1")) {
                    //已统计
                    continue;
                }
                List<SignInRecord> signInRecordList = signInRecordService.getListByStatisticsUuid(statisticsUuid);
                HashSet<String> alreadyUser = new HashSet<>();
                HashSet<String> notUser = new HashSet<>();
                for (SignInRecord record : signInRecordList) {
                    if (record.getStatus().equals(SignStatusEnum.NOT_SING)) {
                        notUser.add(record.getUserUuid());
                    }
                    if (record.getStatus().equals(SignStatusEnum.RESIGN)) {
                        alreadyUser.add(record.getUserUuid());
                    }
                    record.setIsEnable(true);
                    signInRecordService.updateByUuid(record);
                }
                SignStatistics statistics = signStatisticsService.getStatisticsByUuid(statisticsUuid);
                statistics.setAlreadyUser(JSONObject.toJSONString(alreadyUser));
                statistics.setNotUser(JSONObject.toJSONString(notUser));
                statistics.setIsEnable(true);

                OfficialAccountVo officialAccountVo = SignTasks.checkHttpResponse(wechatRemoteService.get(signInTask.getInstitutionUuid()));
                //负责人推送
                if (StringUtils.hasText(signInTask.getMoreConfig())) {
                    MoreConfig moreConfig = JSONObject.parseObject(signInTask.getMoreConfig(), MoreConfig.class);
                    //开启推送
                    if (moreConfig.getReportPush().getIsReportPush()) {
                        List<String> userUuids = moreConfig.getReportPush().getPusherUuids();
                        //已绑公众号
                        if (officialAccountVo.isBandMiniapp()) {
                            sendWechatReport(signInTask, statistics, now, officialAccountVo, userUuids);
                        }
                    }
                }
                //体温推送
                String moreConfigJson = signInTask.getMoreConfig();
                if (StringUtils.hasText(moreConfigJson)) {
                    MoreConfig moreConfig = JSONObject.parseObject(moreConfigJson, MoreConfig.class);
                    //开启推送
                    if (moreConfig.getBodyTemperature().getIsPush()) {
                        List<String> pushUuids = moreConfig.getBodyTemperature().getPushUuids();
                        //已绑公众号
                        if (officialAccountVo.isBandMiniapp()) {
                            //TODO:体温推送待开发
                            sendWechatBodyTemperature(statistics, officialAccountVo, signInRecordList, pushUuids);
                        }
                    }
                }
                signStatisticsService.updateByUuid(statistics);
            }
        }
    }

    private void sendWechatBodyTemperature(SignStatistics statistics,
                                           OfficialAccountVo officialAccountVo, List<SignInRecord> signInRecordList, List<String> pushUuids) {
        InstitutionVo institutionVo =
                SignTasks.checkHttpResponse(baseServiceRemote.get(statistics.getInstitutionUuid()));
        //体温正常
        List<String> users = new ArrayList<>();
        //体温异常
        List<String> exceptionUsers = new ArrayList<>();
        //未检测
//        HashSet<String> nullUsers = new HashSet<>();

        for (SignInRecord signInRecord : signInRecordList) {
            if (Objects.isNull(signInRecord.getBodyTemperature())) {
//                nullUsers.add(signInRecord.getUsername());
                continue;
            }
            if (signInRecord.getBodyTemperature() <= 37.3) {
                users.add(signInRecord.getUsername());
                continue;
            }
            if (signInRecord.getBodyTemperature() >= 37.3) {
                exceptionUsers.add(signInRecord.getUsername());
            }
        }

        StringBuilder exceptionUserToString = new StringBuilder("");

        for (int i = 1; i <= exceptionUsers.size(); i++) {
            exceptionUserToString.append(exceptionUsers.get(i - 1));
            if (i <= 5) {
                if (i != exceptionUsers.size()) {
                    exceptionUserToString.append("、");
                }
            } else {
                exceptionUserToString.append("...");
                break;
            }
        }

        OfficialAccountSendVo sendVo = new OfficialAccountSendVo();
        sendVo.setOfficialAccountUuid(officialAccountVo.getUuid());
        sendVo.setTemplateType(templateType);
        sendVo.setUserUuids(pushUuids);
        OfficialAccountSendVo.ParamsBean paramsBean = new OfficialAccountSendVo.ParamsBean();
        paramsBean.setFirst("签到体温异常提醒");
        paramsBean.setKeyword1(String.valueOf(users.size()));
        paramsBean.setKeyword2(String.valueOf(exceptionUsers.size()));
        paramsBean.setKeyword3(exceptionUserToString.toString());
        paramsBean.setRemark(institutionVo.getName());
        sendVo.setParams(paramsBean);
        wechatRemoteService.sendListByUser(sendVo);

    }


    private void sendWechatReport(SignInTask signInTask, SignStatistics statistics, LocalDate now,
                                  OfficialAccountVo officialAccountVo, List<String> userUuids) {
        List<String> allUsers = parseJsonToList(statistics.getAllUser());
        List<String> alreadyUsers = parseJsonToList(statistics.getAlreadyUser());
        List<String> notUsers = parseJsonToList(statistics.getNotUser());
        OfficialAccountSendVo sendVo = new OfficialAccountSendVo();
        sendVo.setOfficialAccountUuid(officialAccountVo.getUuid());
        sendVo.setTemplateType(templateType);
        sendVo.setUserUuids(userUuids);
        OfficialAccountSendVo.ParamsBean paramsBean = new OfficialAccountSendVo.ParamsBean();
        paramsBean.setFirst(String.format("您好,【%s】已结束,详情情况如下", signInTask.getName()));
        paramsBean.setKeyword1(now.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
        paramsBean.setKeyword2(String.format("应签: %s人,已完成: %s人,未完成: %s人", allUsers.size(), alreadyUsers.size(),
                notUsers.size()));
        paramsBean.setRemark("祝您工作顺利");
        sendVo.setParams(paramsBean);
        wechatRemoteService.sendListByUser(sendVo);
    }

    @XxlJob("autoEndTask")
    public void autoEndTask() {
        //获取所有启用,并合法的项目(未设置用户,未设置设备)
        //模拟小程序端获取数据
        LambdaQueryWrapper<SignInTask> signs = new LambdaQueryWrapper<>();
        signs.in(SignInTask::getIsEnable, Arrays.asList(TaskEnableStatusEnum.ENABLE,
                TaskEnableStatusEnum.AUTO_CLOSE));
        List<SignInTask> signInTasks = signInTaskService.list(signs);
        for (SignInTask signInTask : signInTasks) {
            if (LocalDate.now().isAfter(signInTask.getTaskStartDate()) && LocalDate.now().isBefore(signInTask.getTaskEndDate()))  {
                if (signInTask.getStatus()){
                    continue;
                }
                signInTask.setStatus(true);
                signInTask.setIsEnable(TaskEnableStatusEnum.ENABLE);
                signInTaskService.updateById(signInTask);
            }else if (signInTask.getTaskStartDate().isEqual(LocalDate.now())){
                if (signInTask.getStatus()){
                    continue;
                }
                signInTask.setStatus(true);
                signInTask.setIsEnable(TaskEnableStatusEnum.ENABLE);
                signInTaskService.updateById(signInTask);
            }else {
                if (!signInTask.getStatus()){
                    continue;
                }
                signInTask.setStatus(false);
                signInTask.setIsEnable(TaskEnableStatusEnum.AUTO_CLOSE);
                signInTaskService.updateById(signInTask);
            }
        }

    }


    public void createTask(SignInTask signInTask) {
        //获取所有启用,并合法的项目(未设置用户,未设置设备)
        //模拟小程序端获取数据
        thirdTaskService.getSignStatisticsUUid(signInTask, LocalDate.now());
    }

    private static List<String> parseJsonToList(String json) {
        if (StringUtils.isEmpty(json)) {
            return new ArrayList<>();
        }
        return JSONObject.parseArray(json, String.class);
    }
}
