package com.gdczhl.saas.service.remote.follback;

import com.gdczhl.saas.service.remote.IotRemoteService;
import com.gdczhl.saas.vo.PageVo;
import com.gdczhl.saas.vo.ResponseVo;
import com.gdczhl.saas.vo.feign.iot.request.DeviceSearchVo;
import com.gdczhl.saas.vo.feign.iot.response.DeviceInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class IotRemoteServiceFallback implements IotRemoteService {

    @Override
    public ResponseVo<PageVo<DeviceInfoVo>> pageByDeviceUuids(DeviceSearchVo searchVo) {
        return null;
    }

    @Override
    public ResponseVo<List<DeviceInfoVo>> getDeviceListByUuidList(List<String> userUuidList) {
        return null;
    }
}
