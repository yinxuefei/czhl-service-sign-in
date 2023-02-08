package com.gdczhl.saas.service.remote;

import com.gdczhl.saas.bo.feign.area.AreaBriefInfoVo;
import com.gdczhl.saas.mq.messages.areaPlus.UserDeviceFlagSaveListBo;
import com.gdczhl.saas.vo.PageVo;
import com.gdczhl.saas.vo.ResponseVo;
import com.gdczhl.saas.vo.feign.iot.request.DeviceSearchVo;
import com.gdczhl.saas.vo.feign.iot.response.DeviceInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "czhl-service-area", path = "/area/feign")
public interface AreaRemoteService {

    /**
     * @return
     */
    @GetMapping("findInfoByUuid")
    ResponseVo<AreaBriefInfoVo> findInfoByUuid(@RequestParam("uuid") String uuid);

}
