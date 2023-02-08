package com.gdczhl.saas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gdczhl.saas.entity.Device;
import com.gdczhl.saas.mapper.DeviceMapper;
import com.gdczhl.saas.service.IDeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hkx
 * @since 2023-01-09
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements IDeviceService {

    @Override
    public Device getByDeviceUuid(String deviceUuid) {
        LambdaQueryWrapper<Device> eq = new LambdaQueryWrapper<>();
        eq.eq(Device::getUuid, deviceUuid).last("limit 0,1");

        return getOne(eq);
    }
}
