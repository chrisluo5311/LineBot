package com.infotran.springboot.LineBot.service;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;

public interface LineMessageInterface {
	
	
	LineMessagingClient client = LineMessagingClient
	        .builder("ww5P0UIpl9jxB+a+dkO44euiFLfNhbUW+uqNjUue+dk/evwcYs/7ySV6iwwatmQwZExhR3polWKVDIJHxXMSehCExemJ4RRny0uFpYdWgwsp+Mi+643shY6fzzH/Ttqbn9iHPB8xm7GAlg7UH/klIwdB04t89/1O/w1cDnyilFU=")
	        .build();

	
}
