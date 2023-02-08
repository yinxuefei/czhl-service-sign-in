package com.gdczhl.saas.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum NettyCmd {
    UPDATE(408, "更新"),
    REPORT(407, "发布");

    private Integer code;
    private String desc;

    public String toCmd() {
        Map<String, Object> param = Maps.newHashMap();
        param.put("code", this.code);
        param.put("name", this.toString());
        return JSON.toJSONString(param, SerializerFeature.WriteMapNullValue);
    }

    public String toCmd(Map<String, Object> param) {
        param.put("code", this.code);
        param.put("name", this.toString());
        return JSON.toJSONString(param, SerializerFeature.WriteMapNullValue);
    }

    @Data
    public static class ResultData<T> {
        private List<T> success;
        private List<T> fail;
    }
}