package com.infotran.springboot.LineBot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.infotran.springboot.LineBot.service.ReplyMessageHandler;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
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
        System.out.println("event: " + event);
        BotApiResponse response = replymessagehandler.reply(event);
        System.out.println("Sent messages: " + response);
    }
	
	@EventMapping
	public void handlePostBackEvent(PostbackEvent event)throws Exception{
		
		
	}
	
}
