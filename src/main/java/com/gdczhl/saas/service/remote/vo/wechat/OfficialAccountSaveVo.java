package com.gdczhl.saas.service.remote.vo.wechat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OfficialAccountSaveVo {

    @ApiModelProperty("公众号uuid")
    private String officialAccountUuid;
    @ApiModelProperty("公众号uuid")
    private String templateId;
    @ApiModelProperty("消息模板类型")
    private String templateType;

}
