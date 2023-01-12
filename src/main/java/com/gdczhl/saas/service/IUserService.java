package com.gdczhl.saas.service;

import com.gdczhl.saas.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hkx
 * @since 2023-01-06
 */
public interface IUserService extends IService<User> {

    User getByUserUuid(String uuid);
}
