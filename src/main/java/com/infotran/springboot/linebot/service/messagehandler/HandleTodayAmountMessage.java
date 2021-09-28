package com.infotran.springboot.linebot.service.messagehandler;

import com.infotran.springboot.annotation.QuickReplyMode;
import com.infotran.springboot.annotation.quickreplyenum.ActionMode;
import com.infotran.springboot.confirmcase.model.ConfirmCase;
import com.infotran.springboot.linebot.service.BaseMessageHandler;
import com.infotran.springboot.linebot.service.messagehandler.enums.HandlerEnum;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * @author chris
 * 第一功能<br>
 * 編號: 1 <br>
 * 回覆今日新增的確診數目<br>
 *
 * */
@Slf4j
@Component(value = "HandleTodayAmount")
public class HandleTodayAmountMessage extends BaseMessageHandler {

    private static final String LOG_PREFIX = "HandleTodayAmount";

    @Override
    public String getClassName() {
        return HandlerEnum.getHandlerName(1);
    }

    /**
     * 處理(今日/昨日)新增確診數目
     * @
     */
    @Override
    @QuickReplyMode(mode = ActionMode.MESSAGE,label="昨日確診數",text="昨日確診數")
    protected List<TextMessage> textMessageReply(TextMessageContent event,String replyToken,String userId) {
            String receivedMessage = event.getText();
            StringBuilder message = new StringBuilder();
            TextMessage textMessage = null;
            ConfirmCase confirmCase;
            switch (receivedMessage) {
                case "查詢今日確診":
                    confirmCase = caseService.findByConfirmTime(LocalDate.now());
                    if (confirmCase != null) {
                        message.append("指揮中心快訊：今日新增" + confirmCase.getTodayAmount() + "例COVID-19確定病例。\n");
                        message.append("校正回歸數" + confirmCase.getReturnAmount() + "例。\n");
                        message.append("死亡人數" + confirmCase.getDeathAmount() + "例。");
                    } else {
                        message.append("本日確診數量尚未公布。");
                        log.info("{} 本日新增不存在", LOG_PREFIX);
                    }
                    textMessage = new TextMessage(message.toString());
                    return Collections.singletonList(textMessage);
                case "昨日確診數":
                    confirmCase = caseService.findByConfirmTime(LocalDate.now().minusDays(1));
                    if (confirmCase != null) {
                        message.append("指揮中心快訊：昨日新增" + confirmCase.getTodayAmount() + "例COVID-19確定病例。\n");
                        message.append("校正回歸數" + confirmCase.getReturnAmount() + "例。\n");
                        message.append("死亡人數" + confirmCase.getDeathAmount() + "例。");
                    } else {
                        message.append("昨日資訊異常。");
                        log.warn("{} 昨日新增不存在", LOG_PREFIX);
                    }
                    textMessage = new TextMessage(message.toString());
                    return Collections.singletonList(textMessage);
            }
        return null;
    }

    @Override
    protected List<TextMessage> textMessageReply(PostbackEvent event) {
        //不使用
        return null;
    }

    @Override
    protected List<LocationMessage> handleLocationMessageReply(LocationMessageContent event,String userId) {
        //不使用
        return null;
    }



}
