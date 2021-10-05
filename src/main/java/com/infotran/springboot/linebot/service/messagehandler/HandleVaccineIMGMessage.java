package com.infotran.springboot.linebot.service.messagehandler;

import com.infotran.springboot.annotation.MultiQuickReply;
import com.infotran.springboot.annotation.QuickReplyMode;
import com.infotran.springboot.annotation.quickreplyenum.ActionMode;
import com.infotran.springboot.linebot.service.BaseMessageHandler;
import com.infotran.springboot.linebot.service.messagehandler.enums.HandlerEnum;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chris
 * 第五功能-处理疫苗统计图
 * 編號: 5 <br>
 *
 *
 * */
@Slf4j
@Component
public class HandleVaccineIMGMessage extends BaseMessageHandler {

    private String LOG_PREFFIX = "[HandleVaccineIMGMessage]";

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
    @MultiQuickReply(value = {
            @QuickReplyMode(mode= ActionMode.MESSAGE,label = "查看各梯次COVID-19公费疫苗涵盖率",text = "查看各梯次COVID-19公费疫苗涵盖率"),
            @QuickReplyMode(mode=ActionMode.MESSAGE,label="各縣市COVID-19疫苗涵蓋率",text = "各縣市COVID-19疫苗涵蓋率")
    })
    protected List<Message> handleImagemapMessageReply(PostbackEvent event) {
        String data = event.getPostbackContent().getData();
        switch (data){
            case "施打疫苗統計":
                List<ImageMessage> imgList = new ArrayList<>();
                URI vaccineImgURI1 = createUri("/static/cumulativeVaccined.jpg");
                URI vaccineImgURI2 = createUri("/static/eachBatchCoverage.jpg");
                ImageMessage imgMessage = ImageMessage.builder().previewImageUrl(vaccineImgURI1).originalContentUrl(vaccineImgURI1).build();
                ImageMessage imgMessage2 = ImageMessage.builder().previewImageUrl(vaccineImgURI2).originalContentUrl(vaccineImgURI2).build();
                imgList.add(imgMessage);
                imgList.add(imgMessage2);
                List<Message> replyList = imgList.stream().map(Message.class::cast)
                        .collect(Collectors.toList());
                return replyList;
        }
        return null;
    }

    private static URI createUri(String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .scheme("https")
                .path(path).build()
                .toUri();
    }




}
