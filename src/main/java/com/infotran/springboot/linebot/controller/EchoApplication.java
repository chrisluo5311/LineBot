package com.infotran.springboot.linebot.controller;

import com.infotran.springboot.linebot.service.BaseMessageHandler;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import javax.annotation.Resource;

@LineMessageHandler
@SpringBootApplication
@Slf4j
public class EchoApplication {

    public static void main(String[] args) {
        SpringApplication.run(EchoApplication.class, args);
    }

    @Resource
    BaseMessageHandler baseMessageHandler;


    /**
     * 處理目錄
     * @param event PostbackEvent
     * */
    @EventMapping
    public void handlePostBackEvent(PostbackEvent event) throws Exception {
        baseMessageHandler.postBackReply(event);
    }

    /**
     * 處理使用者位置
     * @param event MessageEvent<LocationMessageContent>
     * */
    @EventMapping
    public void handleLocationMessageEvent(MessageEvent<LocationMessageContent> event) throws Exception {
        baseMessageHandler.handleMessageEvent(event);
    }

    /**
     * 測試-接收使用者文字訊息
     * @param event MessageEvent<TextMessageContent>
     * */
    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
        baseMessageHandler.handleMessageEvent(event);
    }

    /**
     * 測試-接收使用者貼圖訊息
     * @param event MessageEvent<StickerMessageContent>
     * */
    @EventMapping
    public void handleStickerMessageEvent(MessageEvent<StickerMessageContent> event) throws Exception {
        baseMessageHandler.handleMessageEvent(event);
    }


}
