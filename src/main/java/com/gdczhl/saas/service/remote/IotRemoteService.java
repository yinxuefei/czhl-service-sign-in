package com.gdczhl.saas.service.remote;

import com.gdczhl.saas.vo.PageVo;
import com.gdczhl.saas.vo.ResponseVo;
import com.gdczhl.saas.vo.feign.iot.request.DeviceSearchVo;
import com.gdczhl.saas.vo.feign.iot.response.DeviceInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "czhl-service-iot", path = "/iot/feign")
public interface IotRemoteService {

    /**
     * 设备列表
     * @param searchVo
     * @return
     */
    @PostMapping("/device/pageByDeviceUuids")
    ResponseVo<PageVo<DeviceInfoVo>> pageByDeviceUuids(@RequestBody DeviceSearchVo searchVo);

    /**
     * 批量获取
     * @param userUuidList
     * @return
     */
    @PostMapping("/device/getDeviceListByUuidList")
    ResponseVo<List<DeviceInfoVo>> getDeviceListByUuidList(@RequestBody List<String> devUuidList);

}
