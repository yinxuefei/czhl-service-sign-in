package com.gdczhl.saas.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.gdczhl.saas.utils.ContextCache;
import com.gdczhl.saas.interceptor.HeaderInterceptor;
import com.gdczhl.saas.utils.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

//自动填充功能
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "version", () -> 1, Integer.class);
        this.strictInsertFill(metaObject, "deleted", () -> 0, Integer.class);
        this.strictInsertFill(metaObject, "uuid", IdUtils::getUUID, String.class);

        String operatorUuid = (String) ContextCache.getAttribute(HeaderInterceptor.OPERATOR_UUID);
        if (!StringUtils.isEmpty(operatorUuid)) {
            this.strictInsertFill(metaObject, "creator", () -> operatorUuid, String.class);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 起始版本 3.3.3(推荐)
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
        String operatorUuid = (String) ContextCache.getAttribute(HeaderInterceptor.OPERATOR_UUID);
        if (!StringUtils.isEmpty(operatorUuid)) {
            this.strictUpdateFill(metaObject, "editor", () -> operatorUuid, String.class);
        }
    }
}
