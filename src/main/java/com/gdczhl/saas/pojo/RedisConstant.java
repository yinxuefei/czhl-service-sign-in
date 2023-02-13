package com.gdczhl.saas.pojo;

import lombok.Data;

@Data
public class RedisConstant {

    //根据任务uuid 获取统计uuid             键值对     过期明日凌晨
    public static final String STATISTICS_UUID_KEY = "sign:statistics_uuid:";

    //根据统计uuid 获取已签用户uuid           set
    public static final String USER_UUID_KEY = "sign:user_uuid:";

    //根据任务uuid 人员uuid 获取已签记录uuid    键值对
    //String key = RECORD_KEY+signInTask.getUuid()+userUuid;
    public static final String RECORD_KEY = "sign:record_uuid:";
    //根据统计uuid 获取异常体温人员
    public static final String BODY_TEMPERATURE_KEY = "sign:body_temperature_user_uuid:";

    public static String getKey(String a, String b) {
        return a + "_" + b;
    }
}
