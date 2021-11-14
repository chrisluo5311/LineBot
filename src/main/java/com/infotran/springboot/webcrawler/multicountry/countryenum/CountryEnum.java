package com.infotran.springboot.webcrawler.multicountry.countryenum;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 國家enum
 * @author chris
 */
@Getter
@AllArgsConstructor
public enum CountryEnum {

    US(Code.US,"美國");

    /** 國家代號 */
    private final String countryCode;
    /** 國家名稱 */
    private final String name;

    public class Code{
        public static final String US = "US";
    }
}
