package com.gdczhl.saas.service.remote;

import com.gdczhl.saas.vo.PageVo;
import com.gdczhl.saas.vo.ResponseVo;
import com.gdczhl.saas.vo.feign.iot.request.DeviceSearchVo;
import com.gdczhl.saas.vo.feign.iot.response.DeviceInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "czhl-service-iot",url = "http://192.168.10.251:18080",path = "/iot/feign")
public interface IotRemoteService {

    /**
     * 设备列表
     *
     * @param searchVo
     * @return
     */
    @PostMapping("/device/pageByDeviceUuids")
    ResponseVo<PageVo<DeviceInfoVo>> pageByDeviceUuids(@RequestBody DeviceSearchVo searchVo);


    @PostMapping("/device/getDeviceListByUuidList")
    ResponseVo<List<DeviceInfoVo>> getDeviceListByUuidList(@RequestBody List<String> userUuidList);

}
