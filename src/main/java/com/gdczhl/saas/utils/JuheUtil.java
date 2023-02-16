package com.gdczhl.saas.utils;

import ch.qos.logback.core.status.Status;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Log4j2
public class JuheUtil {

    private static String REDIS_VOCATION = "vocation_";

    private final static String JUHE_KEY = "88f8dc0f8bc4292b424ca21696983363";

    /**
     * 获取本日是否节假日详情说明
     *
     * @param stringRedisTemplate
     * @return
     */
    public static JuheBean getVacation(StringRedisTemplate stringRedisTemplate, LocalDate localDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = localDate.format(dateTimeFormatter);

        String jsonStr = stringRedisTemplate.opsForValue().get(REDIS_VOCATION + date);
        if (!BaseUtil.isEmpty(jsonStr)) {
            return JSONObject.parseObject(jsonStr, JuheBean.class);
        }
        String VACATION_URL = "http://apis.juhe.cn/fapig/calendar/day.php?date=DATE&key=";
        String url = VACATION_URL.replaceAll("DATE", date) + JUHE_KEY;
        RestTemplate restTemplate = new RestTemplate();
        JuheBean result = restTemplate.getForObject(url, JuheBean.class);
        if (result == null) {
            return null;
        }
        stringRedisTemplate.opsForValue().set(REDIS_VOCATION + date, JSONObject.toJSONString(result),
                getDistanceTomorrowSeconds(localDate), TimeUnit.SECONDS);
        return result;

    }

    /**
     * 获取距离明天  00:00:00 差 多少秒
     *
     * @return
     */
    public static long getDistanceTomorrowSeconds(LocalDate date) {
        LocalDateTime tomorrowTime = LocalDateTime.of(date.plusDays(1l), LocalTime.MIN);
        long result = (tomorrowTime.toEpochSecond(ZoneOffset.of("+8")) * 1000 - System.currentTimeMillis()) / 1000;
        System.out.println(result);
        return result;
    }

}