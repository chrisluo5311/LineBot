package com.infotran.springboot.linebot.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.infotran.springboot.annotation.MultiQuickReply;
import com.infotran.springboot.annotation.QuickReplyMode;
import com.infotran.springboot.annotation.quickreplyenum.ActionMode;
import com.infotran.springboot.confirmcase.service.ConfirmCaseService;
import com.infotran.springboot.medicinestore.service.MedicineStoreService;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.LocationAction;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.quickreply.QuickReply;
import com.linecorp.bot.model.message.quickreply.QuickReplyItem;
import com.linecorp.bot.model.response.BotApiResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@Slf4j
@Component
public abstract class BaseMessageHandler implements BaseMessageInterface,LineClientInterface {

    private static String LOG_PREFIX = "BaseMessageHandler";

    @Resource
    public ConfirmCaseService caseService;

    @Resource
    public MedicineStoreService mService;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean canSupport(String className) {
        return getClassName().equals(className);
    }

    /**
     * 處理TextMessageContent文字訊息<br>
     * 可搭配使用的annotation : {@link QuickReplyMode}
     *
     * @param event MessageEvent
     * @return
     *
     * */
    protected abstract List<TextMessage> textMessageReply(TextMessageContent event,String replyToken);

    /**
     * 處理PostbackEvent文字訊息<br>
     * 可搭配使用的annotation : {@link QuickReplyMode}
     *
     * @param event PostbackEvent
     * @return
     *
     * */
    protected abstract List<TextMessage> textMessageReply(PostbackEvent event);

    /**
     * 處理使用者回傳的現在位置
     *
     * @param event  MessageEvent
     */
    protected abstract <T extends MessageContent> List<LocationMessage> handleLocationMessageReply(LocationMessageContent event);

    @Override
    public BotApiResponse postBackReply(PostbackEvent event) throws IOException, NoSuchMethodException {
        BotApiResponse botApiResponse = null;
        String replyToken = event.getReplyToken();
        String data = event.getPostbackContent().getData();
        StringBuilder message = new StringBuilder();
        log.info("{} event data: {}",LOG_PREFIX,data);
        switch (data){
            case "國內外疫情" :
                break;
            case "施打疫苗統計" :
                break;
            case "其他" :
                List<Message> textList = textMessageReply(event).stream().collect(Collectors.toList());
                botApiResponse = this.reply(replyToken,textList);
                break;
        }
        return botApiResponse;
    }

    /**
     * 回覆順序: 處理文字訊息(可null) -> 功能訊息(確診數目、藥局地址、統計圖、貼圖...等等)
     *
     * */
    @Override
    public final <T extends MessageContent> BotApiResponse handleMessageEvent(MessageEvent<T> event) throws Exception{
        BotApiResponse botApiResponse = null;
        List<Message> replyMessageList = new ArrayList<>();
        //replyToken
        String replyToken = event.getReplyToken();
        if(event.getMessage() instanceof TextMessageContent){
            //1. 處理文字(可null)
            List<TextMessage> textMessageList = textMessageReply((TextMessageContent)event.getMessage(),replyToken);
            if (Objects.nonNull(textMessageList)){
                Method textMethod = this.getClass().getDeclaredMethod("textMessageReply",TextMessageContent.class,String.class);
                botApiResponse =execute(textMethod,textMessageList,replyToken);
            }
        } else if (event.getMessage() instanceof LocationMessageContent) {
            //2. 功能訊息-(處理使用者地址並回傳藥局資訊/可null)
            List<LocationMessage> locationMessageList = handleLocationMessageReply((LocationMessageContent) event.getMessage());
            if(Objects.nonNull(locationMessageList)){
                Method locationMethod = this.getClass().getDeclaredMethod("handleLocationMessageReply",MessageEvent.class);
                botApiResponse = execute(locationMethod,locationMessageList,replyToken);
            }
        } else if (event.getMessage() instanceof  StickerMessageContent) {
            //處理貼圖(如果是官方貼圖，預設回覆一樣)
            handleSticker(replyToken, (StickerMessageContent) event.getMessage());
        }
        log.info("{} 回覆物件botApiResponse: {}",LOG_PREFIX,botApiResponse);
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
     * 解析@QuickReply、合併Message集合並調用reply回覆<br>
     * @param method
     * @param messageList
     * @param replyToken
     * @return BotApiResponse
     *
     * */
    private <T extends Message> BotApiResponse execute(Method method,List<T> messageList,String replyToken){
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
     * 回應文字訊息(小於1000字)
     * @param replyToken String
     * @param message String
     * */
    protected BotApiResponse replyText(@NonNull String replyToken, @NonNull String message) {
        if (replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken must not be empty");
        }
        if (message.length() > 1000) {
            message = message.substring(0, 1000 - 2) + "……";
        }
        return this.reply(replyToken, new TextMessage(message));
    }


    /**
     *  先檢查是否有 @MultiQuickReply <br>
     *  再取註解 QuickReplyMode 並建立一個或多個 QuickReplyItem <br>
     *  最後回傳 QuickReply 物件
     *
     *  @param method Method
     *  @return QuickReply
     * */
    private QuickReply getQuickReply(Method method) {
        MultiQuickReply multiQuickReply = method.getAnnotation(MultiQuickReply.class);
        if (Objects.isNull(multiQuickReply)){//單一
            QuickReplyMode quickReplyMode = method.getAnnotation(QuickReplyMode.class);
            if (Objects.isNull(quickReplyMode)) {
                //不使用sign要自己寫QuickReply
                return null;
            }
            QuickReplyItem quickReplyItem = getQuickReply(quickReplyMode);
            log.info("{} QuickReplyItem物件: {}",LOG_PREFIX,quickReplyItem);
            return QuickReply.builder().item(quickReplyItem).build();
        } else { //多個
            List<QuickReplyItem> quickReplyItemList = new ArrayList<>();
            for (QuickReplyMode quickReplyMode1 : multiQuickReply.value()){
                QuickReplyItem item = getQuickReply(quickReplyMode1);
                log.info("{} 多重註解裡的@QuickReplyMode {}",LOG_PREFIX,item);
                quickReplyItemList.add(item);
            }
            return QuickReply.builder().items(quickReplyItemList).build();
        }
    }


    private QuickReplyItem getQuickReply(QuickReplyMode quickReplyMode){
        ActionMode mode = quickReplyMode.mode();
        String label = quickReplyMode.label();
        String data = quickReplyMode.data();
        String displayText = quickReplyMode.displayText();
        String text = quickReplyMode.text();
        log.info("{} @QuickReplyMode註解裡的參數 mode: {}, label: {}, data: {}, displayText: {},text: {}",LOG_PREFIX,mode,label,data,displayText,text);
        //回傳QuickReplyItem
        switch (mode){
            case POSTBACK:
                QuickReplyItem item =QuickReplyItem.builder()
                        .action(PostbackAction.builder()
                                .label(label)
                                .data(data)
                                .displayText(displayText)
                                .build())
                        .build();
                return item;
            case LOCATION:
                QuickReplyItem item2 = QuickReplyItem.builder()
                        .action(LocationAction.withLabel(label))
                        .build();
                return item2;
            case MESSAGE:
                QuickReplyItem item3 = QuickReplyItem.builder()
                        .action(new MessageAction(label,text))
                        .build();
                return item3;
        }
        return null;
    }

}
