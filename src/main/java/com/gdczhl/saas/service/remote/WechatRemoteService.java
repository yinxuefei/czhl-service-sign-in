package com.gdczhl.saas.service.remote;

import com.gdczhl.saas.bo.feign.area.AreaBriefInfoVo;
import com.gdczhl.saas.service.remote.vo.wechat.OfficialAccountSaveVo;
import com.gdczhl.saas.service.remote.vo.wechat.OfficialAccountSendVo;
import com.gdczhl.saas.service.remote.vo.wechat.OfficialAccountVo;
import com.gdczhl.saas.vo.ResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "czhl-service-wechat", path = "/wechat/feign/officialAccount")
public interface WechatRemoteService {

    /**
     * 获取公众号信息
     * @param institutionUuid 机构uuid
     * @return
     */
    @GetMapping("get")
    ResponseVo<OfficialAccountVo> get(@RequestParam("institutionUuid") String institutionUuid);


//    /**
//     * 添加公众号模板类型
//     * @param officialAccountSaveVo
//     * @return
//     */
//    @PostMapping("templateMessage/addTemplate")
//    ResponseVo addTemplate(@RequestBody OfficialAccountSaveVo officialAccountSaveVo);


    /**
     * 批量发送
     * @param officialAccountSendVo 包含人员信息,模板信息,公众号信息
     * @return
     */
    @PostMapping("templateMessage/sendListByUser")
    ResponseVo sendListByUser(@RequestBody OfficialAccountSendVo officialAccountSendVo);
}
