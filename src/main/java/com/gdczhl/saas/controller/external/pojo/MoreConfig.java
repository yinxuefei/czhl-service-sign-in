package com.gdczhl.saas.controller.external.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@ApiModel("更多设置")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoreConfig {

    @ApiModelProperty("自动运行  默认开启")
    private Boolean autoRun;

    @ApiModelProperty("任务负责人")
    private Manager manager;

    @ApiModelProperty("汇报推送")
    private ReportPush reportPush;

    @ApiModelProperty("是否允许补签")
    private Resign resign;

    @ApiModelProperty("签到定位")
    private Location location;

    @ApiModelProperty("温度推送")
    private BodyTemperature bodyTemperature;


    @ApiModel("任务负责人")
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Manager {
        @ApiModelProperty("是否添加负责人")
        private Boolean isManager;

        @ApiModelProperty("责任人")
        private List<String> managerUuids;
    }

    @ApiModel("汇报推送")
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReportPush {

        @ApiModelProperty("是否汇报推送")
        private Boolean isReportPush;

        @ApiModelProperty("推送人")
        private List<String> pusherUuids;

    }

    @ApiModel("体温推送")
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BodyTemperature {
        @ApiModelProperty("是否添加推送人")
        private Boolean isPush;

        @ApiModelProperty("推送人")
        private List<String> pushUuids;
    }

    @ApiModel("补签")
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Resign {

        @ApiModelProperty("是否允许补签")
        private Boolean isResign;

        @ApiModelProperty("是否允许本人补签")
        private Boolean isMeResign;

        @ApiModelProperty("是否允许老师补签")
        private Boolean isTeacherResign;

        //签到结束后多久时间内可以补签
        @ApiModelProperty("允许补签时效")
        @JsonFormat(pattern = "HH:mm")
        private LocalTime resignTime;
    }

    @ApiModel("签到定位")
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Location {

        @ApiModelProperty("是否签到定位")
        private Boolean isLocation;

        @ApiModelProperty("签到定位地点")
        private String name;

        @ApiModelProperty("打卡范围")
        private Integer scope;
    }


}

