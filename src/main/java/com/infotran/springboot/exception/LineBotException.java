package com.infotran.springboot.exception;

import com.infotran.springboot.exception.exceptionenum.LineBotExceptionEnums;

public class LineBotException extends Exception{

    private LineBotExceptionEnums lineBotExceptionCodeCode;

    public LineBotException(LineBotExceptionEnums lineBotExceptionCodeCode, Object message, Throwable cause){
        super(lineBotExceptionCodeCode.getStatus() + "->" + message,cause);
        this.lineBotExceptionCodeCode = lineBotExceptionCodeCode;
    }

    public LineBotException(LineBotExceptionEnums lineBotExceptionCodeCode, Object message){
        super(lineBotExceptionCodeCode.getStatus() + "->" + message);
        this.lineBotExceptionCodeCode = lineBotExceptionCodeCode;
    }


    public LineBotException(LineBotExceptionEnums lineBotExceptionCodeCode){
        super(lineBotExceptionCodeCode.getStatus());
        this.lineBotExceptionCodeCode = lineBotExceptionCodeCode;
    }

    public LineBotExceptionEnums getWebCrawlerCode(){
        return this.lineBotExceptionCodeCode;
    }
}
