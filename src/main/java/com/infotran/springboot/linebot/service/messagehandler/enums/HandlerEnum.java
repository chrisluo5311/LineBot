package com.infotran.springboot.linebot.service.messagehandler.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author chris
 */

@Getter
@AllArgsConstructor
public enum HandlerEnum {

    //新增功能類別要對應功能enum
    HANDLE_DEFAULT_MESSAGE(0,"處理測試文字"),
    HANDLE_TODAY_AMOUNT_MESSAGE(1,"處理今日確診數"),
    HANDLE_LOCATION_MESSAGE(2,"處理藥局地點"),
    HANDLE_QRCODE(3,"掃描QRCode"),
    HANDLE_FOREIGN_COVID(4,"國外疫情"),
    HANDLE_STATISTIC_DIAGRAM(5,"查看統計圖"),
    HANDLE_OTHER_MESSAGE(6,"處理其他");

    private Integer id;
    private String handlerName;

    /**
     *  取得功能名稱
     *  @param id 對應目錄表順序的id
     *  @return String 名字
     * */
    public static String getHandlerName(Integer id){
        return Stream.of(HandlerEnum.values()).filter(x -> x.getId().equals(id)).findFirst().toString();
    }

    /**
     *  取得 HandlerEnum 功能
     *  @param handlerEnum 對應目錄表功能
     *  @return HandlerEnum
     * */
    public static HandlerEnum getHandler(HandlerEnum handlerEnum){
        return Arrays.stream(HandlerEnum.values()).filter(x->x.equals(handlerEnum)).findFirst().get();
    }

    /**
     *  尋找所有enums
     *  @return ArrayList 名字集合
     * */
    public static ArrayList<String> getAllEnums(){
        ArrayList<String> enumList = new ArrayList<>();
        Arrays.stream(HandlerEnum.values()).map(HandlerEnum::getHandlerName).forEach(enumList::add);
        return enumList;
    }

}
