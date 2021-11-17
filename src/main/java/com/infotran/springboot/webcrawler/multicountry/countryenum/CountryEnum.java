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

    GLOBAL(Code.GLOBAL,"全球"),
    NORTH_AMERICA(Code.NORTH_AMERICA,"北美洲"),
    US(Code.US,"美國"),
    EU(Code.EU,"歐洲聯盟"),
    SOUTH_AMERICA(Code.SOUTH_AMERICA,"南美洲"),
    INDIA(Code.INDIA,"印度"),
    BRAZIL(Code.BRAZIL,"巴西"),
    ENGLAND(Code.ENGLAND,"英國"),
    RUSSIA(Code.RUSSIA,"俄羅斯"),
    FRANCE(Code.FRANCE,"法國"),
    GERMAN(Code.GERMAN,"德國"),
    THAILAND(Code.THAILAND,"泰國"),
    JAPAN(Code.JAPAN,"日本"),
    ISRAEL(Code.ISRAEL,"以色列"),
    KOREAN(Code.KOREAN,"韓國"),
    HONGKONG(Code.HONGKONG,"香港"),
    CHINA(Code.CHINA,"中國大陸")
    ;

    /** 國家代號(對應CDC全球疫情CSV檔的iso_code) */
    private String countryCode;
    /** 國家名稱 */
    private String name;

    public class Code{
        public static final String GLOBAL = "OWID_WRL";
        public static final String US = "USA";
        public static final String NORTH_AMERICA = "OWID_NAM";
        public static final String EU = "OWID_EUN";
        public static final String SOUTH_AMERICA = "OWID_SAM";
        public static final String INDIA = "IND";
        public static final String BRAZIL = "BRA";
        public static final String ENGLAND = "GBR";
        public static final String RUSSIA = "RUS";
        public static final String FRANCE = "FRA";
        public static final String GERMAN = "DEU";
        public static final String THAILAND = "THA";
        public static final String JAPAN = "JPN";
        public static final String ISRAEL = "JPN";
        public static final String KOREAN = "KOR";
        public static final String HONGKONG = "HKG";
        public static final String CHINA = "CHN";
    }
}
