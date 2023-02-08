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

    User getByUserUuid(String uuid);

    Map<String, User> getByUserUuids(List<String> uuids);

}
