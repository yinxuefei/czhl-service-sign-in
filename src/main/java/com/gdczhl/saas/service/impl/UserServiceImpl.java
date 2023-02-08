package com.gdczhl.saas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gdczhl.saas.entity.User;
import com.gdczhl.saas.mapper.UserMapper;
import com.gdczhl.saas.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hkx
 * @since 2023-01-06
 */
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public User getByUserUuid(String uuid) {
        if (uuid == null) {
            throw new RuntimeException("参数为空");
        }
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<User>().eq(User::getUuid, uuid).last("limit 0,1");
        return getOne(qw);
    }

    @Override
    public Map<String, User> getByUserUuids(List<String> uuids) {
        if (uuids == null) {
            throw new RuntimeException("参数为空");
        }
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(User::getUuid, uuids);
        HashMap<String, User> hashMap = new HashMap<>();
        for (User user : list(lambdaQueryWrapper)) {
            hashMap.put(user.getUuid(), user);
        }
        return hashMap;
    }
}
