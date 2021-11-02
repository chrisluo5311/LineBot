package com.infotran.springboot.linebot.controller;

import com.infotran.springboot.exception.LineBotException;
import com.infotran.springboot.exception.exceptionenum.LineBotExceptionEnums;
import com.infotran.springboot.linebot.service.BaseMessageInterface;
import com.infotran.springboot.linebot.service.BaseMessagePool;
import com.infotran.springboot.linebot.service.messagehandler.enums.HandlerEnum;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

@Slf4j
@LineMessageHandler
public class EchoApplication {

    private static final String LOG_PREFIX = "EchoApplication";

    @Resource
    BaseMessagePool baseMessagePool;

    BaseMessageInterface baseMessageInterface = null;

    /**
     * 處理目錄
     * @param event PostbackEvent
     * */
    @EventMapping
    public void handlePostBackEvent(PostbackEvent event) throws Exception {
        BotApiResponse botApiResponse = null;
        String data = event.getPostbackContent().getData();
        switch (data){
            case "國內外疫情":
                break;
            case "施打疫苗統計":
                baseMessageInterface = baseMessagePool.getMethod(HandlerEnum.getHandlerName(5));
                botApiResponse = baseMessageInterface.postBackReply(event);
                break;
            case "其他":
                baseMessageInterface = baseMessagePool.getMethod(HandlerEnum.getHandlerName(6));
                botApiResponse = baseMessageInterface.postBackReply(event);
        }
        if(botApiResponse==null){
            log.warn("接收處理PostbackEvent失敗");
            throw new LineBotException(LineBotExceptionEnums.BOTAPI_RESPONSE_EMPTY,"PostbackEvent");
        }
    }


    /**
     * 處理MessageEvent
     * @param event MessageEvent<T>
     * */
    @EventMapping
    public void handleMessageEvent(MessageEvent event) throws Exception {
        if (event.getMessage() instanceof TextMessageContent) {
            String text =((TextMessageContent) event.getMessage()).getText();
            switch (text){
                case "查詢今日確診":
                case "昨日確診數":
                    baseMessageInterface = baseMessagePool.getMethod(HandlerEnum.getHandlerName(1));
                    break;
                case "查看所在位置口罩剩餘狀態":
                case "下五間":
                case "重新定位":
                    baseMessageInterface = baseMessagePool.getMethod(HandlerEnum.getHandlerName(2));
                    break;
                default:
                    //測試
                    baseMessageInterface = baseMessagePool.getMethod(HandlerEnum.getHandlerName(0));
                    break;
            }
        } else if (event.getMessage() instanceof LocationMessageContent){
            baseMessageInterface = baseMessagePool.getMethod(HandlerEnum.getHandlerName(2));
        }
        //接收處理 MessageEvent
        BotApiResponse botApiResponse = baseMessageInterface.handleMessageEvent(event);
        if(botApiResponse==null){
            log.warn("接收處理MessageEvent失敗");
            throw new LineBotException(LineBotExceptionEnums.BOTAPI_RESPONSE_EMPTY,"MessageEvent");
        }
    }



}
