package com.infotran.springboot.LineBot.service;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.TextMessage.Emoji;
import com.linecorp.bot.model.response.BotApiResponse;

@Service
public class ReplyMessageHandler {
	
	
	final LineMessagingClient client = LineMessagingClient
	        .builder("ww5P0UIpl9jxB+a+dkO44euiFLfNhbUW+uqNjUue+dk/evwcYs/7ySV6iwwatmQwZExhR3polWKVDIJHxXMSehCExemJ4RRny0uFpYdWgwsp+Mi+643shY6fzzH/Ttqbn9iHPB8xm7GAlg7UH/klIwdB04t89/1O/w1cDnyilFU=")
	        .build();
	
	private ReplyMessage replyMessage = null;
	
	private static URI createUri(String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                                          .scheme("https")
                                          .path(path).build()
                                          .toUri();
    }
	
	@SuppressWarnings("null")
	public BotApiResponse reply(MessageEvent<TextMessageContent> event)throws IOException{
		
		 String receivedMessage = event.getMessage().getText();
	     String replyToken = event.getReplyToken();
	     BotApiResponse botApiResponse = null;
	     
	     switch (receivedMessage) {
		case "測試文字":
			List<Emoji> emojis = new ArrayList<Emoji>();
			Emoji emoji = Emoji.builder().index(4).productId("5ac1bfd5040ab15980c9b435").emojiId("002").build();
			emojis.add(emoji);
			TextMessage textMessage = TextMessage.builder().text("測試成功$").emojis(emojis).build();
			replyMessage = new ReplyMessage(replyToken,textMessage);
			try {
				botApiResponse = client.replyMessage(replyMessage).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			break;
		case "測試carousel":
			URI imageuri = URI.create("/static/maskon.jpg");
			break;
		default:
			break;
		}
	     
	     return botApiResponse;
	}
	
	
	public BotApiResponse reply(PostbackEvent event)throws IOException{
		BotApiResponse botApiResponse = null;
		String replyToken = event.getReplyToken();
		String data = event.getPostbackContent().getData();
		System.out.println("data====>>>"+data);
		TextMessage textMessage = TextMessage.builder().text("已為您保留").build();
		replyMessage = new ReplyMessage(replyToken,textMessage);
		try {
			botApiResponse = client.replyMessage(replyMessage).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return botApiResponse;
	}
	
	
	
}
