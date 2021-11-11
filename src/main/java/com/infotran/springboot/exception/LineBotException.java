package com.infotran.springboot.exception;

import com.infotran.springboot.exception.exceptionenum.LineBotExceptionEnums;

/**
 * @author chris
 */
public class LineBotException extends Exception{

    private final LineBotExceptionEnums lineBotExceptionCodeCode;

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
