package com.infotran.springboot.webcrawler.multicountry.service.enums;

import com.infotran.springboot.exception.LineBotException;
import com.infotran.springboot.exception.exceptionenum.LineBotExceptionEnums;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 各國疫情狀況對應欄位
 * */
@Getter
@AllArgsConstructor
public enum CountryResponseCode {

    ISOCODE(1),
    COUNTRY(3),
    TIMESTAMP(4),
    TOTAL_AMOUNT(5),
    NEW_AMOUNT(6),
    TOTAL_DEATH(8),
    NEW_DEATH(9),
    CONFIRMED_IN_MILLIONS(11),
    DEATH_IN_MILLIONS(12),
    VACCINED_AMOUNT(21),
    VACCINED_IN_HUNDREDS(25);

    private Integer code;

    /**
     * 獲取 CountryResponseCode
     * @param code 響應碼
     * */
    public static CountryResponseCode getCollumn(Integer code) throws LineBotException {
        return Arrays.stream(CountryResponseCode.values()).filter(x->x.getCode().equals(code)).findFirst().orElseThrow(()->new LineBotException(LineBotExceptionEnums.MISSING_COUNTRY_RESPONSECODE));
    }
}
