package com.gdczhl.saas.controller.external;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdczhl.saas.bo.feign.user.UserVo;
import com.gdczhl.saas.controller.external.pojo.DatePeriod;
import com.gdczhl.saas.controller.external.pojo.MoreConfig;
import com.gdczhl.saas.controller.external.pojo.bo.SignInTaskSaveBo;
import com.gdczhl.saas.controller.external.pojo.TimePeriod;
import com.gdczhl.saas.controller.external.pojo.bo.SignInTaskUpdateBo;
import com.gdczhl.saas.controller.external.pojo.vo.*;
import com.gdczhl.saas.enums.EResultCode;
import com.gdczhl.saas.service.ISignInTaskService;
import com.gdczhl.saas.utils.CzBeanUtils;
import com.gdczhl.saas.vo.PageVo;
import com.gdczhl.saas.vo.ResponseVo;
import com.gdczhl.saas.vo.feign.iot.response.DeviceInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hkx
 * @since 2023-01-05
 */
@Api(tags = "签到任务管理")
@Slf4j
@RestController
@RequestMapping("external/signInTask")
public class SignInTaskController {

    @Autowired
    private ISignInTaskService signInTaskService;


    @PostMapping("add")
    @ApiOperation("添加任务")
    public ResponseVo<String> addTask(@RequestBody SignInTaskSaveVo saveVo){
        Assert.notNull(saveVo,"任务为空,添加失败");
        //新增任务默认启用
        saveVo.setStatus(true);
        //封装bo
        SignInTaskSaveBo saveBo = new SignInTaskSaveBo();
        BeanUtils.copyProperties(saveVo,saveBo);
        DatePeriod datePeriod = saveVo.getDatePeriod();
        TimePeriod timePeriod = saveVo.getTimePeriod();
        BeanUtils.copyProperties(datePeriod,saveBo);
        BeanUtils.copyProperties(timePeriod,saveBo);
        return signInTaskService.add(saveBo)?ResponseVo.success("添加成功"):ResponseVo.fail(EResultCode.InsertDataFail);
    }

    @PostMapping("setDevice")
    @ApiOperation("设置终端")
    public ResponseVo<String> setDevice(@ApiParam(name = "devUuids",value = "请求体 :设备uuid") @RequestBody List<String> deviceUuids,
                                      @ApiParam(name = "uuid",value = "请求头: 任务uuid") @RequestParam("uuid")String uuid){
        if (Objects.isNull(deviceUuids)) {
            return ResponseVo.success("添加成功");
        }
        return signInTaskService.setDevices(deviceUuids,uuid)?ResponseVo.success("添加成功"):
                ResponseVo.fail(EResultCode.InsertDataFail);
    }

    @GetMapping("getTaskNameList")
    @ApiOperation("获取签到任务名称")
    public ResponseVo<Map<String,Set<String>>> getTaskNameList(){
        return ResponseVo.success(signInTaskService.getTaskNameList());
    }

    @GetMapping("getOrganizationNameList")
    @ApiOperation("获取人员组织架构名称")
    public ResponseVo<Set<String>> getOrganizationNameList(){
        return ResponseVo.success(signInTaskService.getOrganizationNameList());
    }

    @GetMapping("getAreaAddressNameList")
    @ApiOperation("获取设备场地架构名称")
    public ResponseVo<Set<String>> getAreaAddressNameList(){
        return ResponseVo.success(signInTaskService.getAreaAddressNameList());
    }

    @GetMapping("getTaskByUuid")
    @ApiOperation("根据uuid获取单个任务")
    public ResponseVo<SignInTaskSaveVo> getTaskVoByUuid(@ApiParam(value = "任务uuid")String uuid){
        Assert.notNull(uuid,"参数为空");
        return ResponseVo.success(signInTaskService.getTaskVoByUuid(uuid));
    }

    @PutMapping ("updateTask")
    @ApiOperation("更新任务信息")
    public ResponseVo<String> updateTaskByUuid(@ApiParam(value = "签到任务")@RequestBody SignInTaskUpdateVo updateVo){
        SignInTaskUpdateBo updateBo = CzBeanUtils.copyProperties(updateVo, SignInTaskUpdateBo::new);
        DatePeriod datePeriod = updateVo.getDatePeriod();
        TimePeriod timePeriod = updateVo.getTimePeriod();
        BeanUtils.copyProperties(datePeriod,updateBo);
        BeanUtils.copyProperties(timePeriod,updateBo);
        Boolean result = signInTaskService.updateTaskByUpdateBo(updateBo);
        return result?ResponseVo.success("更新成功"):ResponseVo.fail(EResultCode.UpdateDataFail);
    }

    @GetMapping("taskPage")
    @ApiOperation("获取任务分页")
    public ResponseVo<PageVo<SignInTaskPageVo>> pageTask(@ApiParam(name = "name", value = "任务名称")String name,
                                       @ApiParam(name = "taskName", value = "签到任务名称")String taskName,
                                       @ApiParam(name = "pageNo", value = "当前页码") @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                                       @ApiParam(name = "pageSize", value = "每页记录数") @RequestParam(required = false, defaultValue = "20")Integer pageSize){
        PageVo<SignInTaskPageVo> page = signInTaskService.pageTaskVo(name, taskName, pageNo, pageSize);
        return ResponseVo.success(page);

    }

    @PutMapping("doEnable")
    @ApiOperation("任务 启用/停用")
    public ResponseVo<String> doEnable(@ApiParam(name = "uuid", value = "任务uuid")String uuid,Boolean enable){
        return signInTaskService.doEnable(uuid,enable)?ResponseVo.success("更新成功"):ResponseVo.fail(EResultCode.UpdateDataFail);
    }

    @DeleteMapping("taskDelete")
    @ApiOperation("任务 删除")
    public ResponseVo<String> taskDelete(@ApiParam(name = "uuid", value = "任务uuid")String uuid,Boolean enable){
        return signInTaskService.taskDelete(uuid)?ResponseVo.success("更新成功"):
                ResponseVo.fail(EResultCode.DeleteDataFail);
    }

    @PostMapping("setUser")
    @ApiOperation("设置人员")
    public ResponseVo<String> setUser(@ApiParam(name = "userUuids",value = "请求体: 用户uuid ") @RequestBody List<String> userUuids,
                                      @ApiParam(name = "uuid",value = "请求头: 任务uuid") @RequestParam("uuid")String uuid){
        if (Objects.isNull(userUuids)) {
            return ResponseVo.success("添加成功");
        }
        return signInTaskService.setUsers(userUuids,uuid)?ResponseVo.success("添加成功"):
                ResponseVo.fail(EResultCode.InsertDataFail);
    }
    @GetMapping("userPage")
    @ApiOperation("获取人员分页")
    public ResponseVo<PageVo<UserPageVo>> userPage(@ApiParam(name = "uuid", value = "任务uuid")String uuid,
                                                   @ApiParam(name = "pageNo", value = "当前页码") @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                                                   @ApiParam(name = "pageSize", value = "每页记录数") @RequestParam(required = false, defaultValue = "20")Integer pageSize,
                                                   @ApiParam("用户名") String name,
                                                   @ApiParam("架构名称")String organizationName){
        Assert.notNull(uuid,EResultCode.NullDataFail.getMessage());
        PageVo<UserPageVo> pageVo = signInTaskService.pageUser(uuid,name,organizationName, pageNo, pageSize);
        return ResponseVo.success(pageVo);
    }

    @DeleteMapping("deleteTaskUser")
    @ApiOperation("删除人员关联")
    public ResponseVo<Boolean> deleteTaskUsers(@ApiParam(name = "uuid", value = "任务uuid")String uuid,@ApiParam(name =
            "userUuids",value = "用户uuids") List<String> userUuids){
        Assert.notNull(uuid,EResultCode.NullDataFail.getMessage());
        return signInTaskService.deleteTaskUser(uuid,userUuids)?ResponseVo.success():
                ResponseVo.fail(EResultCode.DeleteDataFail);
    }


    @GetMapping("devicePage")
    @ApiOperation("获取设备分页")
    public ResponseVo<PageVo<DevicePageVo>> devicePage(@ApiParam(name = "uuid", value = "任务uuid")String uuid,
                                                       @ApiParam(name = "pageNo", value = "当前页码") @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                                                       @ApiParam(name = "pageSize", value = "每页记录数") @RequestParam(required = false, defaultValue = "20")Integer pageSize,
                                                       @ApiParam("设备名称") String name,
                                                       @ApiParam("场地名称")String address){
        Assert.notNull(uuid,EResultCode.NullDataFail.getMessage());
        PageVo<DevicePageVo> pageVo = signInTaskService.devicePage(uuid, pageNo, pageSize,name,
                address);
        return ResponseVo.success(pageVo);

    }

    @DeleteMapping("deleteDeviceUser")
    @ApiOperation("删除设备关联")
    public ResponseVo<Boolean> deleteDeviceUser(@ApiParam(name = "uuid", value = "任务uuid")String uuid,@ApiParam(name = "userUuids",value = "用户uuids") List<String> userUuids){
        Assert.notNull(uuid,EResultCode.NullDataFail.getMessage());
        return signInTaskService.deleteDeviceUser(uuid,userUuids)?ResponseVo.success():ResponseVo.fail(EResultCode.DeleteDataFail);
    }


}
