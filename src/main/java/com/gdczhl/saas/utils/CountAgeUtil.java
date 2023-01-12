package com.gdczhl.saas.utils;

import java.util.Calendar;
import java.util.Date;

public class CountAgeUtil {

    public synchronized static Integer getAge(Date birthDay) {
        int age = 0;
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) {
            return age;
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayBirth = cal.get(Calendar.DAY_OF_MONTH);
        //年相减
        age = yearNow - yearBirth;
        //判断月份
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                //判断天
                if (dayNow < dayBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }
        return age;
    }
}
