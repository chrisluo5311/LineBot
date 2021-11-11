package com.infotran.springboot.linebot.service;

import com.infotran.springboot.annotation.MultiQuickReply;
import com.infotran.springboot.annotation.QuickReplyMode;
import com.infotran.springboot.annotation.quickreplyenum.ActionMode;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.LocationAction;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.quickreply.QuickReply;
import com.linecorp.bot.model.message.quickreply.QuickReplyItem;
import com.linecorp.bot.model.response.BotApiResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author chris
 */
@Slf4j
public abstract class BaseMessageUtil implements LineClientInterface {

    private static final String LOG_PREFIX = "[BaseMessageUtil]";

    /**
     * 最大MESSAGE傳送數量
     * */
    private static final Integer MAX_MESSAGE_AMOUNT = 1000;

    /**
     * 回應訊息(replyToken,Message物件)<br>
     * 將Message物件轉成List
     * @param replyToken
     * @param message 單一Message物件
     * */
    protected BotApiResponse reply(@NonNull String replyToken, @NonNull Message message) {
        return reply(replyToken, Collections.singletonList(message));
    }

    /**
     * 回應訊息(replyToken,List集合的Message物件)<br>
     * @param replyToken String
     * @param messages List
     * */
    protected BotApiResponse reply(@NonNull String replyToken, @NonNull List<Message> messages) {
        return reply(replyToken, messages, false);
    }

    /**
     * 回應訊息
     * @param replyToken  replyToken
     * @param messages 回傳訊息 List
     * @param notificationDisabled notificationDisabled
     * @return BotApiResponse
     * */
    private BotApiResponse reply(@NonNull String replyToken,
                                   @NonNull List<Message> messages,
                                   boolean notificationDisabled) {
        try {
            log.info("======================發送post請求==========================");
            BotApiResponse apiResponse = CLIENT
                    .replyMessage(new ReplyMessage(replyToken, messages, notificationDisabled))
                    .get();
            log.info("========================請求結束============================");
            log.info("[{}] 送出給使用者的訊息: {}",LOG_PREFIX,messages);
            return apiResponse;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 回應文字訊息(小於1000字)
     * @param replyToken String
     * @param message String
     * @return BotApiResponse
     * */
    protected BotApiResponse replyText(@NonNull String replyToken, @NonNull String message) {
        if (replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken must not be empty");
        }

        if (message.length() > MAX_MESSAGE_AMOUNT) {
            message = message.substring(0, 1000 - 2) + "……";
        }
        return this.reply(replyToken, new TextMessage(message));
    }



    /**
     * 解析@QuickReply、合併Message集合並調用reply回覆<br>
     * @param method 方法
     * @param messageList messageList
     * @param replyToken replyToken
     * @return BotApiResponse
     *
     * */
    protected <T extends Message> BotApiResponse executeReply(Method method, List<T> messageList, String replyToken){
        if(messageList.stream().allMatch(TextMessage.class::isInstance)) {
            List<TextMessage> textMessageList = (List<TextMessage>) messageList;
            //如果有@QuickReply自動產生QuickReply物件
            QuickReply quickReply = getQuickReply(method);
            List<Message> messages = textMessageList.stream()
                    .map(x -> x.toBuilder().quickReply(quickReply).build())
                    .map(Message.class::cast)
                    .collect(Collectors.toList());
            return this.reply(replyToken,messages);
        } else if (messageList.stream().allMatch(LocationMessage.class::isInstance)){
            List<LocationMessage> locationMessageList = (List<LocationMessage>) messageList;
            //如果有@QuickReply自動產生QuickReply物件
            QuickReply quickReply = getQuickReply(method);
            List<Message> messages = locationMessageList.stream()
                    .map(x -> x.toBuilder().quickReply(quickReply).build())
                    .map(Message.class::cast)
                    .collect(Collectors.toList());
            return this.reply(replyToken,messages);
        }
        return null;
    }


    /**
     *  先檢查是否有 @MultiQuickReply <br>
     *  再取註解 QuickReplyMode 並建立一個或多個 QuickReplyItem <br>
     *  最後回傳 QuickReply 物件
     *
     *  @param method Method
     *  @return QuickReply
     * */
    protected QuickReply getQuickReply(Method method) {
        MultiQuickReply multiQuickReply = method.getAnnotation(MultiQuickReply.class);
        if (Objects.isNull(multiQuickReply)){
            //單一
            QuickReplyMode quickReplyMode = method.getAnnotation(QuickReplyMode.class);
            if (Objects.isNull(quickReplyMode)) {
                //不使用註解要自己寫QuickReply
                return null;
            }
            QuickReplyItem quickReplyItem = getQuickReply(quickReplyMode);
            log.info("[{}] 回傳單一的QuickReplyItem物件: {}",LOG_PREFIX,quickReplyItem);
            return QuickReply.builder().item(quickReplyItem).build();
        } else {
            //多個
            List<QuickReplyItem> quickReplyItemList = new ArrayList<>();
            for (QuickReplyMode quickReplyMode1 : multiQuickReply.value()){
                QuickReplyItem item = getQuickReply(quickReplyMode1);
                log.info("[{}] 回傳多個的QuickReplyItem物件: {}",LOG_PREFIX,item);
                quickReplyItemList.add(item);
            }
            return QuickReply.builder().items(quickReplyItemList).build();
        }
    }

    /**
     * 依據不同的ActionMode
     * 建立不同Action的QuickReplyItem
     * @param quickReplyMode QuickReplyMode
     * @return QuickReplyItem
     *
     * */
    protected QuickReplyItem getQuickReply(QuickReplyMode quickReplyMode){
        ActionMode mode = quickReplyMode.mode();
        String label = quickReplyMode.label();
        String data = quickReplyMode.data();
        String displayText = quickReplyMode.displayText();
        String text = quickReplyMode.text();
        log.info("[{}] @QuickReplyMode註解裡的參數 mode: {}, label: {}, data: {}, displayText: {},text: {}",LOG_PREFIX,mode,label,data,displayText,text);
        //回傳QuickReplyItem
        switch (mode){
            case POSTBACK:
                return QuickReplyItem.builder()
                        .action(PostbackAction.builder()
                                .label(label)
                                .data(data)
                                .displayText(displayText)
                                .build())
                        .build();
            case LOCATION:
                return QuickReplyItem.builder()
                        .action(LocationAction.withLabel(label))
                        .build();
            case MESSAGE:
                return QuickReplyItem.builder()
                        .action(new MessageAction(label,text))
                        .build();
        }
        return null;
    }
}
