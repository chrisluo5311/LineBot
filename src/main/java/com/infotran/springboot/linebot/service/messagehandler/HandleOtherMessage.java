package com.infotran.springboot.linebot.service.messagehandler;

import com.infotran.springboot.linebot.service.BaseMessageHandler;
import com.infotran.springboot.linebot.service.messagehandler.enums.HandlerEnum;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author chris
 * 第六功能
 * 編號: 6 <br>
 * 處理其他功能
 *
 * */
@Slf4j
@Component(value = "HandleOtherMessage")
public class HandleOtherMessage extends BaseMessageHandler {

    @Override
    public String getClassName() {
        return HandlerEnum.getHandlerName(6);
    }

    @Override
    protected List<TextMessage> textMessageReply(TextMessageContent event, String replyToken) {
        return null;
    }

    /**
     * 處理其他
     * @param event PostbackEvent
     * */
    @Override
    protected List<TextMessage> textMessageReply(PostbackEvent event) {
        StringBuilder message = new StringBuilder();
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
        TextMessage text = TextMessage.builder().text(message.toString()).build();
        return Collections.singletonList(text);
    }

    @Override
    protected List<LocationMessage> handleLocationMessageReply(LocationMessageContent event) {
        //不使用
        return null;
    }






}
