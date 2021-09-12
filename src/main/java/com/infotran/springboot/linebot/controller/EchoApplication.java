package com.infotran.springboot.linebot.controller;

import com.linecorp.bot.model.event.message.LocationMessageContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.infotran.springboot.linebot.service.impl.ReplyMessageHandler;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

@LineMessageHandler
@SpringBootApplication
public class EchoApplication {

	public static void main(String[] args) {
	    SpringApplication.run(EchoApplication.class, args);
	 }
	
	@Autowired
	ReplyMessageHandler replymessagehandler;

	@EventMapping
	public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {  
        BotApiResponse response = replymessagehandler.textMessageReply(event);
    }
	
	@EventMapping
	public void handlePostBackEvent(PostbackEvent event)throws Exception{
		replymessagehandler.postBackReply(event);
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
