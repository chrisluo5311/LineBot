package com.infotran.springboot.linebot.service.messagehandler;

import com.infotran.springboot.linebot.service.BaseMessageHandler;
import com.infotran.springboot.linebot.service.messagehandler.enums.HandlerEnum;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImagemapMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author chris
 * 第四功能
 * 編號: 4 <br>
 * 1.
 *
 * */
@Slf4j
@Component
public class HandleSVGMessage extends BaseMessageHandler {

    private String LOG_PREFFIX = "HandleSVGMessage";


    @Override
    public String getClassName() {
        return HandlerEnum.getHandlerName(5);
    }

    @Override
    protected List<TextMessage> textMessageReply(TextMessageContent event, String replyToken, String userId) {
        return null;
    }

    @Override
    protected List<TextMessage> textMessageReply(PostbackEvent event) {
        return null;
    }

    @Override
    protected <T extends MessageContent> List<LocationMessage> handleLocationMessageReply(LocationMessageContent event, String userId) {
        //不使用
        return null;
    }

    @Override
    protected List<ImagemapMessage> handleImagemapMessageReply(PostbackEvent event) {
        String data = event.getPostbackContent().getData();
        switch (data){
            case "施打疫苗統計":
        }
        return null;
    }



}
