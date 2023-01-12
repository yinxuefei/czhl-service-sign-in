package com.gdczhl.saas.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdczhl.saas.bo.feign.user.UserVo;
import com.gdczhl.saas.controller.external.pojo.bo.SignInTaskSaveBo;
import com.gdczhl.saas.controller.external.pojo.bo.SignInTaskUpdateBo;
import com.gdczhl.saas.controller.external.pojo.vo.*;
import com.gdczhl.saas.entity.SignInTask;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gdczhl.saas.vo.PageVo;
import com.gdczhl.saas.vo.ResponseVo;
import com.gdczhl.saas.vo.feign.iot.response.DeviceInfoVo;
import io.swagger.annotations.ApiParam;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hkx
 * @since 2023-01-05
 */
public interface ISignInTaskService extends IService<SignInTask> {


    /**
     * 添加任务
     * @param saveBo
     * @return
     */
    boolean add(SignInTaskSaveBo saveBo);

    /**
     * 获取任务名称集合
     * @return
     */
    Map<String,Set<String>> getTaskNameList();

    /**
     * 根据任务uuid 添加用户
     * @param userUuids
     * @param uuid
     * @return
     */
    boolean setUsers(List<String> userUuids, String uuid);

    /**
     * 根据任务uuid 设置设备信息
     * @param deviceUuids
     * @param uuid
     * @return
     */
    boolean setDevices(List<String> deviceUuids, String uuid);

    /**
     * 根据任务uuid获取任务
     * @param uuid
     * @return
     */
    SignInTask getTaskByUuid(String uuid);

    /**
     * 根据uuids获取多个任务
     * @param uuids
     * @return key 任务uuid
     */
    Map<String,SignInTask> getTasksByUuids(List<String> uuids);

    /**
     * 修改任务信息
     * @param updateBo
     * @return
     */
    Boolean updateTaskByUpdateBo(SignInTaskUpdateBo updateBo);

    /**
     * 任务分页查询
     * @param name
     * @param taskName
     * @param pageNo
     * @param pageSize
     * @return
     */
    Page<SignInTask> pageTask(String name, String taskName, Integer pageNo, Integer pageSize);

    /**
     * 任务启用禁用
     * @param uuid
     * @param enable
     * @return
     */
    boolean doEnable(String uuid, Boolean enable);

    /**
     * 根据任务uuid获取任务vo
     * @param uuid
     * @return
     */
    SignInTaskSaveVo getTaskVoByUuid(String uuid);

    /**
     * 做任务分页(pc端显示)
     * @param name
     * @param taskName
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageVo<SignInTaskPageVo> pageTaskVo(String name, String taskName, Integer pageNo, Integer pageSize);

    /**
     * 删除任务
     * @param uuid
     * @return
     */
    boolean taskDelete(String uuid);

    /**
     * 用户分页
     * @param uuid
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageVo<UserPageVo> pageUser(String uuid, String name, String organizationName, Integer pageNo, Integer pageSize);

    /**
     * 设备分页
     * @param uuid
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageVo<DevicePageVo> devicePage(String uuid, Integer pageNo, Integer pageSize,String name,
                                   String address);

    /**
     * 删除用户
     * @param uuid
     * @param userUuids
     * @return
     */
    boolean deleteTaskUser(String uuid, List<String> userUuids);

    /**
     * 删除设备
     * @param uuid
     * @param devices
     * @return
     */
    boolean deleteDeviceUser(String uuid, List<String> devices);

    Set<String> getOrganizationNameList();

    Set<String> getAreaAddressNameList();
}
