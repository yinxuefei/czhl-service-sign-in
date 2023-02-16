package com.gdczhl.saas.service.remote;

import com.gdczhl.saas.bo.feign.institution.InstitutionVo;
import com.gdczhl.saas.bo.feign.organization.OrganizationVo;
import com.gdczhl.saas.bo.feign.user.UserVo;
import com.gdczhl.saas.vo.ResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author HKX
 */
@FeignClient(name = "czhl-service-base", path = "/base/feign")
public interface BaseServiceRemote {

    //根据用户uuid列表获取用户信息（如果有需求要获取用户头像的用这个）
    @PostMapping(value = "/user/listByUuidsWithGetFacePhoto")
    ResponseVo<List<UserVo>> listByUuidsWithGetFacePhoto(@RequestBody List<String> uuidList);


    //用户信息，如果没有获取头像需求，则继续调用这个
    @PostMapping(value = "/user/listByUuids")
    ResponseVo<List<UserVo>> listByUuids(@RequestBody List<String> uuidList);


    //根据uuid列表查询班级
    @PostMapping("/organization/listByUuids")
    ResponseVo<List<OrganizationVo>> listOrganizationByUuids(@RequestBody List<String> uuidList);

    //根据用户手机号码获取用户信息
    @GetMapping(value = "/user/getByPhone")
    ResponseVo<UserVo> getByPhone(@RequestParam(value = "institutionUuid") String institutionUuid,
                                  @RequestParam(value = "phone") String phone);

    //根据机构uuid获取机构信息
    @GetMapping("/institution/get")
    ResponseVo<InstitutionVo> get(@RequestParam(value = "uuid") String uuid);

    /**
     * 获取该组织架构下的其他组织uuids
     * @param parentUuid 当前组织uuid
     * @return
     */
    @GetMapping("organization/listTeacherOrganizationsByParentUuid")
    ResponseVo<List<OrganizationVo>> listTeacherOrganizationsByParentUuid(@RequestParam(value = "parentUuid") String parentUuid);

}
