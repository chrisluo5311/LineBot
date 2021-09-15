package com.infotran.springboot.Exception.Enum;

/*
* 爬蟲狀態
*
* */
public enum WebCrawlerCode {

    SUCCESS("000","爬蟲成功"),

    FAIL_ON_PROCESSING("A01","爬蟲過程失敗"),
    FAIL_ON_RESPONSE("A02","響應失敗"),

    RESPONSE_EMPTY("B01","響應物件為空"),
    RESPONSE_NOT_TARGET("B02","響應物件非目標"),

    NEWS_KEYWORD_CHANGE("C01","新聞關鍵字改變");



    private String code;
    private String status;


    WebCrawlerCode(String code, String status) {
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
