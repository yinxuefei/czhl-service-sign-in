package com.gdczhl.saas.service.remote.vo.wechat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class OfficialAccountSendVo {

    private String miniprogramPagepath;
    @ApiModelProperty("公众号uuid")
    private String officialAccountUuid;
    private ParamsBean params;
    @ApiModelProperty("模板类型")
    private String templateType;
    private String url;
    @ApiModelProperty("用户集合")
    private List<String> userUuids;

    @Data
    public class ParamsBean{
        private String first;
        private String keyword1;
        private String keyword2;
        private String keyword3;
        private String remark;
    }
}
