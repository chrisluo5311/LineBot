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

    public static final String US = "US";

    /** JHU Data URL 需補上每日日期 */
    @Value("${JHU.URL.PREFIX}")
    public String JHU_URL;

    /**
     * 解析各國疫情狀況
     * @param body JHU csv 檔
     * */
    public void parseCsvInfo(@NonNull String body){
        String[] lines = body.split("/n");
        for(int i = 1; i<lines.length;i++){
            String[] eachCountry = lines[i].split(",");
            for(int j = 0; j<eachCountry.length;j++){
                // 3:Country_Region 4:Last_Update 7:Confirmed 8:Deaths 12:Incident_Rate 13:Case_Fatality_Ratio
                if(US.equals(eachCountry[3])){
                    Integer confirmedSum = Integer.parseInt(eachCountry[7]);
                }
            }
        }
    }

}
