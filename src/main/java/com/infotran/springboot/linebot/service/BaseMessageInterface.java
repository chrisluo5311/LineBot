package com.infotran.springboot.linebot.service;

import com.infotran.springboot.linebot.service.messagehandler.enums.HandlerEnum;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.response.BotApiResponse;

/**
 * BaseMessageHandler 規範層
 * @author chris
 */
public interface BaseMessageInterface {

	/**
	 *  查詢實現類
	 *  @param handlerEnum 實作類HandlerEnum
	 *  @return boolean
	 * */
	boolean canSupport(HandlerEnum handlerEnum);


	/**
	 *  實作才有值
	 *  @return HandlerEnum
	 *
	 * */
	default HandlerEnum getHandler(){
		return null;
	}

	/**
	 *  PostbackEvent分配器<br>
	 *  根據PostbackEvent的data參數不同而執行相對應的方法
	 *  @param event PostbackEvent
	 *  @param data  Menu回傳字段
	 *  @return BotApiResponse 響應物件
	 *  @throws  Exception LineBotException
	 * */
	BotApiResponse postBackReply(PostbackEvent event,String data) throws Exception;

	/**
	 *  MessageEvent分配器<br>
	 *  根據傳送訊息調用不同物件執行相對應的方法
	 *  @param event MessageEvent
	 *  @return BotApiResponse 響應物件
	 *  @throws  Exception LineBotException
	 * */
	<T extends MessageContent> BotApiResponse handleMessageEvent(MessageEvent<T> event) throws Exception;
}
