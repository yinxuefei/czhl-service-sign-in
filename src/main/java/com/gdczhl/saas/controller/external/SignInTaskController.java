package com.gdczhl.saas.controller.external;

import com.gdczhl.saas.enums.PollingModeEnum;
import com.gdczhl.saas.pojo.DatePeriod;
import com.gdczhl.saas.pojo.bo.signInTask.*;
import com.gdczhl.saas.pojo.TimePeriod;
import com.gdczhl.saas.pojo.vo.DevicePageVo;
import com.gdczhl.saas.pojo.vo.UserPageVo;
import com.gdczhl.saas.pojo.vo.signInTask.*;
import com.gdczhl.saas.enums.EResultCode;
import com.gdczhl.saas.pojo.vo.signInTask.TaskNameVo;
import com.gdczhl.saas.service.ISignInTaskService;
import com.gdczhl.saas.utils.CzBeanUtils;
import com.gdczhl.saas.vo.PageVo;
import com.gdczhl.saas.vo.ResponseVo;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hkx
 * @since 2023-01-05
 */
@Api(tags = "任务管理")
@Slf4j
@RestController
@RequestMapping("external/signInTask")
public class SignInTaskController {

    @Autowired
    private ISignInTaskService signInTaskService;

    @PostMapping("add")
    @ApiOperation("添加任务")
    public ResponseVo<String> addTask(@RequestBody SignInTaskSaveVo saveVo) {
        Assert.notNull(saveVo, "任务为空,添加失败");
        //封装bo
        SignInTaskSaveBo saveBo = new SignInTaskSaveBo();
        BeanUtils.copyProperties(saveVo, saveBo);
        if (saveVo.getPollingMode().equals(PollingModeEnum.DAY.getCode())) {
            saveBo.setWeekDays(saveVo.getDateDays());
        } else {
            saveBo.setWeekDays(saveVo.getWeekDays().stream().map(integer -> String.valueOf(integer)).collect(Collectors.toList()));
        }
        DatePeriod datePeriod = saveVo.getDatePeriod();
        TimePeriod timePeriod = saveVo.getTimePeriod();
        BeanUtils.copyProperties(datePeriod, saveBo);
        BeanUtils.copyProperties(timePeriod, saveBo);
        saveBo.setIsEnable(saveBo.getTaskStartDate().isAfter(LocalDate.now()) && saveBo.getTaskEndDate().isBefore(LocalDate.now()));
        return signInTaskService.add(saveBo) ? ResponseVo.success("添加成功") : ResponseVo.fail(EResultCode.InsertDataFail);
    }

    @PostMapping("setDevice")
    @ApiOperation("设置终端")
    public ResponseVo<String> setDevice(@RequestBody TaskDeviceVo taskDeviceVo) {
        if (Objects.isNull(taskDeviceVo.getDeviceUuids())) {
            return ResponseVo.success("添加成功");
        }
        return signInTaskService.setDevices(taskDeviceVo.getDeviceUuids(), taskDeviceVo.getUuid()) ? ResponseVo.success(
                "添加成功") :
                ResponseVo.fail(EResultCode.InsertDataFail);
    }

    @GetMapping("getTaskNameList")
    @ApiOperation("获取签到任务名称")
    public ResponseVo<List<TaskNameVo>> getTaskNameList(@ApiParam(value = "0,失效,1,未失效,null 全部") Integer type) {
        if (type.equals(-1)) {
            type = null;
        }
        return ResponseVo.success(signInTaskService.getTaskNameList(type));
    }

    @GetMapping("getTaskByUuid")
    @ApiOperation("查看任务")
    public ResponseVo<SignInTaskVo> getTaskVoByUuid(@ApiParam(value = "任务uuid") String uuid) {
        Assert.notNull(uuid, "参数为空");
        return ResponseVo.success(signInTaskService.getTaskVoByUuid(uuid));
    }

    @PutMapping("updateTask")
    @ApiOperation("更新任务信息")
    public ResponseVo<String> updateTaskByUuid(@ApiParam(value = "签到任务") @RequestBody SignInTaskUpdateVo updateVo) {
        SignInTaskUpdateBo updateBo = CzBeanUtils.copyProperties(updateVo, SignInTaskUpdateBo::new);
        DatePeriod datePeriod = updateVo.getDatePeriod();
        TimePeriod timePeriod = updateVo.getTimePeriod();
        BeanUtils.copyProperties(datePeriod, updateBo);
        BeanUtils.copyProperties(timePeriod, updateBo);
        Boolean result = signInTaskService.updateTaskByUpdateBo(updateBo);
        return result ? ResponseVo.success("更新成功") : ResponseVo.fail(EResultCode.UpdateDataFail);
    }

    @GetMapping("taskPage")
    @ApiOperation("任务分页")
    public ResponseVo<PageVo<SignInTaskPageVo>> pageTask(@ApiParam(value = "任务名称 /模糊搜索") String name,
                                                         @ApiParam(value = "已失效,") Integer taskStatus,
                                                         @ApiParam(value = "当前页码") @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                                                         @ApiParam(value = "每页记录数") @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        if (taskStatus.equals(-1)) {
            taskStatus = null;
        }
        PageVo<SignInTaskPageVo> page = signInTaskService.pageTaskVo(name, taskStatus, pageNo, pageSize);
        return ResponseVo.success(page);
    }

    @PutMapping("doEnable")
    @ApiOperation("启用/停用任务 ")
    public ResponseVo<String> doEnable(@ApiParam(value = "任务uuid") @RequestParam String uuid, Boolean enable) {
        return signInTaskService.doEnable(uuid, enable) ? ResponseVo.success("更新成功") : ResponseVo.fail(EResultCode.UpdateDataFail);
    }

    @DeleteMapping("taskDelete")
    @ApiOperation("删除任务")
    public ResponseVo<String> taskDelete(@ApiParam(value = "任务uuid") @RequestParam String uuid) {
        return signInTaskService.taskDelete(uuid) ? ResponseVo.success("更新成功") :
                ResponseVo.fail(EResultCode.DeleteDataFail);
    }

    @PostMapping("setUser")
    @ApiOperation("设置人员")
    public ResponseVo<String> setUser(@RequestBody TaskUserVo taskUserVo) {
        if (Objects.isNull(taskUserVo.getUserUuids())) {
            return ResponseVo.success("添加成功");
        }
        return signInTaskService.setUsers(taskUserVo.getUserUuids(), taskUserVo.getUuid()) ? ResponseVo.success("添加成功") :
                ResponseVo.fail(EResultCode.InsertDataFail);
    }
    @GetMapping("userPage")
    @ApiOperation("获取人员分页")
    public ResponseVo<PageVo<UserPageVo>> userPage(@ApiParam(name = "uuid", value = "任务uuid")String uuid,
                                                   @ApiParam(name = "pageNo", value = "当前页码") @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                                                   @ApiParam(name = "pageSize", value = "每页记录数") @RequestParam(required = false, defaultValue = "20")Integer pageSize,
                                                   @ApiParam("用户名") String name,
                                                   @ApiParam("架构名称")String organizationUuid){
        Assert.notNull(uuid,EResultCode.NullDataFail.getMessage());
        PageVo<UserPageVo> pageVo = signInTaskService.pageUser(uuid,name,organizationUuid, pageNo, pageSize);
        return ResponseVo.success(pageVo);
    }

    @DeleteMapping("deleteTaskUser")
    @ApiOperation("删除人员关联")
    public ResponseVo<Boolean> deleteTaskUsers(@RequestBody TaskUserVo vo) {
        Assert.notNull(vo.getUuid(), EResultCode.NullDataFail.getMessage());
        return signInTaskService.deleteTaskUser(vo.getUuid(), vo.getUserUuids()) ? ResponseVo.success() :
                ResponseVo.fail(EResultCode.DeleteDataFail);
    }

    @DeleteMapping("deleteAllUser")
    @ApiOperation("清空人员")
    public ResponseVo<Boolean> deleteAllUser(@ApiParam(value = "任务uuid") @RequestParam String uuid) {
        Assert.notNull(uuid, EResultCode.NullDataFail.getMessage());
        return signInTaskService.deleteAllUser(uuid) ? ResponseVo.success() :
                ResponseVo.fail(EResultCode.DeleteDataFail);
    }

    @DeleteMapping("deleteAllDevice")
    @ApiOperation("清空设备")
    public ResponseVo<Boolean> deleteAllDevice(@ApiParam(value = "任务uuid") @RequestParam String uuid) {
        Assert.notNull(uuid, EResultCode.NullDataFail.getMessage());
        return signInTaskService.deleteAllDevice(uuid) ? ResponseVo.success() :
                ResponseVo.fail(EResultCode.DeleteDataFail);
    }

    @GetMapping("devicePage")
    @ApiOperation("设备分页")
    public ResponseVo<PageVo<DevicePageVo>> devicePage(@ApiParam(name = "uuid", value = "任务uuid") String uuid,
                                                       @ApiParam(name = "pageNo", value = "当前页码") @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                                                       @ApiParam(name = "pageSize", value = "每页记录数") @RequestParam(required = false, defaultValue = "20") Integer pageSize,
                                                       @ApiParam("设备名称") String name,
                                                       @ApiParam("设备编号") String number,
                                                       @ApiParam("场地code") String areaCode) {
        Assert.notNull(uuid, EResultCode.NullDataFail.getMessage());
        PageVo<DevicePageVo> pageVo = signInTaskService.devicePage(uuid, pageNo, pageSize, name,
                number, areaCode);
        return ResponseVo.success(pageVo);

    }

    @DeleteMapping("deleteDevice")
    @ApiOperation("删除设备关联")
    public ResponseVo<Boolean> deleteDeviceUser(@RequestBody TaskDeviceVo vo) {
        Assert.notNull(vo.getUuid(), EResultCode.NullDataFail.getMessage());
        return signInTaskService.deleteDeviceUser(vo.getUuid(), vo.getDeviceUuids()) ? ResponseVo.success() :
                ResponseVo.fail(EResultCode.DeleteDataFail);
    }

    @PostMapping("userPage")
    @ApiOperation("查看人员")
    public ResponseVo<List<TaskUserPageVo>> userPage(@RequestBody ListVo vo) {
        if (CollectionUtils.isEmpty(vo.getUserUuids())) {
            return ResponseVo.success(Lists.newArrayList());
        }
        List<TaskUserPageVo> a = signInTaskService.userPage(vo.getUserUuids());
        return ResponseVo.success(a);
    }

}
