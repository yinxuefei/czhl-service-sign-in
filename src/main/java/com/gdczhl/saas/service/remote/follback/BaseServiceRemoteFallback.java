package com.gdczhl.saas.service.remote.follback;

import com.gdczhl.saas.bo.feign.institution.InstitutionVo;
import com.gdczhl.saas.bo.feign.organization.OrganizationVo;
import com.gdczhl.saas.bo.feign.user.UserVo;
import com.gdczhl.saas.service.remote.BaseServiceRemote;
import com.gdczhl.saas.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class BaseServiceRemoteFallback implements BaseServiceRemote {

    @Override
    public ResponseVo<List<UserVo>> listByUuidsWithGetFacePhoto(List<String> uuidList) {
        return null;
    }

    @Override
    public ResponseVo<List<UserVo>> listByUuids(List<String> uuidList) {
        return null;
    }

    @Override
    public ResponseVo<List<OrganizationVo>> listOrganizationByUuids(List<String> uuidList) {
        return null;
    }

    @Override
    public ResponseVo<UserVo> getByPhone(String institutionUuid, String phone) {
        return null;
    }

    @Override
    public ResponseVo<InstitutionVo> get(String uuid) {
        return null;
    }
}
