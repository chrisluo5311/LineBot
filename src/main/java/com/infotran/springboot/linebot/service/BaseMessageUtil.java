package com.infotran.springboot.linebot.service;

import com.infotran.springboot.annotation.MultiQuickReply;
import com.infotran.springboot.annotation.QuickReplyMode;
import com.infotran.springboot.annotation.quickreplyenum.ActionMode;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.CameraAction;
import com.linecorp.bot.model.action.LocationAction;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.message.*;
import com.linecorp.bot.model.message.quickreply.QuickReply;
import com.linecorp.bot.model.message.quickreply.QuickReplyItem;
import com.linecorp.bot.model.response.BotApiResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 回覆訊息所需的方法<br>
 * ex.回覆文字訊息、貼圖、打開地圖、快捷鍵...等等
 * @author chris
 */
@Slf4j
public abstract class BaseMessageUtil implements LineClientInterface {

    /** 最大MESSAGE傳送數量 */
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
            log.info("送出給使用者的訊息: {}",messages);
            return apiResponse;
        } catch (InterruptedException | ExecutionException e) {
            log.error("發送post請求 失敗:{}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    protected BotApiResponse sendPushMessage(PushMessage pushMessage){
        try {
            log.info("======================發送post請求==========================");
            BotApiResponse apiResponse = CLIENT
                    .pushMessage(pushMessage)
                    .get();
            log.info("========================請求結束============================");
            log.info("送出push message 給自己的訊息: {}",pushMessage);
            return apiResponse;
        } catch (InterruptedException | ExecutionException e) {
            log.error("發送post請求 失敗:{}",e.getMessage());
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
     * 處理使用者回傳的貼圖<br>
     * (預設回復一模一樣的貼圖)
     * @param replyToken replyToken
     * @param content StickerMessageContent
     * */
    public void handleSticker(String replyToken, StickerMessageContent content) {
        //回傳一模一樣的給用戶
        reply(replyToken, new StickerMessage(
                content.getPackageId(), content.getStickerId())
        );
    }

    /**
     * 打開地圖
     * @return TestMessage 訊息帶打開地圖動作的QuickReply
     *
     * */
    public TextMessage openMap() {
        final List<QuickReplyItem> items = List.of(
                QuickReplyItem.builder()
                        .action(LocationAction.withLabel("定位"))
                        .build()
        );

        final QuickReply quickReply = QuickReply.items(items);

        return TextMessage.builder().text("點選下方打開地圖").quickReply(quickReply).build();
    }

    /**
     * 打開相機
     * @return TestMessage 訊息帶打開相機動作的QuickReply
     *
     * */
    public TextMessage openCamera(){
        final List<QuickReplyItem> items = List.of(
                QuickReplyItem.builder()
                        .action(CameraAction.builder().label("相機").build())
                        .build()
        );

        final QuickReply quickReply = QuickReply.items(items);

        return TextMessage.builder().text("點選下方打開相機").quickReply(quickReply).build();
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
        } else if (messageList.stream().allMatch(TemplateMessage.class::isInstance)){
            List<TemplateMessage> templateMessageList = (List<TemplateMessage>) messageList;
            //如果有@QuickReply自動產生QuickReply物件
            QuickReply quickReply = getQuickReply(method);
            List<Message> messages = templateMessageList.stream()
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
            log.info("回傳單一的QuickReplyItem物件: {}",quickReplyItem);
            return QuickReply.builder().item(quickReplyItem).build();
        } else {
            //多個
            List<QuickReplyItem> quickReplyItemList = Arrays.stream(multiQuickReply.value()).map(this::getQuickReply).collect(Collectors.toList());
            return QuickReply.builder().items(quickReplyItemList).build();
        }
    }

    /**
     * 依據不同的ActionMode <br>
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
        log.info("@QuickReplyMode註解裡的參數 mode: {}, label: {}, data: {}, displayText: {},text: {}",mode,label,data,displayText,text);
        //回傳QuickReplyItem
        return mode.getQuickReplyItem(label,data,displayText,text);
    }
}
