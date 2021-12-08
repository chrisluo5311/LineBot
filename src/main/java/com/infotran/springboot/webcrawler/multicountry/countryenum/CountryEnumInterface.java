package com.infotran.springboot.webcrawler.multicountry.countryenum;

import java.net.URI;

/**
 * 每個country所需做的方法
 * @author chris
 * */
public interface CountryEnumInterface {

    String worldCovidUrl = "https://covid-19.nchc.org.tw/dt_owl.php?dt_name=4&countrycode=";

    /** 國旗照片Uri */
    URI getUri();

    /** 各國疫情統計圖的超連結 */
    String getActionUri();

}
