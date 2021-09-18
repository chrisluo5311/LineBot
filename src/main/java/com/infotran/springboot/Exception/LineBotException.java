package com.infotran.springboot.Exception;

import com.infotran.springboot.Exception.Enum.LineBotExceptionCode;

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
