package com.gdczhl.saas.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdczhl.saas.bo.feign.area.AreaBriefInfoVo;
import com.gdczhl.saas.mq.messages.areaPlus.UserDeviceFlagSaveListBo;
import com.gdczhl.saas.service.remote.AreaRemoteService;
import com.gdczhl.saas.utils.ContextCache;
import com.gdczhl.saas.interceptor.HeaderInterceptor;
import com.gdczhl.saas.pojo.bo.signInTask.TaskUserPageVo;
import com.gdczhl.saas.pojo.vo.DevicePageVo;
import com.gdczhl.saas.pojo.vo.UserPageVo;
import com.gdczhl.saas.bo.feign.organization.OrganizationVo;
import com.gdczhl.saas.bo.feign.user.UserVo;
import com.gdczhl.saas.pojo.DatePeriod;
import com.gdczhl.saas.pojo.MoreConfig;
import com.gdczhl.saas.pojo.TimePeriod;
import com.gdczhl.saas.pojo.bo.signInTask.SignInTaskSaveBo;
import com.gdczhl.saas.pojo.bo.signInTask.SignInTaskUpdateBo;
import com.gdczhl.saas.pojo.vo.signInTask.SignInTaskPageVo;
import com.gdczhl.saas.pojo.vo.signInTask.SignInTaskVo;
import com.gdczhl.saas.pojo.vo.signInTask.TaskNameVo;
import com.gdczhl.saas.entity.*;
import com.gdczhl.saas.enums.*;
import com.gdczhl.saas.mapper.SignInTaskMapper;
import com.gdczhl.saas.mq.SyncProducer;
import com.gdczhl.saas.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdczhl.saas.service.remote.BaseServiceRemote;
import com.gdczhl.saas.service.remote.IotRemoteService;
import com.gdczhl.saas.utils.*;
import com.gdczhl.saas.vo.PageVo;
import com.gdczhl.saas.vo.ResponseVo;
import com.gdczhl.saas.vo.feign.iot.response.DeviceInfoVo;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hkx
 * @since 2023-01-05
 */
@Service
@Transactional
@Slf4j
public class SignInTaskServiceImpl extends ServiceImpl<SignInTaskMapper, SignInTask> implements ISignInTaskService {

    @Autowired
    private IUserService userService;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private BaseServiceRemote baseServiceRemote;

    @Autowired
    private IotRemoteService iotRemoteService;

    @Autowired
    private SyncProducer syncProducer;

    @Autowired
    private ISignInRecordService signInRecordService;

    @Autowired
    private AreaRemoteService areaRemoteService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public boolean add(SignInTaskSaveBo saveBo) {

        String institutionUuid = getInstitutionUuid();
        saveBo.setInstitutionUuid(institutionUuid);
        SignInTask signInTask = new SignInTask();
        BeanUtils.copyProperties(saveBo, signInTask);
        List<String> weekDays = saveBo.getWeekDays();
        signInTask.setWeek(JSONObject.toJSONString(weekDays));
        MoreConfig moreConfig = saveBo.getMoreConfig();
        signInTask.setMoreConfig(JSONObject.toJSONString(moreConfig));
        signInTask.setPollingMode(PollingModeEnum.getByCode(saveBo.getPollingMode()));
        signInTask.setSignInMode(JSONObject.toJSONString(saveBo.getSignInMode()));

        //添加负责人
        MoreConfig.Manager manager = moreConfig.getManager();
        if (null != manager) {
            if (manager.getIsManager()) {
                List<String> managerUuids = manager.getManagerUuids();
                saveUser(managerUuids);
            }
        }

        //添加汇报推送人
        MoreConfig.ReportPush reportPush = moreConfig.getReportPush();
        if (null != reportPush) {
            if (reportPush.getIsReportPush()) {
                List<String> pusherUuids = reportPush.getPusherUuids();
                saveUser(pusherUuids);
            }
        }

        signInTask.setStatus(!isExpires(signInTask));

        return save(signInTask);

    }

    private static String getInstitutionUuid() {
        return ContextCache.getAttribute(HeaderInterceptor.INSTITUTION_UUID).toString();
    }

    private static Boolean isExpires(SignInTask signInTask) {
        //未失效
        return !LocalDate.now().isAfter(signInTask.getTaskStartDate()) || !LocalDate.now().isBefore(signInTask.getTaskEndDate());
    }

//    private void saveSignStatisticsTask(SignInTask signInTask) {
//        PollingModeEnum pollingMode = signInTask.getPollingMode();
//        List<String> weekDays = JSONObject.parseArray(signInTask.getWeek(), String.class);
//        Boolean filterFestival = signInTask.getFilterFestival();
//        LocalDate taskStartDate = signInTask.getTaskStartDate();
//        LocalDate taskEndDate = signInTask.getTaskEndDate();
//        ArrayList<LocalDate> dateList = Lists.newArrayList();
//        if (pollingMode.equals(PollingModeEnum.DAY)){
//            for (String weekDay : weekDays) {
//                LocalDate date = LocalDate.parse(weekDay, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//                if (filterFestival){
//                    JuheBean vacation = JuheUtil.getVacation(stringRedisTemplate, date);
//                    if (vacation != null && vacation.getResult().getStatus().equals(JuheDayStatusEnum.FESTIVAL.getCode())){
//                        dateList.add(date);
//                    }
//                }else {
//                    dateList.add(date);
//                }
//            }
//        }
//
//        if (pollingMode.equals(PollingModeEnum.WEEK)){
//            List<Integer> weeks = weekDays.stream().map(s -> Integer.valueOf(s)).collect(Collectors.toList());
//            List<LocalDate> dates = TimeUtil.getPeriodDate(taskStartDate,taskEndDate);
//            for (LocalDate date : dates) {
//                if (weeks.contains(date.getDayOfWeek().getValue())){
//                    if (filterFestival){
//                        JuheBean vacation = JuheUtil.getVacation(stringRedisTemplate, date);
//                    if (vacation != null && vacation.getResult().getStatus().equals(JuheDayStatusEnum.FESTIVAL.getCode())){
//                        dateList.add(date);
//                        }
//                    }else {
//                        dateList.add(date);
//                    }
//                }
//            }
//        }
//
//
//        for (LocalDate localDate : dateList) {
//            SignStatistics signStatistics = new SignStatistics();
//            BeanUtils.copyProperties(signInTask,signStatistics);
//            signStatistics.setTaskName(SignTasks.getTaskNameResult(signInTask));
//            signStatistics.setAllUser(signInTask.getUserUuids());
//            signStatistics.setCreateDate(localDate);
//            signStatisticsService.save(signStatistics);
//        }
//
//    }


    public List<TaskNameVo> getTaskNameList(Integer status) {
        List<TaskNameVo> result = new ArrayList<>();
        LambdaQueryWrapper<SignInTask> qw = new LambdaQueryWrapper<SignInTask>()
                .eq(Objects.nonNull(status), SignInTask::getStatus, status)
                .eq(SignInTask::getInstitutionUuid,getInstitutionUuid())
                .orderByDesc(BaseEntity::getCreateTime);
        List<SignInTask> list = list(qw);
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }

        list.forEach(signInTask -> {
            String taskNameResult = SignTasks.getTaskNameResult(signInTask);
            TaskNameVo vo = new TaskNameVo();
            vo.setUuid(signInTask.getUuid());
            vo.setPeriodName(taskNameResult);
            result.add(vo);
        });
        return result;
    }

    @Override
    public boolean setUsers(List<String> userUuids, String uuid) {
        SignInTask signInTask = getTaskByUuid(uuid);
        Assert.notNull(signInTask, "任务未开启");
        log.info("保存user信息到本地库");
        saveUser(userUuids);
        List<String> deviceUuids = new ArrayList<>();

        //查设备
        if (StringUtils.hasText(signInTask.getDeviceUuids())) {
            deviceUuids = JSONObject.parseArray(signInTask.getDeviceUuids()).toJavaList(String.class);
        }

        Set<String> userSet = new HashSet<>();
        if (StringUtils.hasText(signInTask.getUserUuids())) {
            userSet.addAll(JSONObject.parseArray(signInTask.getUserUuids(), String.class));
        }

        userSet.addAll(userUuids);
        signInTask.setUserUuids(JSONObject.toJSONString(userSet));

        if (updateById(signInTask)) {
            //任务,允许人脸签到
            addOrDeleteFaceReport(deviceUuids, signInTask, userUuids,ReportEnum.ADD);
            return true;
        } else {
            return false;
        }
    }

    private void saveUser(List<String> userUuids) {
        if (CollectionUtils.isEmpty(userUuids)){
            return;
        }
        //查本地库
        List<String> nativeUserList = userService.list().stream().map(user -> user.getUuid()).collect(Collectors.toList());
        //求新增 本地未添加用户
        List<String> newUserUuidList = ListUtil.difference(nativeUserList, userUuids);
        ResponseVo<List<UserVo>> responseVoUser = baseServiceRemote.listByUuidsWithGetFacePhoto(newUserUuidList);
        List<UserVo> userVoList = SignTasks.checkHttpResponse(responseVoUser);
        for (UserVo userVo : userVoList) {
            User user = new User();
            ResponseVo<List<OrganizationVo>> ResponseVoOrganization = baseServiceRemote.listOrganizationByUuids(Arrays.asList(userVo.getOrganizationUuid()));
            OrganizationVo organizationVo = SignTasks.checkHttpResponse(ResponseVoOrganization).get(0);
            BeanUtils.copyProperties(userVo, user);
            user.setOrganizationName(organizationVo.getName());
            userService.save(user);
        }
    }


    @Override
    public boolean setDevices(List<String> deviceUuids, String uuid) {
        SignInTask signInTask = getTaskByUuid(uuid);
        Assert.notNull(signInTask, "任务未开启");
        log.info("保存设备信息到本地库");
        saveDevice(deviceUuids);
        Set<String> devUuids = new HashSet<>();

        if (StringUtils.hasText(signInTask.getDeviceUuids())) {
            devUuids.addAll(JSONObject.parseArray(signInTask.getDeviceUuids(), String.class));
        }

        List<String> userUuids = new ArrayList<>();
        if (StringUtils.hasText(signInTask.getUserUuids())) {
            userUuids = JSONObject.parseArray(signInTask.getUserUuids()).toJavaList(String.class);
        }

        devUuids.addAll(deviceUuids);
        signInTask.setDeviceUuids(JSONObject.toJSONString(devUuids));

        if (updateById(signInTask)) {
            addOrDeleteFaceReport(deviceUuids, signInTask, userUuids, ReportEnum.ADD);
            return true;
        }
        return false;
    }

    private void addOrDeleteFaceReport(List<String> deviceUuids, SignInTask signInTask, List<String> userUuids,
                                       ReportEnum report) {
        if (JSONObject.parseArray(signInTask.getSignInMode(), Integer.class).contains(SignInModeEnum.FACE.getCode())) {

            if (!CollectionUtils.isEmpty(userUuids) && !CollectionUtils.isEmpty(deviceUuids)) {
                //下发人脸
                syncProducer.addOrDeleteUserDeviceFlagSaveList(deviceUuids, userUuids, report.getCode());
            }
        }
    }

    private void saveDevice(List<String> deviceUuids) {
        if (CollectionUtils.isEmpty(deviceUuids)){
            return;
        }

        List<Device> list = deviceService.list();
        List<String> nativeDeviceList =
                list.stream().map(user -> user.getUuid()).collect(Collectors.toList());
        //求差集
        List<String> newDeviceUuidList = ListUtil.difference(nativeDeviceList, deviceUuids);
        ResponseVo<List<DeviceInfoVo>> responseVo = iotRemoteService.getDeviceListByUuidList(newDeviceUuidList);
        if (responseVo.getCode() == EResultCode.SUCCESS.getCode() && Objects.nonNull(responseVo.getData())) {
            List<DeviceInfoVo> deviceInfoVoList = responseVo.getData();
            for (DeviceInfoVo deviceInfoVo : deviceInfoVoList) {
                Device device = new Device();
                BeanUtils.copyProperties(deviceInfoVo, device);
                if (StringUtils.hasText(device.getAreaUuid())){
                    log.info("{}设备的地区uuid:{}",deviceInfoVo.getName(),device.getAreaUuid());
                    ResponseVo<AreaBriefInfoVo> remoteServiceInfoByUuid = areaRemoteService.findInfoByUuid(device.getAreaUuid());
                    AreaBriefInfoVo flagSaveListBo = SignTasks.checkHttpResponse(remoteServiceInfoByUuid);
                    device.setAreaCode(flagSaveListBo.getAreaCode());
                }
                deviceService.save(device);
            }
        }
    }

    @Override
    public SignInTask getTaskByUuid(String uuid) {
        LambdaQueryWrapper<SignInTask> eq = new LambdaQueryWrapper<SignInTask>()
                .eq(SignInTask::getUuid, uuid)
                .last("limit 1");
        return getOne(eq);
    }

    @Override
    public Map<String, SignInTask> getTasksByUuids(List<String> uuids) {
        LambdaQueryWrapper<SignInTask> eq = new LambdaQueryWrapper<SignInTask>()
                .in(SignInTask::getUuid, uuids);
        List<SignInTask> list = list(eq);
        HashMap<String, SignInTask> result = new HashMap<>();
        for (SignInTask signInTask : list) {
            result.put(signInTask.getUuid(), signInTask);
        }
        return result;
    }

    @Override
    public Boolean updateTaskByUpdateBo(SignInTaskUpdateBo updateBo) {

        SignInTask signInTask = new SignInTask();
        updateBo.setInstitutionUuid(getInstitutionUuid());

        BeanUtils.copyProperties(updateBo, signInTask);

        if (updateBo.getPollingMode().equals(PollingModeEnum.DAY.getCode())) {
            signInTask.setWeek(JSONObject.toJSONString(updateBo.getDateDays()));
        } else {
            signInTask.setWeek(JSONObject.toJSONString(updateBo.getWeekDays()));
        }
        if (Objects.nonNull(updateBo.getSignInMode())) {
            signInTask.setSignInMode(JSONObject.toJSONString(updateBo.getSignInMode()));
        }
        if (Objects.nonNull(updateBo.getPollingMode())) {
            signInTask.setPollingMode(PollingModeEnum.getByCode(updateBo.getPollingMode()));
        }
        if (Objects.nonNull(updateBo.getMoreConfig())) {
            signInTask.setMoreConfig(JSONObject.toJSONString(updateBo.getMoreConfig()));
            MoreConfig moreConfig = updateBo.getMoreConfig();
            if (moreConfig.getManager()!= null && moreConfig.getManager().getIsManager()){
                saveUser(moreConfig.getManager().getManagerUuids());
            }
            if (moreConfig.getReportPush()!= null && moreConfig.getReportPush().getIsReportPush()){
                saveUser(moreConfig.getReportPush().getPusherUuids());
            }
            if (moreConfig.getBodyTemperature()!= null && moreConfig.getBodyTemperature().getIsPush()){
                saveUser(moreConfig.getBodyTemperature().getPushUuids());
            }
        }

        return updateByUuid(signInTask);
    }

    private Boolean updateByUuid(SignInTask signInTask) {
        LambdaUpdateWrapper<SignInTask> eq = new LambdaUpdateWrapper<SignInTask>().eq(SignInTask::getUuid, signInTask.getUuid());
        return update(signInTask,eq);
    }

//    private void deleteSignStatisticsTask(SignInTask signInTask) {
//        LambdaQueryWrapper<SignStatistics> eq = new LambdaQueryWrapper<SignStatistics>().eq(SignStatistics::getTaskUuid, signInTask.getUuid());
//        signStatisticsService.remove(eq);
//    }

    @Override
    public Page<SignInTask> pageTask(String name, Integer taskStatus, Integer pageNo, Integer pageSize) {

        Page<SignInTask> signInTaskPage = new Page<>(pageNo, pageSize);

        LambdaQueryWrapper<SignInTask> qw = new LambdaQueryWrapper<>();
        qw.like(!StringUtils.isEmpty(name), SignInTask::getName, name)
                .eq(Objects.nonNull(taskStatus), SignInTask::getStatus, taskStatus)
                .eq(SignInTask::getInstitutionUuid,getInstitutionUuid())
                .orderByDesc(SignInTask::getStatus)
                .orderByDesc(SignInTask::getTaskStartDate);


        page(signInTaskPage, qw);
        return signInTaskPage;
    }

    @Override
    public boolean doEnable(String uuid, Boolean enable) {
        SignInTask task = getTaskByUuid(uuid);
        if (Objects.isNull(task)) {
            throw new IllegalArgumentException("任务已被删除");
        }
        task.setStatus(enable);
        task.setIsEnable(enable);
        return updateById(task);
    }

    @Override
    public SignInTaskVo getTaskVoByUuid(String uuid) {
        SignInTaskVo result = new SignInTaskVo();
        SignInTask signInTask = getTaskByUuid(uuid);
        if (Objects.isNull(signInTask)) {
            return result;
        }
        String moreConfigJson = signInTask.getMoreConfig();
        MoreConfig moreConfig = JSONObject.parseObject(moreConfigJson).toJavaObject(MoreConfig.class);
        DatePeriod datePeriod = CzBeanUtils.copyProperties(signInTask, DatePeriod::new);
        TimePeriod timePeriod = CzBeanUtils.copyProperties(signInTask, TimePeriod::new);
        BeanUtils.copyProperties(signInTask, result);
        result.setPollingMode(signInTask.getPollingMode().getCode());
        result.setSignInMode(JSONObject.parseArray(signInTask.getSignInMode(), Integer.class));
        result.setMoreConfig(moreConfig);
        result.setDatePeriod(datePeriod);
        result.setTimePeriod(timePeriod);
        if (signInTask.getPollingMode().equals(PollingModeEnum.WEEK)) {
            result.setWeekDays(JSONObject.parseArray(signInTask.getWeek(), Integer.class));
        } else {
            result.setDateDays(JSONObject.parseArray(signInTask.getWeek(), String.class));
        }

        return result;
    }

    @Override
    public PageVo<SignInTaskPageVo> pageTaskVo(String name, Integer taskStatus, Integer pageNo, Integer pageSize) {
        Page<SignInTask> signInTaskPage = pageTask(name, taskStatus, pageNo, pageSize);
        List<SignInTask> taskList = signInTaskPage.getRecords();

        //任务列表
        List<SignInTaskPageVo> recodes = taskList.stream().map(signInTask -> {
            SignInTaskPageVo signInTaskPageVo = new SignInTaskPageVo();
            BeanUtils.copyProperties(signInTask, signInTaskPageVo);
            signInTaskPageVo.setPollingMode(signInTask.getPollingMode().getCode());
            String pageVoWeek = getPageVoWeek(signInTask);
            signInTaskPageVo.setWeekDays(pageVoWeek);
            signInTaskPageVo.setTimePeriod(SignTasks.getPeriodNameResult(signInTask));
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            signInTaskPageVo.setDatePeriod(signInTask.getTaskStartDate().equals(signInTask.getTaskEndDate()) ?
                    signInTask.getTaskStartDate().format(dateTimeFormatter) + "-" + signInTask.getTaskEndDate().format(dateTimeFormatter) :
                    signInTask.getTaskStartDate().format(dateTimeFormatter));
            if (StringUtils.hasText(signInTask.getUserUuids())) {
                signInTaskPageVo.setUserCount(JSONObject.parseArray(signInTask.getUserUuids(), String.class).size());
            } else {
                signInTaskPageVo.setUserCount(0);
            }
            if (StringUtils.hasText(signInTask.getDeviceUuids())) {
                signInTaskPageVo.setDeviceCount(JSONObject.parseArray(signInTask.getDeviceUuids(), String.class).size());
            } else {
                signInTaskPageVo.setDeviceCount(0);
            }
            List<Integer> signInModes = JSONObject.parseArray(signInTask.getSignInMode(), Integer.class);
            signInTaskPageVo.setSignInModes(signInModes);
            if (null != signInTask.getEditor()) {
                String editor = signInTask.getEditor();
                User user = userService.getByUserUuid(editor);
                if (user == null) {
                    saveUser(Arrays.asList(editor));
                    user = userService.getByUserUuid(editor);
                }
                signInTaskPageVo.setEditor(user.getName());
            } else {
                String creator = signInTask.getCreator();
                User user = userService.getByUserUuid(creator);
                if (user == null) {
                    saveUser(Arrays.asList(creator));
                    user = userService.getByUserUuid(creator);
                }
                signInTaskPageVo.setEditor(user.getName());
            }
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
            signInTaskPageVo.setLastUpdateTime(signInTask.getUpdateTime() == null ? signInTask.getCreateTime().format(timeFormatter) : signInTask.getUpdateTime().format(timeFormatter));
            signInTaskPageVo.setStatus(signInTask.getStatus() ? 1 : 0);
            return signInTaskPageVo;
        }).collect(Collectors.toList());

        PageVo<SignInTaskPageVo> result = new PageVo<>();
        BeanUtils.copyProperties(signInTaskPage, result, "records");
        result.setRecords(recodes);
        return result;
    }

    @Override
    public boolean taskDelete(String uuid) {
        SignInTask signInTask = getTaskByUuid(uuid);
        if (Objects.isNull(signInTask)) {
            return false;
        }
        List<String> userUuids = new ArrayList<>();
        List<String> deviceUuids = new ArrayList<>();
        if (StringUtils.hasText(signInTask.getUserUuids())){
            userUuids = JSONObject.parseArray(signInTask.getUserUuids(), String.class);
        }
        if (StringUtils.hasText(signInTask.getDeviceUuids())){
            deviceUuids = JSONObject.parseArray(signInTask.getDeviceUuids(), String.class);
        }

        if (removeById(signInTask)){
            addOrDeleteFaceReport(deviceUuids, signInTask,userUuids,ReportEnum.DELETE);
            //删除统计
            signInRecordService.deleteByTaskUuid(signInTask.getUuid());
            return true;
        }
        return false;
    }

    @Override
    public PageVo<UserPageVo> pageUser(String uuid, String name,
                                      String organizationUuid, Integer pageNo, Integer pageSize) {
        Page<User> userPage = new Page<>(pageNo, pageSize);
        PageVo<UserPageVo> result = new PageVo<>();
        List<String> userUuidList = Lists.newArrayList();
        SignInTask signInTask = getTaskByUuid(uuid);
        String userUuidsJson = signInTask.getUserUuids();
        if (StringUtils.hasText(userUuidsJson)){
            userUuidList = JSONObject.parseArray(userUuidsJson,String.class);
        }
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<User>()
                .like(StringUtils.hasText(name),User::getName,name)
                .like(StringUtils.hasText(organizationUuid),User::getOrganizationUuid,organizationUuid)
                .orderByDesc(User::getCreateTime);

        if (CollectionUtils.isEmpty(userUuidList)){
            return result;
        }else {
            qw.in(User::getUuid,userUuidList);
        }

        userService.page(userPage,qw);
        BeanUtils.copyProperties(userPage,result);
        List<UserPageVo> records = CzBeanUtils.copyListProperties(userPage.getRecords(), UserPageVo::new);
        result.setRecords(records);
        return result;
    }

    @Override
    public PageVo<DevicePageVo> devicePage(String uuid, Integer pageNo, Integer pageSize, String name,
                                           String number, String areaCode) {
        Page<Device> userPage = new Page<>(pageNo, pageSize);
        PageVo<DevicePageVo> result = new PageVo<>();
        List<String> deviceUuidList = Lists.newArrayList();
        SignInTask signInTask = getTaskByUuid(uuid);
        String deviceUuidJson = signInTask.getDeviceUuids();
        if (StringUtils.hasText(deviceUuidJson)) {
            deviceUuidList = JSONObject.parseArray(deviceUuidJson, String.class);
        }

        if (CollectionUtils.isEmpty(deviceUuidList)) {
            return result;
        }

        LambdaQueryWrapper<Device> qw = new LambdaQueryWrapper<Device>()
                .in(!CollectionUtils.isEmpty(deviceUuidList), Device::getUuid, deviceUuidList)
                .like(StringUtils.hasText(name), Device::getName, name)
                .likeRight(StringUtils.hasText(areaCode), Device::getAreaCode, areaCode)
                .like(StringUtils.hasText(number), Device::getNumber, number);

        deviceService.page(userPage, qw);

        BeanUtils.copyProperties(userPage, result);
        List<DevicePageVo> records = CzBeanUtils.copyListProperties(userPage.getRecords(), DevicePageVo::new);
        result.setRecords(records);
        return result;
    }

    @Override
    public boolean deleteTaskUser(String uuid, List<String> userUuids) {
        //获取人员
        SignInTask signInTask = getTaskByUuid(uuid);
        Assert.notNull(signInTask, EResultCode.NullDataFail.getMessage());
        String uuidsJson = signInTask.getUserUuids();
        List<String> userUuidList = JSONObject.parseArray(uuidsJson, String.class);
        userUuidList = removeAll(userUuidList,userUuids);
        //保存
        signInTask.setUserUuids(JSONObject.toJSONString(userUuidList));
        List<String> deviceUuids = new ArrayList<>();
        if (StringUtils.hasText(signInTask.getDeviceUuids())){
            deviceUuids = JSONObject.parseArray(signInTask.getDeviceUuids(), String.class);
        }

        if (updateById(signInTask)) {
            addOrDeleteFaceReport(userUuids, signInTask, deviceUuids,ReportEnum.DELETE);
            return true;
        }
        return false;
    }



    @Override
    public boolean deleteDeviceUser(String uuid, List<String> deviceUuids) {
        SignInTask signInTask = getTaskByUuid(uuid);
        Assert.notNull(signInTask, EResultCode.NullDataFail.getMessage());
        String uuidsJson = signInTask.getDeviceUuids();
        List<String> deviceUuidList = JSONObject.parseArray(uuidsJson, String.class);
        deviceUuidList = removeAll(deviceUuidList,deviceUuids);
        signInTask.setDeviceUuids(JSONObject.toJSONString(deviceUuidList));
        List<String> userUuids = new ArrayList<>();

        if (StringUtils.hasText(signInTask.getUserUuids())){
            userUuids = JSONObject.parseArray(signInTask.getUserUuids(), String.class);
        }

        if (updateById(signInTask)) {
            addOrDeleteFaceReport(userUuids, signInTask, deviceUuids,ReportEnum.DELETE);
            return true;
        }
        return false;
    }

    private List<String> removeAll(List<String> deviceUuidList, List<String> deviceUuids) {
        for (String deviceUuid : deviceUuids) {
            deviceUuidList.remove(deviceUuid);
        }
        return deviceUuidList;
    }

    @Override
    public Set<String> getOrganizationNameList() {
        Set<String> result = new HashSet<>();
        List<User> list = userService.list(new LambdaQueryWrapper<User>().orderByDesc(BaseEntity::getCreateTime));
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        for (User user : list) {
            result.add(user.getOrganizationName());
        }
        return result;
    }

    @Override
    public Set<String> getAreaAddressNameList() {
        Set<String> result = new HashSet<>();
        List<Device> list = deviceService.list(new LambdaQueryWrapper<Device>().orderByDesc(BaseEntity::getCreateTime));
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        for (Device device : list) {
            result.add(device.getAreaAddress());
        }
        return result;
    }

    @Override
    public boolean deleteAllUser(String uuid) {
        SignInTask task = getTaskByUuid(uuid);
        task.setUserUuids("[]");
        return updateById(task);
    }

    @Override
    public boolean deleteAllDevice(String uuid) {
        SignInTask task = getTaskByUuid(uuid);
        task.setDeviceUuids("[]");
        return updateById(task);
    }

    private static String getPageSignInMode(SignInTask signInTask) {
        String signInMode = "";
        String taskSignInMode = signInTask.getSignInMode();
        List<Integer> codeList = JSONObject.parseArray(taskSignInMode, Integer.class);
        for (int i = 0; i < codeList.size(); i++) {
            signInMode += SignInModeEnum.getByCode(codeList.get(i)).getDescription();
            if (i != codeList.size() - 1) {
                signInMode += "、";
            }
        }
        return signInMode;
    }

    private static String getPageVoWeek(SignInTask signInTask) {
        String pageVoWeek = "";
        if (signInTask.getPollingMode().getCode().equals(PollingModeEnum.DAY.getCode())) {
            //单日
            List<LocalDateTime> localDateTimes = JSONObject.parseArray(signInTask.getWeek()).toJavaList(LocalDateTime.class);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            for (int i = 0; i < localDateTimes.size(); i++) {
                pageVoWeek += localDateTimes.get(i).format(dateTimeFormatter);
                if (i != localDateTimes.size() - 1) {
                    pageVoWeek += "、 ";
                }
            }
        } else {
            List<Integer> weekCodes = JSONObject.parseArray(signInTask.getWeek()).toJavaList(Integer.class);
            for (int i = 0; i < weekCodes.size(); i++) {
                pageVoWeek += WeekEnum.getDescription(weekCodes.get(i));
                if (i != weekCodes.size() - 1) {
                    pageVoWeek += "、";
                }
            }
            if (weekCodes.size() == 7) {
                pageVoWeek = WeekEnum.EVERYDAY.getDescription();
            }
        }
        return pageVoWeek;
    }

    @Override
    public List<TaskUserPageVo> userPage(List<String> userUuids) {
        Map<String, User> userMap = userService.getByUserUuids(userUuids);
        ArrayList<TaskUserPageVo> result = Lists.newArrayList();
        userMap.forEach((uuid, user) -> {
            TaskUserPageVo vo = new TaskUserPageVo();
            vo.setUuid(uuid);
            vo.setName(user.getName());
            vo.setUserTypes(UserType.parseTypes(user.getUserType()));
            result.add(vo);
        });
        return result;
    }

    @Override
    public List<SignInTask> getTodayTasks(LocalDate date,String deviceUuid) {
        LambdaQueryWrapper<SignInTask> qw = new LambdaQueryWrapper<SignInTask>();
        qw.le(SignInTask::getTaskStartDate, date)
                .ge(SignInTask::getTaskEndDate, date)
                .eq(SignInTask::getIsEnable, true)
                .like(StringUtils.hasText(deviceUuid),SignInTask::getDeviceUuids,deviceUuid)
                .orderByAsc(SignInTask::getTaskStartTime)
                .orderByAsc(SignInBase::getTaskEndTime);

        //过滤节假日
        return list(qw).stream().filter(signInTask -> {
                    if (signInTask.getFilterFestival()) {
                        JuheBean vacation = JuheUtil.getVacation(stringRedisTemplate, LocalDate.now());
                        if (vacation.getResult().getStatus() == null) {
                            //为工作日
                            return true;
                        }
                        return false;
                    }
                    return true;
                })
                //过滤周期内
                .filter(signInTask -> {
                    PollingModeEnum pollingMode = signInTask.getPollingMode();
                    if (pollingMode.equals(PollingModeEnum.DAY)) {
                        List<String> list = JSONObject.parseArray(signInTask.getWeek(), String.class);
                        List<LocalDate> collect = list.stream().map(s -> {
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            return LocalDate.parse(s, dateTimeFormatter);
                        }).collect(Collectors.toList());
                        //包含日期 保留
                        if (collect.contains(date)) {
                            return true;
                        }
                        return false;
                    }
                    else {
                        List<Integer> list = JSONObject.parseArray(signInTask.getWeek(), Integer.class);
                        List<LocalDate> periodDate = TimeUtil.getPeriodDate(signInTask.getTaskStartDate(), signInTask.getTaskEndDate());

                        for (LocalDate localDate : periodDate) {
                            int week = localDate.getDayOfWeek().getValue();
                            if (list.contains(week)) {
                                //包含星期,保留
                                return true;
                            }
                            return false;
                        }
                        return false;
                    }
                }).collect(Collectors.toList());
    }

    @Override
    public List<SignInTask> getManageTodayTasks(LocalDate date, String operatorUuid) {
        List<SignInTask> sign = getTodayTasks(date,null);
        List<SignInTask> signInTasks = sign.stream().filter(signInTask -> {
            SignInTask task = getTaskByUuid(signInTask.getUuid());
            MoreConfig moreConfig = JSONObject.parseObject(task.getMoreConfig(), MoreConfig.class);
            if (moreConfig != null && moreConfig.getManager() != null && moreConfig.getManager().getIsManager()) {
                if (moreConfig.getManager().getManagerUuids().contains(operatorUuid)) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
        return signInTasks;
    }

    @Override
    public List<SignInTask> getUserTodayTasks(LocalDate date, String operatorUuid) {
        LambdaQueryWrapper<SignInTask> qw = new LambdaQueryWrapper<>();
        qw.le(SignInTask::getTaskStartDate, date)
                .ge(SignInTask::getTaskEndDate, date)
                .eq(SignInTask::getIsEnable, true)
                .like(SignInTask::getUserUuids,operatorUuid)
                .orderByAsc(SignInTask::getTaskStartTime)
                .orderByAsc(SignInBase::getTaskEndTime);
        return list(qw);
    }
}

