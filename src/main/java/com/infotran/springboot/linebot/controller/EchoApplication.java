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

import javax.annotation.PostConstruct;
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

    /** 統一處理訊息的介面 */
    BaseMessageInterface handleMessage;

    /** 處理 PostbackEvent */
    Map<String,HandlerEnum> postbackRequestMapping = new HashMap<>();

    /** 處理 MessageEvent */
    Map<String,HandlerEnum> messageEventRequestMapping = new HashMap<>();

    @PostConstruct
    public void init(){
        //PostbackEvent
        postbackRequestMapping.put("國內外疫情",HandlerEnum.HANDLE_FOREIGN_COVID);
        postbackRequestMapping.put("其他",HandlerEnum.HANDLE_OTHER_MESSAGE);

        //MessageEvent
        messageEventRequestMapping.put("查詢今日確診",HandlerEnum.HANDLE_TODAY_AMOUNT_MESSAGE);
        messageEventRequestMapping.put("昨日確診數",HandlerEnum.HANDLE_TODAY_AMOUNT_MESSAGE);
        messageEventRequestMapping.put("查看所在位置口罩剩餘狀態",HandlerEnum.HANDLE_LOCATION_MESSAGE);
        messageEventRequestMapping.put("下五間",HandlerEnum.HANDLE_LOCATION_MESSAGE);
        messageEventRequestMapping.put("重新定位",HandlerEnum.HANDLE_LOCATION_MESSAGE);
        messageEventRequestMapping.put("掃描QRCode",HandlerEnum.HANDLE_QRCODE);
        messageEventRequestMapping.put("查看統計圖",HandlerEnum.HANDLE_STATISTIC_DIAGRAM);
        messageEventRequestMapping.put("查看各縣市COVID-19疫苗涵蓋率",HandlerEnum.HANDLE_STATISTIC_DIAGRAM);
    }

    /**
     * 處理PostbackEvent
     * @param event PostbackEvent
     * */
    @EventMapping
    public void handlePostBackEvent(PostbackEvent event) {
        try{
            HandlerEnum handlerEnum = null;
            String data = event.getPostbackContent().getData();
            if(postbackRequestMapping.containsKey(data)){
                handlerEnum = postbackRequestMapping.get(data);
            } else {
                throw new LineBotException(LineBotExceptionEnums.BOTAPI_RESPONSE_EMPTY,"PostbackEvent");
            }
            handleMessage = baseMessagePool.getMethod(handlerEnum);
            BotApiResponse botApiResponse = handleMessage.postBackReply(event,data);
            if(Objects.isNull(botApiResponse)){
                throw new LineBotException(LineBotExceptionEnums.BOTAPI_RESPONSE_EMPTY,"PostbackEvent");
            }
        } catch (LineBotException e) {
            log.error("{} 接收處理PostbackEvent失敗 或 無對應PostbackEvent Data 或 實作類未對應enum:{}",LOG_PREFIX,e.getMessage());
        } catch (Exception e) {
            log.error("{} 接收處理PostbackEvent失敗 或 無對應PostbackEvent Data 或 實作類未對應enum:{}",LOG_PREFIX,e.getMessage());
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
                //若無則default
                HandlerEnum handlerEnum = (messageEventRequestMapping.containsKey(text))
                                            ? messageEventRequestMapping.get(text)
                                            : HandlerEnum.HANDLE_DEFAULT_MESSAGE;
                handleMessage = baseMessagePool.getMethod(handlerEnum);
            } else if (event.getMessage() instanceof LocationMessageContent){
                handleMessage = baseMessagePool.getMethod(HandlerEnum.HANDLE_LOCATION_MESSAGE);
            }
            //接收處理 MessageEvent
            BotApiResponse botApiResponse = handleMessage.handleMessageEvent(event);
            if(Objects.isNull(botApiResponse)){
                throw new LineBotException(LineBotExceptionEnums.BOTAPI_RESPONSE_EMPTY,"MessageEvent");
            }
        } catch (LineBotException e) {
            log.error("{} 接收處理MessageEvent失敗或無對應MessageEvent test:{}",LOG_PREFIX,e.getMessage());
        } catch (Exception e) {
            log.error("{} 接收處理MessageEvent失敗或無對應MessageEvent test:{}",LOG_PREFIX,e.getMessage());
        }
    }

}
