package com.infotran.springboot.linebot.service.messagehandler;

import com.infotran.springboot.annotation.QuickReplyMode;
import com.infotran.springboot.annotation.quickreplyenum.ActionMode;
import com.infotran.springboot.linebot.service.BaseMessageHandler;
import com.infotran.springboot.linebot.service.messagehandler.enums.HandlerEnum;
import com.infotran.springboot.webcrawler.confirmcase.model.ConfirmCase;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * 第一功能<br>
 * 編號: 1 <br>
 * 回覆今日新增的確診數目<br>
 * @author chris
 *
 * */
@Slf4j
@Component
public class HandleTodayAmountMessage extends BaseMessageHandler {

    private static final String LOG_PREFIX = "HandleTodayAmount";

    @Resource
    RedisTemplate<Object, ConfirmCase> confirmCaseRedisTemplate;

    @Override
    public HandlerEnum getHandler() {
        return HandlerEnum.HANDLE_TODAY_AMOUNT_MESSAGE;
    }

    @Override
    @QuickReplyMode(mode = ActionMode.MESSAGE,label="昨日確診數",text="昨日確診數")
    protected List<TextMessage> textMessageReply(TextMessageContent event,String replyToken,String userId) {
        String receivedMessage = event.getText();
        StringBuilder message = new StringBuilder();
        ConfirmCase confirmCase;
        switch (receivedMessage) {
            case "查詢今日確診":
                confirmCase = confirmCaseRedisTemplate.opsForValue().get("今日確診");
                if(confirmCase==null){
                    confirmCase = confirmCaseService.findByConfirmTime(LocalDate.now());
                }
                if (confirmCase != null) {
                    message.append("指揮中心快訊：今日新增").append(confirmCase.getTodayAmount()).append("例COVID-19確定病例。\n");
                    message.append("校正回歸數").append(confirmCase.getReturnAmount()).append("例。\n");
                    message.append("死亡人數").append(confirmCase.getDeathAmount()).append("例。\n\n");
                    message.append("參考指揮中心新聞網址:").append(confirmCase.getNewsUrl());
                } else {
                    message.append("本日確診數量尚未公布。");
                    log.warn("{} 本日新增不存在", LOG_PREFIX);
                }
                return Collections.singletonList(new TextMessage(message.toString()));
            case "昨日確診數":
                //todo caseService可以設非叢集索引
                confirmCase = confirmCaseService.findByConfirmTime(LocalDate.now().minusDays(1));
                if (confirmCase != null) {
                    message.append("指揮中心快訊：昨日新增").append(confirmCase.getTodayAmount()).append("例COVID-19確定病例。\n");
                    message.append("校正回歸數").append(confirmCase.getReturnAmount()).append("例。\n");
                    message.append("死亡人數").append(confirmCase.getDeathAmount()).append("例。");
                } else {
                    message.append("昨日資訊異常。");
                    log.warn("{} 昨日新增不存在", LOG_PREFIX);
                }
                return Collections.singletonList(new TextMessage(message.toString()));
            default:
        }
        return null;
    }

    @Override
    protected List<TextMessage> textMessageReply(PostbackEvent event) {
        //不使用
        return null;
    }

    @Override
    protected List<Message> handleImagemapMessageReply(PostbackEvent event) {
        //不使用
        return null;
    }

    @Override
    protected List<LocationMessage> handleLocationMessageReply(LocationMessageContent event,String userId) {
        //不使用
        return null;
    }



}
