package com.infotran.springboot.linebot.controller;

import com.infotran.springboot.linebot.service.LineReplyMessageHandler;
import com.infotran.springboot.linebot.service.messagehandler.HandleLocationMessageReply;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.infotran.springboot.linebot.service.messagehandler.TestReplyMessageHandler;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import javax.annotation.Resource;

@LineMessageHandler
@SpringBootApplication
public class EchoApplication {

	public static void main(String[] args) {
	    SpringApplication.run(EchoApplication.class, args);
	 }
	
	@Autowired
	TestReplyMessageHandler replymessagehandler;

	@Resource
	LineReplyMessageHandler lineReplyMessageHandler;

	@Resource
	HandleLocationMessageReply handleLocationMessageRely;

	@EventMapping
	public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {  
        BotApiResponse response = replymessagehandler.textMessageReply(event);
    }
	
	@EventMapping
	public void handlePostBackEvent(PostbackEvent event)throws Exception{
		lineReplyMessageHandler.postBackReply(event);
	}
	
	@EventMapping
	public void handleStickerMessageEvent(MessageEvent<StickerMessageContent> event) {
		replymessagehandler.handleSticker(event.getReplyToken(), event.getMessage());
    }


	@EventMapping
	public void handleLocationMessageEvent(MessageEvent<LocationMessageContent> event) {
		replymessagehandler.handleLocationMessageRely(event);
	}
}
