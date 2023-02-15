package com.gdczhl.saas.netty.remote;

import com.gdczhl.saas.netty.CmdRequest;
import com.gdczhl.saas.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

@FeignClient(name = "netty-cmd",url = "${netty.service.url}")
public interface INettyServiceRemote {

    /**
     * 在线设备
     *
     * @return sn码
     */
    @GetMapping("online")
    Set<String> getOnlineDevice();

    /**
     * 下发命令接口
     *
     * @param cmdRequest 目标设备列表 命令内容，需要与终端开发者协商
     * @return
     */
    @PostMapping("cmd")
    ResponseVo cmd(@RequestBody CmdRequest cmdRequest);

    /**
     * 下发命令接口(有结果返回)
     *
     * @param cmdRequest 目标设备列表 命令内容，需要与终端开发者协商
     * @return
     */
    @PostMapping("cmdWithResponse")
    ResponseVo cmdWithResponse(@RequestBody CmdRequest cmdRequest);
}
