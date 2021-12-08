package com.infotran.springboot.linebot.service;

import com.infotran.springboot.annotation.QuickReplyMode;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

/**
 * 實作類需實做的方法
 * @author chris
 * */
@Slf4j
public abstract class BaseMessageTemplate extends BaseMessageUtil {

    /**
     * 處理TextMessageContent文字訊息<br>
     * 可搭配使用的annotation : {@link QuickReplyMode}
     *
     * @param event TextMessageContent
     * @param replyToken replyToken
     * @param userId 使用者id
     * @return List<TextMessage>
     *
     * */
    protected abstract List<TextMessage> textMessageReply(TextMessageContent event, String replyToken, String userId);

    /**
     * 處理PostbackEvent文字訊息<br>
     * 可搭配使用的annotation : {@link QuickReplyMode}
     *
     * @param event PostbackEvent
     * @return List<TextMessage>
     *
     * */
    protected abstract List<TextMessage> textMessageReply(PostbackEvent event);

    /**
     * 處理使用者回傳的現在位置
     *
     * @param event  LocationMessageContent
     * @param userId  使用者id
     * @return  List<LocationMessage>
     */
    protected abstract <T extends MessageContent> List<LocationMessage> handleLocationMessageReply(LocationMessageContent event, String userId);

    /**
     * 處理Imagemap回复
     * @param event  PostbackEvent
     * @return  List<Message>
     */
    protected abstract List<Message> handleImagemapMessageReply(PostbackEvent event);
}
