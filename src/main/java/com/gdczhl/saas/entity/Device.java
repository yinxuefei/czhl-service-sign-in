package com.gdczhl.saas.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author hkx
 * @since 2023-01-06
 */
@Getter
@Setter
public class Device extends BaseEntity {

    /**
     * 机构uuid
     */
    private String institutionUuid;

    /**
     * 名称
     */
    private String name;

    /**
     * 设备sn码
     */
    private String numberSn;

    /**
     * 设备编号
     */
    private String number;

    /**
     * 地址
     */
    private String areaAddress;

    /**
     * 是否启用
     */
    private String enable;

}
