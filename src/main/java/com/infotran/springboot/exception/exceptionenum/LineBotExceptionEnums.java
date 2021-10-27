package com.infotran.springboot.exception.exceptionenum;

/*
* 爬蟲狀態
*
* */
public enum LineBotExceptionEnums {

    SUCCESS("000","爬蟲成功"),

    FAIL_ON_WEBCRAWLING("A01","爬蟲過程失敗"),
    FAIL_ON_BODY_RESPONSE("A02","響應失敗"),
    FAIL_ON_SSLHELPER_CONNECTION("A03","SSL Connection 失敗"),
    FAIL_ON_FIND_TODAY_COVIDNEWS("A04","查找今日新聞失敗 或 尚未發佈 或 標題錯誤"),

    RESPONSE_EMPTY("B01","響應物件為空"),
    RESPONSE_NOT_TARGET("B02","響應物件非目標"),

    NEWS_KEYWORD_CHANGE("C01","新聞關鍵字改變"),
    NEWS_TITLE_CHANGE("C02","新聞標題改變，請手動更新"),

    FAIL_ON_SAVING_RESPONSE("D01","資料庫儲存失敗"),

    FAIL_ON_IMPLEMENT_GETCLASSNAME("E01","實現類未實作getClassName()方法");

    private String code;
    private String status;


    LineBotExceptionEnums(String code, String status) {
        this.code = code;
        this.status = status;
    }

    public String getCode(){
        return this.code;
    }

    public String getStatus(){
        return this.status;
    }
}
