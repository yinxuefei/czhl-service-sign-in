package com.gdczhl.saas.mq;

import com.gdczhl.saas.enums.UserDeviceFlagsEnum;
import com.gdczhl.saas.mq.messages.areaPlus.UserDeviceFlagSaveListBo;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

//人脸下发
@Component
public class SyncProducer {

    @Autowired
    RocketMQTemplate mqTemplate;

    public void addOrDeleteUserDeviceFlagSaveList(List<String> DeviceUuids, List<String> userUuids, Integer code) {
        // 定义消息体
        UserDeviceFlagSaveListBo bo = new UserDeviceFlagSaveListBo();
        bo.setDeviceUuids(DeviceUuids);
        bo.setUserUuids(userUuids);
        bo.setUserDeviceFlags(UserDeviceFlagsEnum.SIGN_IN.getType());
        bo.setType(code);
        // 发送MQ消息
        mqTemplate.convertAndSend("area-plus-user-device-flag-save-remove", bo);
    }

}