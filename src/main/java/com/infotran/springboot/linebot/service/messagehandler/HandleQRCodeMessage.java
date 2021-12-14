package com.infotran.springboot.linebot.service.messagehandler;

import com.infotran.springboot.linebot.service.BaseMessageHandler;
import com.infotran.springboot.linebot.service.messagehandler.enums.HandlerEnum;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
/**
 * 第三功能 <br>
 * 掃描QRCode <br>
 * 編號: 3 <br>
 * 1.打開用戶鏡頭
 *
 * @author chris
 */
@Slf4j
@Component
public class HandleQRCodeMessage  extends BaseMessageHandler {
    @Override
    public HandlerEnum getHandler() {
        return HandlerEnum.HANDLE_QRCODE;
    }

    @Override
    protected List<TextMessage> textMessageReply(TextMessageContent event, String replyToken, String userId) {
        String text = event.getText();
        switch (text){
            case "掃描QRCode":
                TextMessage textMessage = openCamera();
                reply(replyToken,textMessage);
            default:
        }
        return null;
    }

    @Override
    protected List<TextMessage> textMessageReply(PostbackEvent event) {
        return null;
    }

    @Override
    protected <T extends MessageContent> List<LocationMessage> handleLocationMessageReply(LocationMessageContent event, String userId) {
        return null;
    }

    @Override
    protected List<TemplateMessage> handleImagemapMessageReply(PostbackEvent event) {
        return null;
    }
}
