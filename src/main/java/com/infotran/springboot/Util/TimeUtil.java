package com.infotran.springboot.Util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class TimeUtil {

    public static Map<String, String> genTodayDate() {
        Map<String, String> dateMap = new HashMap<>();
        LocalDate now = LocalDate.now();
        dateMap.put(String.valueOf(now.getMonthValue()), String.valueOf(now.getDayOfMonth()));
        return dateMap;
    }

}
