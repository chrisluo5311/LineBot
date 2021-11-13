package com.infotran.springboot.exception.exceptionenum;

/**
 * 自訂義LineBot Exception 枚舉
 *
 * @author chris
 */
public enum LineBotExceptionEnums {

    FAIL_ON_WEBCRAWLING("A01","爬蟲過程失敗"),
    FAIL_ON_BODY_RESPONSE("A02","響應失敗"),
    FAIL_ON_SSLHELPER_CONNECTION("A03","SSL Connection 失敗"),
    FAIL_ON_FIND_TODAY_COVIDNEWS("A04","查找今日新聞失敗 或 尚未發佈 或 標題錯誤"),
    FAIL_ON_GET_JSONOBJECT("A05","取得JSON物件失敗"),

    BOTAPI_RESPONSE_EMPTY("B01","MessageEvent響應物件為空"),
    RESPONSE_NOT_TARGET("B02","響應物件非目標"),

    NEWS_KEYWORD_CHANGE("C01","新聞關鍵字改變"),
    NEWS_TITLE_CHANGE("C02","新聞標題改變，請手動更新"),
    NEWS_CONTENT_CHANGE("C03","新聞内文[境外移入或本土]改變，請手動更新"),

    DB_FAILED("D01","資料庫儲存失敗"),

    FAIL_ON_IMPLEMENT_GETCLASSNAME("E01","實現類未實作getClassName()方法"),

    FAIL_ON_OUTPUT_FILE("F01","檔案輸出失敗"),

    FAIL_ON_CREATE_RICHMENU("G01","製作Line RichMenu失敗")
    ;

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
