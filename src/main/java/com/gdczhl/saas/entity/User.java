package com.gdczhl.saas.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.gdczhl.saas.enums.GenderEnum;
import com.gdczhl.saas.enums.UserType;
import lombok.Data;


/**
 * <p>
 * 
 * </p>
 *
 * @author hkx
 * @since 2023-01-05
 */

@Data
@TableName("user")
public class User extends BaseEntity {

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别,0:女,1,男
     */
    private GenderEnum gender;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 照片url
     */
    private String photo;

    /**
     * 头像
     */
    private String headUrl;

    /**
     * 昵称
     */
    private String nickname;

    private String organizationUuid;

    private String organizationName;
}
