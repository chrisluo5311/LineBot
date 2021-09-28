package com.infotran.springboot.linebot.controller;

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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@LineMessageHandler
@SpringBootApplication
@Slf4j
public class EchoApplication {

    public static void main(String[] args) {
        SpringApplication.run(EchoApplication.class, args);
    }

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
        String data = event.getPostbackContent().getData();
        switch (data){
            case "國內外疫情":
                break;
            case "施打疫苗統計":
                break;
            case "其他":
                baseMessageInterface = baseMessagePool.getMethod(HandlerEnum.getHandlerName(6));
        }
        BotApiResponse botApiResponse = baseMessageInterface.postBackReply(event);
        log.info("{} 處理PostbackEvent方法 回傳物件: {}",LOG_PREFIX,botApiResponse);
    }


    /**
     * 處理MessageEvent
     * @param event MessageEvent<T>
     * */
    @EventMapping
    public void handleMessageEvent(MessageEvent event) throws Exception {
        log.info("handleMessageEvent方法");
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

        BotApiResponse botApiResponse = baseMessageInterface.handleMessageEvent(event);
        log.info("{} handleMessageEvent方法 BotApiResponse回傳物件: {}",LOG_PREFIX,botApiResponse);
    }



}
