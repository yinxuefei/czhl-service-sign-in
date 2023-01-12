package com.gdczhl.saas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gdczhl.saas.entity.User;
import com.gdczhl.saas.mapper.UserMapper;
import com.gdczhl.saas.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hkx
 * @since 2023-01-06
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public User getByUserUuid(String uuid) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<User>().eq(User::getUuid, uuid).last("limit 0,1");
        return getOne(qw);
    }
}
