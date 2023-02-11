package com.gdczhl.saas.controller.external;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.List;

@Data
public class PageUserVo {
    @ApiParam(value = "用户uuid")
    private List<String> uuid;
    @ApiParam(value = "当前页码", defaultValue = "1")
    private Integer pageNo;
    @ApiParam(value = "每页记录数", defaultValue = "20")
    private Integer pageSize;
    @ApiParam("用户名")
    private String name;
    @ApiParam("架构uuid")
    private String organizationUuid;
}
