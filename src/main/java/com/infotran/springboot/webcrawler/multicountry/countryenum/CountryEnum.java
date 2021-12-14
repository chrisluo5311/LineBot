package com.infotran.springboot.webcrawler.multicountry.countryenum;

import com.infotran.springboot.util.HandleFileUtil;
import com.infotran.springboot.webcrawler.multicountry.model.DiffCountry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        public String getActionUri(String worldCovidUrl) {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    NORTH_AMERICA(Code.NORTH_AMERICA,"北美洲"){
        @Override
        public URI getUri() {
            return  HandleFileUtil.createUri("/static/NorthAmerica.png");
        }

        @Override
        public String getActionUri(String worldCovidUrl) {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    US(Code.US,"美國"){

        @Override
        public URI getUri() {
            return HandleFileUtil.createUri("/static/AmericaFlag.png");
        }

        @Override
        public String getActionUri(String worldCovidUrl) {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    EU(Code.EU,"歐洲聯盟"){
        @Override
        public URI getUri() {
            return HandleFileUtil.createUri("/static/EU.jpg");
        }

        @Override
        public String getActionUri(String worldCovidUrl) {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    SOUTH_AMERICA(Code.SOUTH_AMERICA,"南美洲"){
        @Override
        public URI getUri() {
            return HandleFileUtil.createUri("/static/SouthAmerica.png");
        }

        @Override
        public String getActionUri(String worldCovidUrl) {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    INDIA(Code.INDIA,"印度"){
        @Override
        public URI getUri() {
            return HandleFileUtil.createUri("/static/India.jpg");
        }

        @Override
        public String getActionUri(String worldCovidUrl) {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    BRAZIL(Code.BRAZIL,"巴西"){
        @Override
        public URI getUri() {
            return HandleFileUtil.createUri("/static/Brazil.jpg");
        }

        @Override
        public String getActionUri(String worldCovidUrl) {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    ENGLAND(Code.ENGLAND,"英國"){
        @Override
        public URI getUri() {
            return HandleFileUtil.createUri("/static/UK.png");
        }

        @Override
        public String getActionUri(String worldCovidUrl) {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    RUSSIA(Code.RUSSIA,"俄羅斯"){
        @Override
        public URI getUri() {
            return HandleFileUtil.createUri("/static/Russia.jpg");
        }

        @Override
        public String getActionUri(String worldCovidUrl) {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    FRANCE(Code.FRANCE,"法國"){
        @Override
        public URI getUri() {
            return HandleFileUtil.createUri("/static/France.jpg");
        }

        @Override
        public String getActionUri(String worldCovidUrl) {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    GERMAN(Code.GERMAN,"德國"){
        @Override
        public URI getUri() {
            return HandleFileUtil.createUri("/static/German.png");
        }

        @Override
        public String getActionUri(String worldCovidUrl) {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    THAILAND(Code.THAILAND,"泰國"){
        @Override
        public URI getUri() {
            return HandleFileUtil.createUri("/static/Thailand.png");
        }

        @Override
        public String getActionUri(String worldCovidUrl) {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    JAPAN(Code.JAPAN,"日本"){
        @Override
        public URI getUri() {
            return HandleFileUtil.createUri("/static/JapanFlag.png");
        }

        @Override
        public String getActionUri(String worldCovidUrl) {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    ISRAEL(Code.ISRAEL,"以色列"){
        @Override
        public URI getUri() {
            return HandleFileUtil.createUri("/static/Israel.png");
        }

        @Override
        public String getActionUri(String worldCovidUrl) {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    KOREAN(Code.KOREAN,"韓國"){
        @Override
        public URI getUri() {
            return HandleFileUtil.createUri("/static/Korea.png");
        }

        @Override
        public String getActionUri(String worldCovidUrl) {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    HONGKONG(Code.HONGKONG,"香港"){
        @Override
        public URI getUri() {
            return HandleFileUtil.createUri("/static/HK.png");
        }

        @Override
        public String getActionUri(String worldCovidUrl) {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    CHINA(Code.CHINA,"中國大陸"){
        @Override
        public URI getUri() {
            return HandleFileUtil.createUri("/static/ChinaFlag.png");
        }

        @Override
        public String getActionUri(String worldCovidUrl) {
            return worldCovidUrl.concat(this.getCountryCode());
        }
    },
    SINGAPORE(Code.SINGAPORE,"新加坡"){
        @Override
        public URI getUri() {
            return HandleFileUtil.createUri("/static/Singapore.png");
        }

        @Override
        public String getActionUri(String worldCovidUrl) {
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
     * 匹配 CountryName
     * @param countryName countryName
     * @return Boolean
     * */
    public static Boolean matchCountryName(String countryName){
        return Arrays.stream(CountryEnum.values()).anyMatch(x->x.getName().equals(countryName));
    }

    /**
     * 匹配 「點我看更多日本資訊」
     * @param textMessageContent text
     * @return Boolean
     * */
    public static Boolean isValidCountryName(String textMessageContent){
        if(textMessageContent.startsWith("點我看更多")){
            String countryName = textMessageContent.substring(5, textMessageContent.indexOf("資訊"));
            return matchCountryName(countryName);
        }
        return false;
    }

    /**
     * ex. 擷取「點我看更多日本資訊」 中的 「日本」
     * @param textMessageContent text
     * @return String
     * */
    public static String getCountryNameByTextMessageContent(String textMessageContent){
        return textMessageContent.substring(5, textMessageContent.indexOf("資訊"));
    }

    /**
     * Get CountryEnum By Code
     * @param isoCode
     * @return CountryEnum
     * */
    public static CountryEnum getCountryEnumByCode(String isoCode){
        return Arrays.stream(CountryEnum.values()).filter(x->x.getCountryCode().equals(isoCode)).findFirst().get();
    }

    /**
     * Get CountryEnum By countryName
     * @param countryName
     * @return CountryEnum
     * */
    public static CountryEnum getCountryEnumByName(String countryName){
        return Arrays.stream(CountryEnum.values()).filter(x->x.getName().equals(countryName)).findFirst().get();
    }

    /**
     * 取得前四個優先回覆的地區
     * @return List CountryEnum
     * */
    public static List<CountryEnum> getPriorityCountryEnum(){
        List<CountryEnum> countryEnumList = new ArrayList<>();
        countryEnumList.add(CountryEnum.GLOBAL);
        countryEnumList.add(CountryEnum.US);
        countryEnumList.add(CountryEnum.CHINA);
        countryEnumList.add(CountryEnum.JAPAN);
        return countryEnumList;
    }

    /**
     * 製作回覆模板
     * @param diffCountry
     * @return String
     * */
    public static String createReplyTemplate(@NonNull DiffCountry diffCountry,Boolean isCarousel){
        StringBuffer text = new StringBuffer();
        String totalAmount          = diffCountry.getTotalAmount();
        String newAmount            = diffCountry.getNewAmount();
        String totalDeath           = diffCountry.getTotalDeath();
        String newDeath             = diffCountry.getNewDeath();
        //每百萬確診數
        String confirmedInMillions  = diffCountry.getConfirmedInMillions();
        //每百萬死亡數
        String deathInMillions      = diffCountry.getDeathInMillions();
        //疫苗總接種人數
        String totalVaccinated      = diffCountry.getTotalVaccinated();
        //每百人接種疫苗人數
        String vaccinatedInHundreds = diffCountry.getVaccinatedInHundreds();
        String lastUpdate           = diffCountry.getLastUpdate();
        if(isCarousel){
            text.append("新增確診數: ").append(newAmount+"人\n");
            text.append("新增死亡數: ").append(newDeath+"人\n");
            text.append("更新時間: ").append(lastUpdate+"\n");
        } else {
            text.append("總確診數: ").append(totalAmount+"人\n");
            text.append("新增確診數: ").append(newAmount+"人\n");
            text.append("總死亡數: ").append(totalDeath+"人\n");
            text.append("新增死亡數: ").append(newDeath+"人\n");
            text.append("每百萬確診數: ").append(confirmedInMillions+"\n");
            text.append("每百萬死亡數: ").append(deathInMillions+"\n");
            text.append("疫苗總接種數: ").append(totalVaccinated+"人\n");
            text.append("每百人接種疫苗人數: ").append(vaccinatedInHundreds+"\n");
            text.append("更新時間: ").append(lastUpdate+"\n\n");
            text.append("備:欄位為0者，可能為該國未提供，資料僅供參考，若有誤差，以官方提供為準。");
        }
        return text.toString();
    }


}
