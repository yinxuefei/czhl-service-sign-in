package com.gdczhl.saas.utils;

import lombok.Data;
import lombok.Setter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimeUtil {

    public static final SimpleDateFormat DATE_UTIL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter LOCAL_DATE_TIME_UTIL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter LOCAL_DATE_UTIL = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static DateTimeFormatter LOCAL_TIME_UTIL = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void setLocalTimeUtil(DateTimeFormatter localTimeUtil) {
        LOCAL_TIME_UTIL = localTimeUtil;
    }

    public static String format(Date date) {
        return DATE_UTIL.format(date);
    }

    public static String format(LocalDate date) {
        return LOCAL_DATE_UTIL.format(date);
    }

    public static String format(LocalTime time) {
        return LOCAL_TIME_UTIL.format(time);
    }

    public static String format(LocalDateTime localDateTime) {
        return LOCAL_DATE_TIME_UTIL.format(localDateTime);
    }

    public static Date parseDate(String date) {
        try {
            return DATE_UTIL.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static LocalDate parseLocalDate(String date) {
        return LocalDate.parse(date, LOCAL_DATE_UTIL);
    }

    public static LocalTime parseLocalTime(String date) {
        return LocalTime.parse(date, LOCAL_TIME_UTIL);
    }

    public static LocalDateTime parseLocalDateTime(String date) {
        return LocalDateTime.parse(date, LOCAL_DATE_TIME_UTIL);
    }

    public static List<LocalDate> getPeriodDate(LocalDate taskStartDate, LocalDate taskEndDate) {
        ArrayList<LocalDate> localDates = new ArrayList<>();

        while (taskStartDate.isBefore(taskEndDate)) {
            localDates.add(taskStartDate);
            taskStartDate = taskStartDate.plusDays(1);
        }
        localDates.add(taskEndDate);

        return localDates;
    }

}
