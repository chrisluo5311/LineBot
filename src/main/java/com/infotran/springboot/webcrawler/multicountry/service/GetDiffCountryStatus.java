package com.infotran.springboot.webcrawler.multicountry.service;

import com.infotran.springboot.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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

    /**
     * JHU CSSE COVID-19 Data
     * 需補上每日日期
     * */
    @Value("${JHU.URL.PREFIX}")
    public String JHU_URL;

    @PostConstruct
    public void init(){
        String todayDate =TimeUtil.formForeignTodayDate();
        JHU_URL.concat(todayDate).concat(".csv");
        log.info("JHU URL: {}", JHU_URL);
    }

    public void parseJsonInfo(String body){

    }

}
