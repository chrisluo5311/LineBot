package com.infotran.springboot.linebot.service.messagehandler;

import com.infotran.springboot.confirmcase.model.ConfirmCase;
import com.infotran.springboot.linebot.service.BaseMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * @author chris
 * 第一功能<br>
 * 回覆今日新增的確診數目<br>
 *
 * */
@Slf4j
@Component(value = "HandleTodayAmount")
public class HandleTodayAmountMessage extends BaseMessageHandler {

    private static final String LOG_PREFIX = "HandleTodayAmount";

    /**
     * 處理今日新增確診數目
     * @param replyToken String
     */
    public void handleTodayAmountMessageRely(StringBuilder message,String replyToken){
        ConfirmCase confirmCase = caseService.findByConfirmTime(LocalDate.now());
        if (confirmCase!=null){
            message.append("指揮中心快訊：今日新增"+ confirmCase.getTodayAmount() + "例COVID-19確定病例。\n");
            message.append("校正回歸數"+confirmCase.getReturnAmount()+"例。\n");
            message.append("死亡人數"+confirmCase.getDeathAmount()+"例。");
        }else {
            message.append("本日確診數量尚未公布。");
            log.info("{} 本日新增不存在",LOG_PREFIX);
        }
        replyText(replyToken,message.toString());
    }

}
