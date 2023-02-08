package com.gdczhl.saas.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdczhl.saas.pojo.bo.signInTask.SignInTaskSaveBo;
import com.gdczhl.saas.pojo.bo.signInTask.SignInTaskUpdateBo;
import com.gdczhl.saas.pojo.bo.signInTask.TaskUserPageVo;
import com.gdczhl.saas.pojo.vo.DevicePageVo;
import com.gdczhl.saas.pojo.vo.UserPageVo;
import com.gdczhl.saas.pojo.vo.signInTask.SignInTaskPageVo;
import com.gdczhl.saas.pojo.vo.signInTask.SignInTaskSaveVo;
import com.gdczhl.saas.pojo.vo.signInTask.SignInTaskVo;
import com.gdczhl.saas.pojo.vo.signInTask.TaskNameVo;
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
     * 删除设备
     *
     * @param uuid
     * @param devices
     * @return
     */
    boolean deleteDeviceUser(String uuid, List<String> devices);

    Set<String> getOrganizationNameList();

    Set<String> getAreaAddressNameList();

    boolean deleteAllUser(String uuid);

    boolean deleteAllDevice(String uuid);

    List<TaskUserPageVo> userPage(List<String> userUuids);

    List<SignInTask> getTodayTasks(LocalDate date,String deviceUuid);

    List<SignInTask> getManageTodayTasks(LocalDate date,String operatorUuid);

    List<SignInTask> getUserTodayTasks(LocalDate date,String operatorUuid);
}
