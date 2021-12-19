package com.infotran.springboot.linebot.service.messagehandler;

import com.infotran.springboot.annotation.MultiQuickReply;
import com.infotran.springboot.annotation.QuickReplyMode;
import com.infotran.springboot.annotation.quickreplyenum.ActionMode;
import com.infotran.springboot.linebot.service.BaseMessageHandler;
import com.infotran.springboot.linebot.service.messagehandler.enums.HandlerEnum;
import com.infotran.springboot.util.HandleFileUtil;
import com.infotran.springboot.webcrawler.multicountry.countryenum.CountryEnum;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 第六功能 <br>
 * 編號: 6 <br>
 * 處理其他功能
 * @author chris
 * */
@Slf4j
@Component
public class HandleOtherMessage extends BaseMessageHandler {

    @Value("${line.my.userId}")
    String userId;

    @Override
    public HandlerEnum getHandler() {
        return HandlerEnum.HANDLE_OTHER_MESSAGE;
    }

    @Override
    protected List<TextMessage> textMessageReply(TextMessageContent event, String replyToken,String userId) {
        return null;
    }

    @Override
    protected List<TextMessage> textMessageReply(PostbackEvent event) {
       return null;
    }

    /**
     * 處理其他
     * @param event PostbackEvent
     * */
    @Override
    @MultiQuickReply(value = {
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "香港",data = "香港",displayText = "香港"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "新加坡",data = "新加坡",displayText = "新加坡"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "問題回報",data = "問題回報",displayText = "問題回報"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "關於作者",data = "關於作者",displayText = "關於作者")
    })
    protected List<TemplateMessage> handleImagemapMessageReply(PostbackEvent event) {
        String data = event.getPostbackContent().getData();
        URI questionMarkUri = HandleFileUtil.createUri("/static/questionMark.jpg");
        URI aboutMeUri = HandleFileUtil.createUri("/static/aboutme.jpg");
        switch (data){
            case "其他":
                //第一個template: 五個沒有在第四功能顯示的國家
                //日本 泰國 韓國 香港 新加坡
                URI globalUri = CountryEnum.GLOBAL.getUri();
                //建構CarouselTemplate
                CarouselTemplate carouselTemplate = new CarouselTemplate(
                        Arrays.asList(
                                new CarouselColumn(globalUri, "其他亞洲疫情統計", "更多地區疫情點擊下方", Arrays.asList(
                                        PostbackAction.builder().data(CountryEnum.JAPAN.getName()).label(CountryEnum.JAPAN.getName()).displayText(CountryEnum.JAPAN.getName()).build(),
                                        PostbackAction.builder().data(CountryEnum.THAILAND.getName()).label(CountryEnum.THAILAND.getName()).displayText(CountryEnum.THAILAND.getName()).build(),
                                        PostbackAction.builder().data(CountryEnum.KOREAN.getName()).label(CountryEnum.KOREAN.getName()).displayText(CountryEnum.KOREAN.getName()).build()
                                ))
                        ));
                TemplateMessage templateMessage = new TemplateMessage("請使用手機觀看", carouselTemplate);
                return Collections.singletonList(templateMessage);
            case "問題回報":
                URI function1 = HandleFileUtil.createUri("/static/eachFunction/function1.jpg");
                URI function2 = HandleFileUtil.createUri("/static/eachFunction/function2.jpg");
                URI function3 = HandleFileUtil.createUri("/static/eachFunction/function3.jpg");
                URI function4 = HandleFileUtil.createUri("/static/eachFunction/function4.jpg");
                URI function5 = HandleFileUtil.createUri("/static/eachFunction/function5.jpg");
                CarouselTemplate questionTemplate = new CarouselTemplate(
                        Arrays.asList(
                                new CarouselColumn(function1, "查詢今日確診數失效", "", Arrays.asList(
                                        PostbackAction.builder().data("查詢今日確診數失效").label("是").displayText("是").build()
                                )),
                                new CarouselColumn(function2, "哪裡買口罩失效", "", Arrays.asList(
                                        PostbackAction.builder().data("哪裡買口罩失效").label("是").displayText("是").build()
                                )),
                                new CarouselColumn(function3, "掃描QRCode失效", "", Arrays.asList(
                                        PostbackAction.builder().data("掃描QRCode失效").label("是").displayText("是").build()
                                )),
                                new CarouselColumn(function4, "國外疫情失效", "", Arrays.asList(
                                        PostbackAction.builder().data("國外疫情失效").label("是").displayText("是").build()
                                )),
                                new CarouselColumn(function5, "疫苗統計圖失效", "", Arrays.asList(
                                        PostbackAction.builder().data("疫苗統計圖失效").label("是").displayText("是").build()
                                ))
                        ));
                TemplateMessage templateMessageForQuestion = new TemplateMessage("請使用手機觀看", questionTemplate);
                return Collections.singletonList(templateMessageForQuestion);
            case "關於作者":
                CarouselTemplate aboutMeCarouselTemplate = new CarouselTemplate(
                        Arrays.asList(
                                new CarouselColumn(aboutMeUri, "About Me", "fuga", Arrays.asList(
                                        new URIAction("Go to line.me",
                                                URI.create("https://github.com/chrisluo5311/"), null),
                                        new URIAction("Go to line.me",
                                                URI.create("https://www.linkedin.com/in/chris-luo-b4b350189/"), null)
                                ))
                        ));
            default:
                //失效問題send push message 給我自己
                final TextMessage textMessage = new TextMessage(data);
                final PushMessage pushMessage = new PushMessage(userId,textMessage);
                sendPushMessage(pushMessage);
                return null;
        }
    }

    @Override
    protected List<LocationMessage> handleLocationMessageReply(LocationMessageContent event,String userId) {
        //不使用
        return null;
    }






}
