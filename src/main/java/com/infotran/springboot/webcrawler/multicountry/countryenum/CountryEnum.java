package com.infotran.springboot.webcrawler.multicountry.countryenum;


import com.infotran.springboot.util.HandleFileUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.URI;
import java.util.Arrays;

/**
 * 國家enum
 * @author chris
 */
@Getter
@AllArgsConstructor
public enum CountryEnum implements CountryEnumInterface{

    GLOBAL(Code.GLOBAL,"全球"){
        @Override
        public URI getUri() {
            return HandleFileUtil.createUri("/static/worldCovid19.png");
        }

        @Override
        public String getActionUri() {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    NORTH_AMERICA(Code.NORTH_AMERICA,"北美洲"){
        @Override
        public URI getUri() {
            return null;
        }

        @Override
        public String getActionUri() {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    US(Code.US,"美國"){

        @Override
        public URI getUri() {
            return HandleFileUtil.createUri("/static/AmericaFlag.png");
        }

        @Override
        public String getActionUri() {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    EU(Code.EU,"歐洲聯盟"){
        @Override
        public URI getUri() {
            return null;
        }

        @Override
        public String getActionUri() {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    SOUTH_AMERICA(Code.SOUTH_AMERICA,"南美洲"){
        @Override
        public URI getUri() {
            return null;
        }

        @Override
        public String getActionUri() {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    INDIA(Code.INDIA,"印度"){
        @Override
        public URI getUri() {
            return null;
        }

        @Override
        public String getActionUri() {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    BRAZIL(Code.BRAZIL,"巴西"){
        @Override
        public URI getUri() {
            return null;
        }

        @Override
        public String getActionUri() {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    ENGLAND(Code.ENGLAND,"英國"){
        @Override
        public URI getUri() {
            return null;
        }

        @Override
        public String getActionUri() {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    RUSSIA(Code.RUSSIA,"俄羅斯"){
        @Override
        public URI getUri() {
            return null;
        }

        @Override
        public String getActionUri() {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    FRANCE(Code.FRANCE,"法國"){
        @Override
        public URI getUri() {
            return null;
        }

        @Override
        public String getActionUri() {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    GERMAN(Code.GERMAN,"德國"){
        @Override
        public URI getUri() {
            return null;
        }

        @Override
        public String getActionUri() {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    THAILAND(Code.THAILAND,"泰國"){
        @Override
        public URI getUri() {
            return null;
        }

        @Override
        public String getActionUri() {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    JAPAN(Code.JAPAN,"日本"){
        @Override
        public URI getUri() {
            return HandleFileUtil.createUri("/static/JapanFlag.png");
        }

        @Override
        public String getActionUri() {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    ISRAEL(Code.ISRAEL,"以色列"){
        @Override
        public URI getUri() {
            return null;
        }

        @Override
        public String getActionUri() {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    KOREAN(Code.KOREAN,"韓國"){
        @Override
        public URI getUri() {
            return null;
        }

        @Override
        public String getActionUri() {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    HONGKONG(Code.HONGKONG,"香港"){
        @Override
        public URI getUri() {
            return null;
        }

        @Override
        public String getActionUri() {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    CHINA(Code.CHINA,"中國大陸"){
        @Override
        public URI getUri() {
            return HandleFileUtil.createUri("/static/ChinaFlag.png");
        }

        @Override
        public String getActionUri() {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    SINGAPORE(Code.SINGAPORE,"新加坡"){
        @Override
        public URI getUri() {
            return null;
        }

        @Override
        public String getActionUri() {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    }
    ;

    /** 國家代號(對應CDC全球疫情CSV檔的iso_code) */
    private String countryCode;
    /** 國家名稱 */
    private String name;

    /**
     * 對應的isocode
     * */
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
        public static final String ISRAEL = "ISR";
        public static final String KOREAN = "KOR";
        public static final String HONGKONG = "HKG";
        public static final String CHINA = "CHN";
        public static final String SINGAPORE = "SGP";
    }

    /**
     * 匹配 IsoCode
     * @param code IsoCode
     * @return Boolean
     * */
    public static Boolean matchCountryIsoCode(String code){
        return Arrays.stream(CountryEnum.values()).anyMatch(x->x.getCountryCode().equals(code));
    }

    /**
     * Get CountryEnum By Code
     * @param isoCode
     * @return CountryEnum
     * */
    public static CountryEnum getCountryEnumByCode(String isoCode){
        return Arrays.stream(CountryEnum.values()).filter(x->x.getCountryCode().equals(isoCode)).findFirst().get();
    }
}
