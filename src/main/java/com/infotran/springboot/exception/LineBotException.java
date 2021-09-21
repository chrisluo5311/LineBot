package com.infotran.springboot.exception;

import com.infotran.springboot.exception.exceptionenum.LineBotExceptionCode;

public class LineBotException extends Exception{

    private LineBotExceptionCode lineBotExceptionCodeCode;

    public LineBotException(LineBotExceptionCode lineBotExceptionCodeCode, Object message, Throwable cause){
        super(lineBotExceptionCodeCode.getStatus() + "->" + message,cause);
        this.lineBotExceptionCodeCode = lineBotExceptionCodeCode;
    }

    public LineBotException(LineBotExceptionCode lineBotExceptionCodeCode, Object message){
        super(lineBotExceptionCodeCode.getStatus() + "->" + message);
        this.lineBotExceptionCodeCode = lineBotExceptionCodeCode;
    }


    public LineBotException(LineBotExceptionCode lineBotExceptionCodeCode){
        super(lineBotExceptionCodeCode.getStatus());
        this.lineBotExceptionCodeCode = lineBotExceptionCodeCode;
    }

    public LineBotExceptionCode getWebCrawlerCode(){
        return this.lineBotExceptionCodeCode;
    }
}
