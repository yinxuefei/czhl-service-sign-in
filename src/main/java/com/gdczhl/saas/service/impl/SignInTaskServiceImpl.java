package com.gdczhl.saas.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdczhl.saas.bo.feign.organization.OrganizationVo;
import com.gdczhl.saas.bo.feign.user.UserVo;
import com.gdczhl.saas.controller.external.pojo.DatePeriod;
import com.gdczhl.saas.controller.external.pojo.MoreConfig;
import com.gdczhl.saas.controller.external.pojo.TimePeriod;
import com.gdczhl.saas.controller.external.pojo.bo.SignInTaskSaveBo;
import com.gdczhl.saas.controller.external.pojo.bo.SignInTaskUpdateBo;
import com.gdczhl.saas.controller.external.pojo.vo.DevicePageVo;
import com.gdczhl.saas.controller.external.pojo.vo.SignInTaskPageVo;
import com.gdczhl.saas.controller.external.pojo.vo.SignInTaskSaveVo;
import com.gdczhl.saas.controller.external.pojo.vo.UserPageVo;
import com.gdczhl.saas.entity.BaseEntity;
import com.gdczhl.saas.entity.Device;
import com.gdczhl.saas.entity.SignInTask;
import com.gdczhl.saas.entity.User;
import com.gdczhl.saas.enums.EResultCode;
import com.gdczhl.saas.enums.PollingModeEnum;
import com.gdczhl.saas.enums.SignInModeEnum;
import com.gdczhl.saas.enums.WeekEnum;
import com.gdczhl.saas.mapper.SignInTaskMapper;
import com.gdczhl.saas.service.IDeviceService;
import com.gdczhl.saas.service.ISignInTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdczhl.saas.service.IUserService;
import com.gdczhl.saas.service.remote.BaseServiceRemote;
import com.gdczhl.saas.service.remote.IotRemoteService;
import com.gdczhl.saas.utils.CzBeanUtils;
import com.gdczhl.saas.utils.ListUtil;
import com.gdczhl.saas.utils.SignTasks;
import com.gdczhl.saas.utils.TimeUtil;
import com.gdczhl.saas.vo.PageVo;
import com.gdczhl.saas.vo.ResponseVo;
import com.gdczhl.saas.vo.feign.iot.response.DeviceInfoVo;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
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


    @Override
    public boolean add(SignInTaskSaveBo saveBo) {
        SignInTask signInTask = new SignInTask();
        BeanUtils.copyProperties(saveBo,signInTask);
        List<String> weekDays = saveBo.getWeekDays();
        signInTask.setWeek(JSONObject.toJSONString(weekDays));
        MoreConfig moreConfig = saveBo.getMoreConfig();
        signInTask.setMoreConfig(JSONObject.toJSONString(moreConfig));
        signInTask.setPollingMode(PollingModeEnum.getByCode(saveBo.getPollingMode()));
        signInTask.setSignInMode(JSONObject.toJSONString(saveBo.getSignInMode()));

        //添加负责人
        MoreConfig.Manager manager = moreConfig.getManager();
        if (null!= manager){
            if (manager.getIsManager()){
                List<String> managerUuids = manager.getManagerUuids();
                saveUser(managerUuids);
            }
        }

        //添加汇报推送人
        MoreConfig.ReportPush reportPush = moreConfig.getReportPush();
        if (null!= reportPush){
            if (reportPush.getIsReportPush()){
                List<String> pusherUuids = reportPush.getPusherUuids();
                saveUser(pusherUuids);
            }
        }

        return save(signInTask);
    }

    @Override
    public Map<String,Set<String>> getTaskNameList() {
        Map<String,Set<String>> result = new HashMap<>();
        //单日循环
        Set<String> daySet = getDaySetOrWeekSet(PollingModeEnum.DAY.getCode());
        result.put(PollingModeEnum.DAY.getDescription(),daySet);
        Set<String> weekSet = getDaySetOrWeekSet(PollingModeEnum.WEEK.getCode());
        result.put(PollingModeEnum.WEEK.getDescription(),weekSet);
        return result;
    }

    private Set<String> getDaySetOrWeekSet(Integer code) {
        HashSet<String> result = new HashSet<>();
        LambdaQueryWrapper<SignInTask> qw = new LambdaQueryWrapper<SignInTask>()
                .eq(SignInTask::getStatus, true)
                .eq(Objects.nonNull(code),SignInTask::getPollingMode,PollingModeEnum.getByCode(code))
                .orderByDesc(BaseEntity::getCreateTime);
        List<SignInTask> list = list(qw);
        if (CollectionUtils.isEmpty(list)){
            return result;
        }
        list.forEach(signInTask -> {
            LocalTime taskStartTime = signInTask.getTaskStartTime();
            LocalTime taskEndTime = signInTask.getTaskEndTime();
            String name = signInTask.getName();
            String taskName = signInTask.getTaskName();
            String taskNameResult = SignTasks.getTaskNameResult(name, taskName, taskStartTime, taskEndTime,
                    DateTimeFormatter.ofPattern("HH:mm"));
            result.add(taskNameResult);
        });
        return result;
    }

    @Override
    public boolean setUsers(List<String> userUuids, String uuid) {
        SignInTask signInTask = getTaskByUuid(uuid);
        Assert.notNull(signInTask,"任务未开启");
        log.info("保存user信息到本地库");
        saveUser(userUuids);
        signInTask.setUserUuids(JSONObject.toJSONString(userUuids));
        return updateById(signInTask);
    }

    private void saveUser(List<String> userUuids) {
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
                BeanUtils.copyProperties(userVo,user);
                user.setOrganizationName(organizationVo.getName());
                userService.save(user);
            }
        }


    @Override
    public boolean setDevices(List<String> deviceUuids, String uuid) {
        SignInTask signInTask = getTaskByUuid(uuid);
        Assert.notNull(signInTask,"任务未开启");
        log.info("保存设备信息到本地库");
        saveDevice(deviceUuids);
        List<String> snList = Arrays.asList();
        ResponseVo<List<DeviceInfoVo>> iotResponse = iotRemoteService.getDeviceListByUuidList(deviceUuids);
        if (iotResponse.getCode()== EResultCode.SUCCESS.getCode() && Objects.nonNull(iotResponse.getData())){
            List<DeviceInfoVo> deviceInfoVoList = iotResponse.getData();
            snList = deviceInfoVoList.stream().map(deviceInfoVo -> deviceInfoVo.getNumberSn()).collect(Collectors.toList());
        }
        signInTask.setDeviceUuids(JSONObject.toJSONString(deviceUuids));
        signInTask.setDeviceSns(JSONObject.toJSONString(snList));
        return updateById(signInTask);
    }

    private void saveDevice(List<String> deviceUuids) {
        List<String> nativeDeviceList = deviceService.list().stream().map(user -> user.getUuid()).collect(Collectors.toList());
        //求差集
        List<String> newDeviceUuidList = ListUtil.difference(nativeDeviceList, deviceUuids);
        ResponseVo<List<DeviceInfoVo>> responseVo = iotRemoteService.getDeviceListByUuidList(newDeviceUuidList);
        if (responseVo.getCode()== EResultCode.SUCCESS.getCode() && Objects.nonNull(responseVo.getData())){
            List<DeviceInfoVo> deviceInfoVoList = responseVo.getData();
            for (DeviceInfoVo deviceInfoVo: deviceInfoVoList) {
                Device device = new Device();
                BeanUtils.copyProperties(deviceInfoVo,device);
                deviceService.save(device);
            }
        }
    }

    @Override
    public SignInTask getTaskByUuid(String uuid) {
        LambdaQueryWrapper<SignInTask> eq = new LambdaQueryWrapper<SignInTask>()
                .eq(SignInTask::getUuid, uuid)
                .eq(SignInTask::getStatus,true)
                .last("limit 1");
        return  getOne(eq);
    }

    @Override
    public Map<String,SignInTask> getTasksByUuids(List<String> uuids) {
        LambdaQueryWrapper<SignInTask> eq = new LambdaQueryWrapper<SignInTask>()
                .in(SignInTask::getUuid, uuids)
                .eq(SignInTask::getStatus,true);
        List<SignInTask> list = list(eq);
        HashMap<String,SignInTask> result = new HashMap<>();
        for (SignInTask signInTask : list) {
            result.put(signInTask.getUuid(),signInTask);
        }
        return result;
    }

    @Override
    public Boolean updateTaskByUpdateBo(SignInTaskUpdateBo updateBo) {
        String uuid = updateBo.getUuid();
        Assert.notNull(uuid,"空参数异常");
        SignInTask signInTask = getTaskByUuid(uuid);
        Assert.notNull(signInTask,"任务未开启");
        BeanUtils.copyProperties(updateBo,signInTask);
        if (Objects.nonNull(updateBo.getWeekDays())){
            signInTask.setWeek(JSONObject.toJSONString(updateBo.getWeekDays()));
        }
        if (Objects.nonNull(updateBo.getSignInMode())){
            signInTask.setSignInMode(JSONObject.toJSONString(updateBo.getSignInMode()));
        }
        if (Objects.nonNull(updateBo.getPollingMode())){
            signInTask.setPollingMode(PollingModeEnum.getByCode(updateBo.getPollingMode()));
        }
        if (Objects.nonNull(updateBo.getMoreConfig())){
            signInTask.setMoreConfig(JSONObject.toJSONString(updateBo.getMoreConfig()));
        }
        return updateById(signInTask);
    }

    @Override
    public Page<SignInTask> pageTask(String name, String taskName, Integer pageNo, Integer pageSize) {
        SignInTask signInTask = null;
        Page<SignInTask> signInTaskPage = new Page<>(pageNo, pageSize);
        if (!StringUtils.isEmpty(taskName)){
            signInTask = SignTasks.parseTaskNameResult(taskName);
        }
        LambdaQueryWrapper<SignInTask> qw = new LambdaQueryWrapper<>();
        qw.like(!StringUtils.isEmpty(name), SignInTask::getName,name)
                .orderByDesc(BaseEntity::getUpdateTime)
                .orderByDesc(BaseEntity::getCreateTime);

        if (null != signInTask){
            qw.eq(SignInTask::getTaskStartTime,signInTask.getTaskStartTime())
                    .eq(SignInTask::getTaskEndTime,signInTask.getTaskEndTime())
                    .eq(SignInTask::getTaskName,signInTask.getTaskName());
        }
        page(signInTaskPage,qw);
        return signInTaskPage;
    }

    @Override
    public boolean doEnable(String uuid, Boolean enable) {
        SignInTask task = getTaskByUuid(uuid);
        if (Objects.isNull(task)){
            throw new IllegalArgumentException("任务已被删除");
        }
        task.setStatus(enable);
        return updateById(task);
    }

    @Override
    public SignInTaskSaveVo getTaskVoByUuid(String uuid) {
        SignInTaskSaveVo result = new SignInTaskSaveVo();
        SignInTask signInTask = getTaskByUuid(uuid);
        if (Objects.isNull(signInTask)){
            return result;
        }
        String moreConfigJson = signInTask.getMoreConfig();
        MoreConfig moreConfig = JSONObject.parseObject(moreConfigJson).toJavaObject(MoreConfig.class);
        DatePeriod datePeriod = CzBeanUtils.copyProperties(signInTask, DatePeriod::new);
        TimePeriod timePeriod = CzBeanUtils.copyProperties(signInTask, TimePeriod::new);
        BeanUtils.copyProperties(signInTask,result);
        result.setPollingMode(signInTask.getPollingMode().getCode());
        result.setSignInMode(JSONObject.parseArray(signInTask.getSignInMode(),Integer.class));
        result.setMoreConfig(moreConfig);
        result.setDatePeriod(datePeriod);
        result.setTimePeriod(timePeriod);
        result.setWeekDays(JSONObject.parseArray(signInTask.getWeek(), String.class));
        return result;
    }

    @Override
    public PageVo<SignInTaskPageVo> pageTaskVo(String name, String taskName, Integer pageNo, Integer pageSize) {
        Page<SignInTask> signInTaskPage = pageTask(name, taskName, pageNo, pageSize);
        List<SignInTask> taskList = signInTaskPage.getRecords();

        List<SignInTaskPageVo> recodes = taskList.stream().map(signInTask -> {
            SignInTaskPageVo signInTaskPageVo = new SignInTaskPageVo();
            BeanUtils.copyProperties(signInTask,signInTaskPageVo);
            signInTaskPageVo.setPollingMode(signInTask.getPollingMode().getDescription());
            String pageVoWeek = getPageVoWeek(signInTask);
            signInTaskPageVo.setWeekDays(pageVoWeek);
            signInTaskPageVo.setTimePeriod(SignTasks.getTaskNameResult(signInTask.getName(), signInTask.getTaskName(),
                    signInTask.getTaskStartTime(),
                    signInTask.getTaskEndTime(), DateTimeFormatter.ofPattern("HH:mm")));
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            signInTaskPageVo.setDatePeriod(Objects.nonNull(signInTask.getTaskEndDate()) ?
                    signInTask.getTaskStartDate().format(dateTimeFormatter) + "-" + signInTask.getTaskEndDate().format(dateTimeFormatter) :
                    signInTask.getTaskStartDate().format(dateTimeFormatter));
            if (StringUtils.hasText(signInTask.getUserUuids())){
            signInTaskPageVo.setUserUuidList(JSONObject.parseArray(signInTask.getUserUuids(), String.class).size());}
            if (StringUtils.hasText(signInTask.getDeviceUuids())){
            signInTaskPageVo.setDeviceUuidList(JSONObject.parseArray(signInTask.getDeviceUuids(), String.class).size());}
            String signInMode = getPageSignInMode(signInTask);
            signInTaskPageVo.setSignInMode(signInMode);
            if (null != signInTask.getEditor()) {
                signInTaskPageVo.setEditor(userService.getByUserUuid(signInTask.getEditor()).getName());
            }else {
                signInTaskPageVo.setEditor(userService.getByUserUuid(signInTask.getCreator()).getName());
            }
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
            signInTaskPageVo.setLastUpdateTime(signInTask.getUpdateTime() == null? signInTask.getCreateTime().format(timeFormatter): signInTask.getUpdateTime().format(timeFormatter));
            return signInTaskPageVo;
        }).collect(Collectors.toList());

        PageVo<SignInTaskPageVo> result = new PageVo<>();
        BeanUtils.copyProperties(signInTaskPage,result,"records");
        result.setRecords(recodes);
        return result;
    }

    @Override
    public boolean taskDelete(String uuid) {
        SignInTask signInTask = getTaskByUuid(uuid);
        if (Objects.isNull(signInTask)){
            return false;
        }
        return removeById(signInTask);
    }

    @Override
    public PageVo<UserPageVo> pageUser(String uuid, String name,
                                      String organizationName, Integer pageNo, Integer pageSize) {
        Page<User> userPage = new Page<>(pageNo, pageSize);
        List<String> userUuidList = Lists.newArrayList();
        SignInTask signInTask = getTaskByUuid(uuid);
        String userUuidsJson = signInTask.getUserUuids();
        if (StringUtils.hasText(userUuidsJson)){
            userUuidList = JSONObject.parseArray(userUuidsJson,String.class);
        }
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<User>()
                .in(CollectionUtils.isEmpty(userUuidList), User::getUuid, userUuidList)
                .like(StringUtils.hasText(name),User::getName,name)
                .like(StringUtils.hasText(organizationName),User::getOrganizationName,organizationName)
                .orderByDesc(User::getCreateTime);

        userService.page(userPage,qw);
        PageVo<UserPageVo> result = new PageVo<>();
        BeanUtils.copyProperties(userPage,result);
        ArrayList<UserPageVo> records = new ArrayList<>();
        BeanUtils.copyProperties(userPage.getRecords(),records);
        result.setRecords(records);
        return result;
    }

    @Override
    public PageVo<DevicePageVo> devicePage(String uuid, Integer pageNo, Integer pageSize,String name,
                                           String address) {
        Page<Device> userPage = new Page<>(pageNo, pageSize);
        List<String> deviceUuidList = Lists.newArrayList();
        SignInTask signInTask = getTaskByUuid(uuid);
        String deviceUuidJson = signInTask.getDeviceUuids();
        if (StringUtils.hasText(deviceUuidJson)){
            deviceUuidList = JSONObject.parseArray(deviceUuidJson,String.class);
        }
        LambdaQueryWrapper<Device> qw = new LambdaQueryWrapper<Device>()
                .in(CollectionUtils.isEmpty(deviceUuidList), Device::getUuid, deviceUuidList)
                .like(StringUtils.hasText(name),Device::getName,name)
                .eq(StringUtils.hasText(address),Device::getAreaAddress,address)
                .orderByDesc(Device::getCreateTime);
        deviceService.page(userPage,qw);
        PageVo<DevicePageVo> result = new PageVo<>();
        BeanUtils.copyProperties(userPage,result);
        ArrayList<DevicePageVo> records = new ArrayList<>();
        BeanUtils.copyProperties(userPage.getRecords(),records);
        result.setRecords(records);
        return result;
    }

    @Override
    public boolean deleteTaskUser(String uuid, List<String> userUuids) {
        SignInTask signInTask = getTaskByUuid(uuid);
        Assert.notNull(signInTask,EResultCode.NullDataFail.getMessage());
        String uuidsJson = signInTask.getUserUuids();
        List<String> userUuidList = JSONObject.parseArray(uuidsJson, String.class);
        userUuidList.removeAll(userUuids);
        signInTask.setUserUuids(JSONObject.toJSONString(userUuidList));
        return updateById(signInTask);
    }

    @Override
    public boolean deleteDeviceUser(String uuid, List<String> devices) {
        SignInTask signInTask = getTaskByUuid(uuid);
        Assert.notNull(signInTask,EResultCode.NullDataFail.getMessage());
        String uuidsJson = signInTask.getDeviceUuids();
        List<String> deviceUuidList = JSONObject.parseArray(uuidsJson, String.class);
        deviceUuidList.removeAll(devices);
        signInTask.setUserUuids(JSONObject.toJSONString(deviceUuidList));
        return updateById(signInTask);
    }

    @Override
    public Set<String> getOrganizationNameList() {
        Set<String> result = new HashSet<>();
        List<User> list = userService.list(new LambdaQueryWrapper<User>().orderByDesc(BaseEntity::getCreateTime));
        if (CollectionUtils.isEmpty(list)){
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
        if (CollectionUtils.isEmpty(list)){
            return result;
        }
        for (Device device : list) {
            result.add(device.getAreaAddress());
        }
        return result;
    }

    private static String getPageSignInMode(SignInTask signInTask) {
        String signInMode = "";
        String taskSignInMode = signInTask.getSignInMode();
        List<Integer> codeList = JSONObject.parseArray(taskSignInMode, Integer.class);
        for (int i = 0; i < codeList.size(); i++) {
            signInMode += SignInModeEnum.getByCode(codeList.get(i)).getDescription();
            if (i != codeList.size()-1){
                signInMode += "、";
            }
        }
        return signInMode;
    }

    private static String getPageVoWeek(SignInTask signInTask) {
        String pageVoWeek = "";
        if (signInTask.getPollingMode().getCode().equals(PollingModeEnum.DAY.getCode())){
            //单日
            List<LocalDateTime> localDateTimes = JSONObject.parseArray(signInTask.getWeek()).toJavaList(LocalDateTime.class);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            for (int i = 0; i < localDateTimes.size(); i++) {
                pageVoWeek += localDateTimes.get(i).format(dateTimeFormatter);
                if (i != localDateTimes.size()-1){
                    pageVoWeek += "、 ";
                }
            }
        }else {
            List<String> localDateTimes = JSONObject.parseArray(signInTask.getWeek()).toJavaList(String.class);
            for (int i = 0; i < localDateTimes.size(); i++) {
                pageVoWeek += localDateTimes.get(i);
                if (i != localDateTimes.size()-1){
                    pageVoWeek += "、";
                }
            }

            if (localDateTimes.size()==7){
                pageVoWeek = WeekEnum.EVERYDAY.getDescription();
            }
        }
        return pageVoWeek;
    }



}
