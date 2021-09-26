package com.infotran.springboot.linebot.service;

import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.response.BotApiResponse;

public interface BaseMessageInterface {

	/**
	 *  查詢實現類
	 *  @return
	 * */
	boolean canSupport(String className);


	/**
	 *  實作才有值
	 *
	 * */
	default String getClassName(){
		return null;
	}

	/**
	 *  PostbackEvent分配器<br>
	 *  根據PostbackEvent的data參數不同執行相對應的方法
	 *  @return Map
	 * */
	BotApiResponse postBackReply(PostbackEvent event) throws Exception;

	/**
	 *  MessageEvent分配器<br>
	 *  根據傳送訊息調用不同物件執行相對應的方法
	 *  @return BotApiResponse 響應物件
	 * */
	<T extends MessageContent> BotApiResponse handleMessageEvent(MessageEvent<T> event) throws Exception;
}
