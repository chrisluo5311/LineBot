package com.infotran.springboot.webcrawler.multicountry.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    /** CDC WORLD Data URL */
    @Value("${CDC.WORLD.COVID}")
    public String CDC_WORLD_URL;

    /**
     * 解析各國疫情狀況
     * iso_code: 1, 國家: 3, 日期: 4,總確診數 5,新增確診數 6
     * 總死亡數: 8,新增死亡數: 9,每百萬人確診數: 11,每百萬人死亡數: 12
     * 疫苗總接種人數: 21,每百人接種疫苗人數: 25
     *
     * @param body JHU csv 檔
     * */
    public void parseCsvInfo(@NonNull String body){
        String[] countries = body.split("\n");
        for (int i = 0; i<countries.length ;i++){
            String[] column = countries[i].split(",");
            for (int j = 0; j<column.length ; j++){
                
            }
        }
    }

}
