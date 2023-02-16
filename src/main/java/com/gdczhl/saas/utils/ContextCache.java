package com.gdczhl.saas.utils;

import com.gdczhl.saas.interceptor.HeaderInterceptor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ContextCache implements Serializable {

    private static final long serialVersionUID = 2136539028591849277L;

    // 使用 ThreadLocal 缓存上下文信息
    public static final ThreadLocal<Map<String, Object>> CACHE = new ThreadLocal<>();

    /**
     * 放数据
     * @param sourceKey
     */
    public static final void putAttribute(String sourceKey, Object value) {
        Map<String, Object> cacheMap = CACHE.get();
        if (null == cacheMap) {
            cacheMap = new HashMap<>();
        }
        cacheMap.put(sourceKey, value);
        CACHE.set(cacheMap);
    }

    /**
     * 拿数据
     * @param sourceKey
     */
    public static final Object getAttribute(String sourceKey) {
        Map<String, Object> cacheMap = CACHE.get();
        if (null == cacheMap) {
            return null;
        }
        return cacheMap.get(sourceKey);
    }

    public static final void remove() {
        CACHE.remove();
    }


    public static final String getOperatorUuid() {
        String operatorUuid = (String) getAttribute(HeaderInterceptor.OPERATOR_UUID);
        return operatorUuid;
    }

    public static final String getInstitutionUuid() {
        String institutionUuid = (String) getAttribute(HeaderInterceptor.INSTITUTION_UUID);
        return institutionUuid;
    }

}
