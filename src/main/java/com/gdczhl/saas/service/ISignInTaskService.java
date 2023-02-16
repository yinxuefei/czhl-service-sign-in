package com.gdczhl.saas.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdczhl.saas.service.bo.task.SignInTaskSaveBo;
import com.gdczhl.saas.service.bo.task.SignInTaskUpdateBo;
import com.gdczhl.saas.controller.external.vo.task.TaskUserPageVo;
import com.gdczhl.saas.controller.external.vo.task.DevicePageVo;
import com.gdczhl.saas.controller.external.vo.task.UserPageVo;
import com.gdczhl.saas.controller.external.vo.task.SignInTaskPageVo;
import com.gdczhl.saas.controller.external.vo.task.SignInTaskVo;
import com.gdczhl.saas.controller.external.vo.task.TaskNameVo;
import com.gdczhl.saas.entity.SignInTask;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gdczhl.saas.vo.PageVo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hkx
 * @since 2023-01-05
 */
public interface ISignInTaskService extends IService<SignInTask> {


    /**
     * 添加任务
     *
     * @param saveBo
     * @return
     */
    boolean add(SignInTaskSaveBo saveBo);

    /**
     * 获取任务名称集合
     *
     * @return
     */
    List<TaskNameVo> getTaskNameList(Integer status);

    /**
     * 根据任务uuid 添加用户
     *
     * @param userUuids
     * @param uuid
     * @return
     */
    boolean setUsers(List<String> userUuids, String uuid);

    /**
     * 根据任务uuid 设置设备信息
     *
     * @param deviceUuids
     * @param uuid
     * @return
     */
    boolean setDevices(List<String> deviceUuids, String uuid);

    /**
     * 根据任务uuid获取任务
     *
     * @param uuid
     * @return
     */
    SignInTask getTaskByUuid(String uuid);

    /**
     * 根据uuids获取多个任务
     *
     * @param uuids
     * @return key 任务uuid
     */
    Map<String, SignInTask> getTasksByUuids(List<String> uuids);

    /**
     * 修改任务信息
     *
     * @param updateBo
     * @return
     */
    Boolean updateTaskByUpdateBo(SignInTaskUpdateBo updateBo);

    /**
     * 任务分页查询
     *
     * @param name
     * @param pageNo
     * @param pageSize
     * @return
     */
    Page<SignInTask> pageTask(String name, Integer type, Integer pageNo, Integer pageSize);

    /**
     * 任务启用禁用
     *
     * @param uuid
     * @param enable
     * @return
     */
    boolean doEnable(String uuid, Boolean enable);

    /**
     * 根据任务uuid获取任务vo
     *
     * @param uuid
     * @return
     */
    SignInTaskVo getTaskVoByUuid(String uuid);

    /**
     * 做任务分页(pc端显示)
     *
     * @param name
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageVo<SignInTaskPageVo> pageTaskVo(String name, Integer taskStatus, Integer pageNo, Integer pageSize);

    /**
     * 删除任务
     *
     * @param uuid
     * @return
     */
    boolean taskDelete(String uuid);

    /**
     * 用户分页
     *
     * @param uuid
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageVo<UserPageVo> pageUser(String uuid, String name, String organizationUuid, Integer pageNo, Integer pageSize);

    /**
     * 设备分页
     *
     * @param uuid
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageVo<DevicePageVo> devicePage(String uuid, Integer pageNo, Integer pageSize, String name,
                                    String number, String areaCode);

    /**
     * 删除用户
     *
     * @param uuid
     * @param userUuids
     * @return
     */
    boolean deleteTaskUser(String uuid, List<String> userUuids);

    /**
     * 删除设备关联
     * @param uuid
     * @param devices
     * @return
     */
    boolean deleteDeviceUser(String uuid, List<String> devices);

    @Deprecated
    Set<String> getOrganizationNameList();

    @Deprecated
    Set<String> getAreaAddressNameList();

    /**
     * 清空用户
     * @param uuid
     * @return
     */
    boolean deleteAllUser(String uuid);
    /**
     * 清空设备
     * @param uuid
     * @return
     */
    boolean deleteAllDevice(String uuid);

    /**
     * 用户分页
     * @param userUuids
     * @return
     */
    List<TaskUserPageVo> userPage(List<String> userUuids);

    /**
     * 获取今日任务
     * 设备uuid非必传
     * @param date 日期
     * @param deviceUuid 设备uuid
     * @return
     */
    List<SignInTask> getTodayTasks(LocalDate date, String deviceUuid);

    /**
     * 获取包含负责人在内的每日任务
     * @param date
     * @param managerUuid
     * @return
     */
    List<SignInTask> getManageTodayTasks(LocalDate date, String managerUuid);

    /**
     * 获取包含推送人在内的每日任务
     * @param date
     * @param operatorUuid
     * @return
     */
    List<SignInTask> getUserTodayTasks(LocalDate date, String operatorUuid);
}
