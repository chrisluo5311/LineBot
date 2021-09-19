package com.infotran.springboot.linebot.service;

import com.linecorp.bot.client.LineBlobClient;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.event.PostbackEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
public interface LineReplyMessageInterface {
	
	LineMessagingClient client = LineMessagingClient
	        .builder("ww5P0UIpl9jxB+a+dkO44euiFLfNhbUW+uqNjUue+dk/evwcYs/7ySV6iwwatmQwZExhR3polWKVDIJHxXMSehCExemJ4RRny0uFpYdWgwsp+Mi+643shY6fzzH/Ttqbn9iHPB8xm7GAlg7UH/klIwdB04t89/1O/w1cDnyilFU=")
	        .build();
	
	
	LineBlobClient blobClient = LineBlobClient
			.builder("ww5P0UIpl9jxB+a+dkO44euiFLfNhbUW+uqNjUue+dk/evwcYs/7ySV6iwwatmQwZExhR3polWKVDIJHxXMSehCExemJ4RRny0uFpYdWgwsp+Mi+643shY6fzzH/Ttqbn9iHPB8xm7GAlg7UH/klIwdB04t89/1O/w1cDnyilFU=")
			.build();


	/**
	 * RichMenu控制台: "1":今日確診
	 *				  "2":買口罩
	 *				  "3":位置狀況
	 *				  "4":國內外疫情
	 *				  "5":施打疫苗統計
	 *				  "6":其他
	 * @param postbackEvent
	 * */
	void postBackReply(PostbackEvent postbackEvent) throws Exception;
}
