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
import java.util.concurrent.TimeUnit;

@Log4j2
public class JuheUtil {

    private static String REDIS_VOCATION = "vocation";

    private final static String JUHE_KEY = "88f8dc0f8bc4292b424ca21696983363";

    /**
     * 获取本日是否节假日详情说明
     * @param stringRedisTemplate
     * @return
     */
    public static JuheBean getVacation(StringRedisTemplate stringRedisTemplate){
        String jsonStr = stringRedisTemplate.opsForValue().get(REDIS_VOCATION);
        if(!BaseUtil.isEmpty(jsonStr)){
            return JSONObject.parseObject(jsonStr, JuheBean.class);
        }
        String VACATION_URL = "http://apis.juhe.cn/fapig/calendar/day.php?date=DATE&key=";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(System.currentTimeMillis());
        String url = VACATION_URL.replaceAll("DATE",date) + JUHE_KEY;
        RestTemplate restTemplate = new RestTemplate();
        JuheBean result = restTemplate.getForObject(url, JuheBean.class);
        if(result==null){
            return null;
        }
        stringRedisTemplate.opsForValue().set(REDIS_VOCATION,JSONObject.toJSONString(result),getDistanceTomorrowSeconds(),TimeUnit.SECONDS);
        return result;

    }

    /**
     * 获取距离明天  00:00:00 差 多少秒
     * @return
     */
    private static long getDistanceTomorrowSeconds(){
        LocalDateTime tomorrowTime = LocalDateTime.of(LocalDate.now().plusDays(1l), LocalTime.MIN);
        long result = (tomorrowTime.toEpochSecond(ZoneOffset.of("+8")) * 1000 - System.currentTimeMillis()) / 1000;
        System.out.println(result);
        return result;
    }

    public static void main(String[] args) {
        getDistanceTomorrowSeconds();
//        Date date = new Date( );
//        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
//        System.out.println(df.format(date));
    }

}