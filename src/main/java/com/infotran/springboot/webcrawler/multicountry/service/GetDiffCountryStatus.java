package com.infotran.springboot.webcrawler.multicountry.service;

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

    /**
     * JHU CSSE COVID-19 Data
     * 需補上每日日期
     * */
    @Value("${JHU.URL.PREFIX}")
    public String JHU_URL_PREFIX;



}
