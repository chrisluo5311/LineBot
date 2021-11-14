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

    public static final String US = "US";

    /** JHU Data URL 需補上每日日期 */
    @Value("${CDC.WORLD.COVID}")
    public String JHU_URL;

    /**
     * 解析各國疫情狀況
     * @param body JHU csv 檔
     * */
    public void parseCsvInfo(@NonNull String body){
        // 3:Country_Region 4:Last_Update 7:Confirmed 8:Deaths 12:Incident_Rate 13:Case_Fatality_Ratio
        String[] countries = body.split("\n");
        // other countries

        // US 從674行開始
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
