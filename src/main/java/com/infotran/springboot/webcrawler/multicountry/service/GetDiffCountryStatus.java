package com.infotran.springboot.webcrawler.multicountry.service;

import com.infotran.springboot.webcrawler.multicountry.countryenum.CountryEnum;
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
     * @param body JHU csv 檔
     * */
    public void parseCsvInfo(@NonNull String body){
        String[] countries = body.split("\n");
        // other countries

        Integer sum = 0;
        for(int i = 674 ; i < countries.length ; i++){
            // us 各州
            String[] eachState = countries[i].split(",");
            for(int j = 0; j < eachState.length;j++){
                //每一個state的狀況
                if(CountryEnum.US.getCountryCode().equals(eachState[3])){
                    log.info("");
                    Integer confirmedNum = Integer.parseInt(eachState[7]);
                    sum = sum + confirmedNum;
                }
            }
        }
        log.info("confirmed sum:{}",sum);
    }

}
