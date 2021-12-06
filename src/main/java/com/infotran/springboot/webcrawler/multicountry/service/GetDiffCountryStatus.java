package com.infotran.springboot.webcrawler.multicountry.service;

import com.infotran.springboot.util.TimeUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

/**
 * 取得不同國家的疫情狀態
 *
 * @author chris
 */
@Slf4j
@Service
public class GetDiffCountryStatus {

    private static final String LOG_PREFIX = "GetDiffCountryStatus";

    public static final String REDIS_KEY = "global_covid_";

    public static final String FILENAME = "world";

    private static String TODAY_DATE = TimeUtil.formTodayDate();

    /** CDC WORLD Data URL */
    @Value("${CDC.WORLD.COVID}")
    public String CDC_WORLD_URL;


    /**
     * 解析各國疫情狀況<br>
     * iso_code: 1,<br>
     * 國家: 3,<br>
     * 日期: 4,<br>
     * 總確診數 5,<br>
     * 新增確診數: 6,<br>
     * 總死亡數: 8,<br>
     * 新增死亡數: 9,<br>
     * 每百萬人確診數: 11,<br>
     * 每百萬人死亡數: 12,<br>
     * 疫苗總接種人數: 21,<br>
     * 每百人接種疫苗人數: 25<br>
     *
     * @param body CDC的csv檔
     * */
    public void parseCsvInfo(@NonNull String body){
        String[] countries = body.split("\n");
        CopyOnWriteArrayList<String> column = new CopyOnWriteArrayList<String>();
        IntStream.range(0,countries.length).parallel().forEachOrdered(x -> {
            Arrays.stream(countries[x].split(",")).filter(i -> i.equals(TimeUtil.formTodayDate())).map(column::add);
            if(checkTime(column.get(4))){

            }
            column.clear();
        });

    }

    /**
     * 檢查是否為今日日期
     * */
    private Boolean checkTime(@NonNull String time){
        return (time.equals(TODAY_DATE))?true:false;
    }
}
