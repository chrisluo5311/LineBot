package com.infotran.springboot.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chris
 * */
@Component
public class TimeUtil {

    private TimeUtil(){
    }

    /**
     * 月份與日期
     * @return Map
     *
     * */
    public static Map<String, String> genTodayDate() {
        Map<String, String> dateMap = new HashMap<>();
        // YYYY-MM-DD
        LocalDate now = LocalDate.now();
        dateMap.put(String.valueOf(now.getMonthValue()), String.valueOf(now.getDayOfMonth()));
        return dateMap;
    }

    /**
     * UTC時間戳(秒)
     * @return String
     */
    public static String getUTCTimeStrInSeconds(){
        return String.valueOf(Instant.now().getEpochSecond());
    }

    /**
     * 今日日期(YYYY-MM-DD)
     * @return String
     * */
    public static String formTodayDate() {
        //YYYY-MM-DD
        LocalDate now = LocalDate.now();
        return now.toString();
    }

    public static String formForeignTodayDate(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/DD/YYYY");
        LocalDate now = LocalDate.now();
        return now.format(formatter).toString();
    }


}
