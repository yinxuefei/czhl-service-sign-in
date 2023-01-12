package com.gdczhl.saas.utils;

/**
 * @author 隔壁老袁
 * @date 2022/9/27
 * <p>
 * 年龄分组工具
 */
public class AgeGroupUtil {

    /**
     * 获取年龄分组
     *
     * @param age
     * @return
     */
    public static int getGroup(Integer age) {
        if (age < 15) {
            return 10;
        } else if (15 <= age && age < 25) {
            return 20;
        } else if (25 <= age && age < 35) {
            return 30;
        } else if (35 <= age && age < 45) {
            return 40;
        } else if (45 <= age && age < 55) {
            return 50;
        } else {
            return 60;
        }
    }


}
