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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 處理 LineBot 回覆訊息
 * @author chris
 */
@Slf4j
@LineMessageHandler
public class EchoApplication {

    private static String LOG_PREFIX = "[EchoApplication]";

    @Resource
    BaseMessagePool baseMessagePool;

    BaseMessageInterface baseMessageInterface;

    /**
     * 處理目錄
     * @param event PostbackEvent
     * */
    @EventMapping
    public void handlePostBackEvent(PostbackEvent event) {
        try{
            HandlerEnum handlerEnum = null;
            String data = event.getPostbackContent().getData();
            Map<String,HandlerEnum> requestMapping = new HashMap<>();
            requestMapping.put("國內外疫情",HandlerEnum.HANDLE_FOREIGN_COVID);
            requestMapping.put("其他",HandlerEnum.HANDLE_OTHER_MESSAGE);
            if(requestMapping.containsKey(data)){
                handlerEnum = requestMapping.get(data);
            } else {
                throw new LineBotException(LineBotExceptionEnums.BOTAPI_RESPONSE_EMPTY,"PostbackEvent");
            }
            baseMessageInterface = baseMessagePool.getMethod(handlerEnum);
            BotApiResponse botApiResponse = baseMessageInterface.postBackReply(event);
            if(Objects.isNull(botApiResponse)){
                throw new LineBotException(LineBotExceptionEnums.BOTAPI_RESPONSE_EMPTY,"PostbackEvent");
            }
        } catch (LineBotException e) {
            log.error("{} 接收處理PostbackEvent失敗或無對應PostbackEvent Data:{}",LOG_PREFIX,e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 處理MessageEvent
     * @param event MessageEvent<T>
     * */
    @EventMapping
    public void handleMessageEvent(MessageEvent event)  {
        try{
            if (event.getMessage() instanceof TextMessageContent) {
                String text =((TextMessageContent) event.getMessage()).getText();
                Map<String,HandlerEnum> requestMapping = new HashMap<>();
                requestMapping.put("查詢今日確診",HandlerEnum.HANDLE_DEFAULT_MESSAGE);
                requestMapping.put("昨日確診數",HandlerEnum.HANDLE_DEFAULT_MESSAGE);
                requestMapping.put("查看所在位置口罩剩餘狀態",HandlerEnum.HANDLE_LOCATION_MESSAGE);
                requestMapping.put("下五間",HandlerEnum.HANDLE_LOCATION_MESSAGE);
                requestMapping.put("重新定位",HandlerEnum.HANDLE_LOCATION_MESSAGE);
                requestMapping.put("掃描QRCode",HandlerEnum.HANDLE_QRCODE);
                requestMapping.put("查看統計圖",HandlerEnum.HANDLE_STATISTIC_DIAGRAM);
                //若無則default
                HandlerEnum handlerEnum = (requestMapping.containsKey(text))
                                            ? requestMapping.get(text)
                                            : HandlerEnum.HANDLE_DEFAULT_MESSAGE;
                baseMessageInterface = baseMessagePool.getMethod(handlerEnum);
            } else if (event.getMessage() instanceof LocationMessageContent){
                baseMessageInterface = baseMessagePool.getMethod(HandlerEnum.HANDLE_LOCATION_MESSAGE);
            }
            //接收處理 MessageEvent
            BotApiResponse botApiResponse = baseMessageInterface.handleMessageEvent(event);
            if(Objects.isNull(botApiResponse)){
                throw new LineBotException(LineBotExceptionEnums.BOTAPI_RESPONSE_EMPTY,"MessageEvent");
            }
        } catch (LineBotException e) {
            log.error("{} 接收處理MessageEvent失敗或無對應MessageEvent test:{}",LOG_PREFIX,e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
