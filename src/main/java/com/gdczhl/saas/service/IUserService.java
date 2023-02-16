package com.gdczhl.saas.service;

import com.gdczhl.saas.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hkx
 * @since 2023-01-06
 */
public interface IUserService extends IService<User> {

    /**
     * 获取用户
     * @param uuid
     * @return
     */
    User getByUserUuid(String uuid);

    /**
     * 批量获取
     * @param uuids
     * @return key 用户uuid;  value 用户实体
     */
    Map<String, User> getByUserUuids(List<String> uuids);

}
