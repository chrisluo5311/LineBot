package com.infotran.springboot.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * TimeUtil
 * @author Chris
 * */
@Slf4j
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

    /**
     * 今日日期
     * @return String
     * */
    public static String formForeignTodayDate(String format,Long minusDays){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        if(Objects.nonNull(minusDays)){
            log.info("minusDays:{}",formatter.format(LocalDateTime.now().minusDays(minusDays)));
            return formatter.format(LocalDateTime.now().minusDays(minusDays));
        }
        return formatter.format(LocalDateTime.now());
    }


    /**
     * 提取數字並返回日期
     * 若非日期返回null
     * @param title 下載標題
     * */
    public static String verifyDate(String title){
        Integer index = 0;
        Integer sum = 0;
        while (Character.isDigit(title.charAt(index))) {
            sum = sum*10 + Character.getNumericValue(title.charAt(index));
            index++;
        }
        return sum==0?null:String.valueOf(sum);
    }

}
