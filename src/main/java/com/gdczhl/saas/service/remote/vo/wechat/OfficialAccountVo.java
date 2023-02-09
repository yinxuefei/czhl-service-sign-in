package com.gdczhl.saas.service.remote.vo.wechat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OfficialAccountVo {

    @ApiModelProperty("公众号appid")
    private String appid;
    @ApiModelProperty("创建时间")
    private String createTime;
    @ApiModelProperty("创建人")
    private String creator;
    @ApiModelProperty("修改人")
    private String editor;
    @ApiModelProperty("机构名")
    private String institutionName;
    @ApiModelProperty("机构uuid")
    private String institutionUuid;
    @ApiModelProperty("公众号是否绑定了小程序")
    private boolean isBandMiniapp;
    @ApiModelProperty("公众号名")
    private String name;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("公众号secret")
    private String secret;
    @ApiModelProperty("启动状态 0启动 1停用")
    private int status;
    @ApiModelProperty("公众号uuid")
    private String updateTime;
    @ApiModelProperty("公众号uuid")
    private String uuid;
}
