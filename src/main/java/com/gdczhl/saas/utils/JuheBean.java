package com.gdczhl.saas.utils;

import lombok.Data;

@Data
public class JuheBean {

    private String reason;

    private JuheDay result;

    private Integer error_code;


    @Data
    public class JuheDay{

        //日期
        private String date;

        // 星期几
        private String week;

        // 节假日 工作日  周末
        private String statusDesc;

        // 当天状态标识，1:节假日，2:工作日，null：周末或工作日(可根据week进行判断，也可以直接根据statusDesc进行判断)
        private Integer status;

    }

}