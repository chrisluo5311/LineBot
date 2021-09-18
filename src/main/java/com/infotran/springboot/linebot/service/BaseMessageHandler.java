package com.infotran.springboot.linebot.service;


import com.infotran.springboot.confirmcase.model.ConfirmCase;
import com.infotran.springboot.confirmcase.service.ConfirmCaseService;
import com.infotran.springboot.medicinestore.service.MedicineStoreService;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.LocationAction;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.quickreply.QuickReply;
import com.linecorp.bot.model.message.quickreply.QuickReplyItem;
import com.linecorp.bot.model.response.BotApiResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

@Service
@Slf4j
public abstract class BaseMessageHandler implements LineReplyMessageHandler {

    private static String LOG_PREFIX = "BaseMessageHandler";

    @Resource
    public ConfirmCaseService caseService;

    @Resource
    public MedicineStoreService mService;

    /**
     * 處理使用者回傳的現在位置
     * @param event LocationMessageContent
     * */
    public abstract void handleLocationMessageRely(MessageEvent<LocationMessageContent> event);

    /**
     * 測試用<br>
     * 處理使用者回傳的文字訊息
     * @param event TextMessageContent
     * */
    public abstract BotApiResponse testTextMessageReply(MessageEvent<TextMessageContent> event);

    /**
     * 測試用<br>
     * 處理使用者回傳的貼圖
     * @param replyToken String
     * @param content StickerMessageContent
     * */
    public abstract void handleSticker(String replyToken, StickerMessageContent content);

    @Override
    public void postBackReply(PostbackEvent event)throws IOException {
        String replyToken = event.getReplyToken();
        String data = event.getPostbackContent().getData();
        StringBuilder message = new StringBuilder();
        switch (data){
            case "1" :
                ConfirmCase confirmCase = caseService.findByConfirmTime(LocalDate.now());
                if (confirmCase!=null){
                    message.append("指揮中心快訊：今日新增"+ confirmCase.getTodayAmount() + "例COVID-19確定病例。\n");
                    message.append("校正回歸數"+confirmCase.getReturnAmount()+"例。\n");
                    message.append("死亡人數"+confirmCase.getDeathAmount()+"例。");
                }else {
                    message.append("本日確診數量尚未公布。");
                }
                this.replyText(replyToken,message.toString());
                break;
            case "2" :
                this.reply(replyToken,new OpenMapQuickReplySupplier().get());
                break;
            case "3" :
                break;
            case "4" :
                break;
            case "5" :
                break;
            case "6" :
                message.append("⊂_ヽ\n" +
                        "　 ＼＼ ＿\n" +
                        "　　 ＼(　•_•) F\n" +
                        "　　　 <　⌒ヽ A\n" +
                        "　　　/ 　 へ＼ B\n" +
                        "　　 /　　/　＼＼ U\n" +
                        "　　 ﾚ　ノ　　 ヽ_つ L\n" +
                        "　　/　/ O\n" +
                        "　 /　/| U\n" +
                        "　(　(ヽ S\n" +
                        "　|　|、＼\n" +
                        "　| 丿 ＼ ⌒)\n" +
                        "　| |　　) /\n" +
                        "`ノ )　　Lﾉ\n" +
                        "(_／");
                this.replyText(replyToken,message.toString());
                break;

        }
    }

    /**
     * 回應訊息(replyToken,Message物件)<br>
     * 將Message物件轉成List
     * @param replyToken
     * @param message 單一Message物件
     * */
    protected void reply(@NonNull String replyToken, @NonNull Message message) {
        reply(replyToken, Collections.singletonList(message));
    }

    /**
     * 回應訊息(replyToken,List集合的Message物件)<br>
     * @param replyToken String
     * @param messages List
     * */
    protected void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
        reply(replyToken, messages, false);
    }

    /**
     * 回應訊息
     * @param replyToken
     * @param messages
     * @param notificationDisabled
     * */
    protected void reply(@NonNull String replyToken,
                       @NonNull List<Message> messages,
                       boolean notificationDisabled) {
        try {
            BotApiResponse apiResponse = client
                    .replyMessage(new ReplyMessage(replyToken, messages, notificationDisabled))
                    .get();
            log.info("{} replyMessage物件: {}",LOG_PREFIX,apiResponse);
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
     * 內部類別<br>
     * 打開地圖快捷鍵的提供器
     * */
    private class OpenMapQuickReplySupplier implements Supplier<Message> {
        @Override
        public Message get() {
            final List<QuickReplyItem> items = Arrays.<QuickReplyItem>asList(
                    QuickReplyItem.builder()
                            .action(LocationAction.withLabel("打開地圖"))
                            .build()
            );

            final QuickReply quickReply = QuickReply.items(items);

            return TextMessage
                    .builder()
                    .text("請點選下方快捷鍵")
                    .quickReply(quickReply)
                    .build();
        }
    }



}
