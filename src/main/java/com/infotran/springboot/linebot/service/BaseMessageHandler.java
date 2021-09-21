package com.infotran.springboot.linebot.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.infotran.springboot.annotation.QuickReplyMode;
import com.infotran.springboot.annotation.quickreplyenum.ActionMode;
import com.infotran.springboot.confirmcase.service.ConfirmCaseService;
import com.infotran.springboot.linebot.service.messagehandler.HandleLocationMessage;
import com.infotran.springboot.linebot.service.messagehandler.HandleOtherMessage;
import com.infotran.springboot.linebot.service.messagehandler.HandleTestReplyMessage;
import com.infotran.springboot.linebot.service.messagehandler.HandleTodayAmountMessage;
import com.infotran.springboot.medicinestore.service.MedicineStoreService;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.*;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.*;
import com.linecorp.bot.model.message.quickreply.QuickReply;
import com.linecorp.bot.model.message.quickreply.QuickReplyItem;
import com.linecorp.bot.model.response.BotApiResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@Slf4j
@Component
public class BaseMessageHandler implements LineMessageClientInterface {

    private static String LOG_PREFIX = "BaseMessageHandler";

    @Resource
    public ConfirmCaseService caseService;

    @Resource
    public MedicineStoreService mService;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private HandleLocationMessage LocationReply;

    @Resource
    private HandleTestReplyMessage testReplyMessageHandler;

    @Resource
    private HandleTodayAmountMessage handleTodayAmount;

    @Resource
    private HandleOtherMessage handleOtherMessage;

    /**
     *  PostbackEvent分配器<br>
     *  根據PostbackEvent的data參數不同執行相對應的方法
     *  @return Map
     * */
    public Map<String,String> postBackReply(PostbackEvent event) throws IOException, NoSuchMethodException {
        Map<String,String> param = new HashMap<>();
        String replyToken = event.getReplyToken();
        String data = event.getPostbackContent().getData();
        StringBuilder message = new StringBuilder();
        log.info("{} event data: {}",LOG_PREFIX,data);
        switch (data){
            case "1" :
                handleTodayAmount.handleTodayAmountMessageRely(message,replyToken);
                break;
            case "2" :
                TextMessage textMessage =LocationReply.openMap();
                Method method = HandleLocationMessage.class.getDeclaredMethod("openMap");
                QuickReply quickReply = getQuickReplyMode(method);
                TextMessage newTextMessage = textMessage.toBuilder().quickReply(quickReply).build();
                this.reply(replyToken,newTextMessage);
                break;
            case "3" :
                break;
            case "4" :
                break;
            case "5" :
                break;
            case "6" :
                handleOtherMessage.handleOtherMessageReply(message,replyToken);
                break;
            case "next5":
                log.info("進入下五間");
                LocationReply.postbackEventUserDefined(replyToken);
                break;
            default:
                param.put(replyToken,data);
                log.info("PostBack event in default -> {}",param);
                return param;
        }
        return param;
    }

    /**
     *  MessageEvent分配器<br>
     *  根據傳送訊息調用不同物件執行相對應的方法
     *  @return BotApiResponse 響應物件
     * */
    public final <T extends MessageContent> BotApiResponse handleMessageEvent(MessageEvent<T> event) throws Exception{
        BotApiResponse botApiResponse = null;
        //replyToken
        String replyToken = event.getReplyToken();
        if (event.getMessage() instanceof TextMessageContent){
            //測試類
            String receivedMessage = ((TextMessageContent) event.getMessage()).getText();
            testReplyMessageHandler.testTextMessageReply(replyToken,receivedMessage);
        } else if (event.getMessage() instanceof LocationMessageContent) {
            //收到使用者傳送地圖
            Double lat1 = ((LocationMessageContent) event.getMessage()).getLatitude();
            Double long1 = ((LocationMessageContent) event.getMessage()).getLongitude();
            List<LocationMessage> locationMessage = LocationReply.handleLocationMessageReply(lat1,long1);
            Method handleLocationMethod = HandleLocationMessage.class.getDeclaredMethod("handleLocationMessageReply",Double.class, Double.class);
            //如果有@QuickReply自動產生QuickReply物件
            QuickReply quickReply = getQuickReplyMode(handleLocationMethod);
            List<Message> messageList = locationMessage.stream().map(x -> x.toBuilder().quickReply(quickReply).build()).map(Message.class::cast).collect(Collectors.toList());
            this.reply(replyToken,messageList);
        } else if (event.getMessage() instanceof StickerMessageContent) {
            StickerMessageContent stickerMessageContent = (StickerMessageContent) event.getMessage();
            handleSticker(replyToken,stickerMessageContent);
        }


        return botApiResponse;
    }

    /**
     * 測試用<br>
     * 處理使用者回傳的貼圖
     * (預設回復一模一樣的貼圖)
     * @param replyToken String
     * @param content StickerMessageContent
     * */
    public void handleSticker(String replyToken, StickerMessageContent content) {
        //回傳一模一樣的給用戶
        reply(replyToken, new StickerMessage(
                content.getPackageId(), content.getStickerId())
        );
    };

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
     * @param replyToken
     * @param messages
     * @param notificationDisabled
     * */
    protected BotApiResponse reply(@NonNull String replyToken,
                       @NonNull List<Message> messages,
                       boolean notificationDisabled) {
        try {
            BotApiResponse apiResponse = client
                    .replyMessage(new ReplyMessage(replyToken, messages, notificationDisabled))
                    .get();
            log.info("{} replyMessage物件: {}",LOG_PREFIX,apiResponse);
            return apiResponse;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 回應文字訊息(限998字)
     * @param replyToken String
     * @param message String
     * */
    protected void replyText(@NonNull String replyToken, @NonNull String message) {
        if (replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken must not be empty");
        }
        if (message.length() > 1000) {
            message = message.substring(0, 1000 - 2) + "……";
        }
        this.reply(replyToken, new TextMessage(message));
    }


    /**
     *  取得註解QuickReplyMode並建立QuickReply物件
     *  @param method Method
     *  @return QuickReply
     * */
    public QuickReply getQuickReplyMode(Method method) {
        QuickReplyMode quickReplyMode = method.getAnnotation(QuickReplyMode.class);
        if (quickReplyMode == null) {
            //不使用sign要自己寫QuickReply
            return null;
        }
        String label = quickReplyMode.label();
        String data = quickReplyMode.data();
        String displayText = quickReplyMode.displayText();
        log.info("getQuickReplyMode ==> label: {} data: {} displaytext: {}",label,data,displayText);
        if (ActionMode.POSTBACK.equals(quickReplyMode.mode())){
            List<QuickReplyItem> items = Arrays.<QuickReplyItem>asList(
                    QuickReplyItem.builder()
                            .action(PostbackAction.builder()
                                    .label(label)
                                    .data(data)
                                    .displayText(displayText)
                                    .build())
                            .build()
            );
            return QuickReply.items(items);
        } else if (ActionMode.LOCATION.equals(quickReplyMode.mode())) {
            List<QuickReplyItem> items = Arrays.<QuickReplyItem>asList(
                    QuickReplyItem.builder()
                            .action(LocationAction.withLabel(label))
                            .build()
            );
            return QuickReply.items(items);
        } else if (ActionMode.MESSAGE.equals(quickReplyMode.mode())) {
            //TODO MESSAGE Action 尚未實作
        }

        return null;
    }


}
