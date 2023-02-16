package com.gdczhl.saas.service;

import com.gdczhl.saas.entity.Device;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hkx
 * @since 2023-01-09
 */
public interface IDeviceService extends IService<Device> {

    /**
     * 获取设备
     * @param deviceUuid 设备uuid
     * @return
     */
    Device getByDeviceUuid(String deviceUuid);
}
