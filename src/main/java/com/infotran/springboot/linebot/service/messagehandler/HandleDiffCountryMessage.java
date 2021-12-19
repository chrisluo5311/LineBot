package com.infotran.springboot.linebot.service.messagehandler;

import com.infotran.springboot.annotation.MultiQuickReply;
import com.infotran.springboot.annotation.QuickReplyMode;
import com.infotran.springboot.annotation.quickreplyenum.ActionMode;
import com.infotran.springboot.exception.exceptionenum.LineBotExceptionEnums;
import com.infotran.springboot.linebot.service.BaseMessageHandler;
import com.infotran.springboot.linebot.service.messagehandler.enums.HandlerEnum;
import com.infotran.springboot.util.TimeUtil;
import com.infotran.springboot.webcrawler.multicountry.countryenum.CountryEnum;
import com.infotran.springboot.webcrawler.multicountry.model.DiffCountry;
import com.infotran.springboot.webcrawler.multicountry.service.Impl.DiffCountryServiceImpl;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URI;
import java.util.*;

/**
 * 第四功能 <br>
 * 國內外疫情 <br>
 * 編號: 4 <br>
 * 1.處理國外疫情状况
 *
 * @author chris
 */
@Slf4j
@Component
public class HandleDiffCountryMessage extends BaseMessageHandler {

    private static final String LOG_PREFIX = "HandleDiffCountryMessage";

    /**
     * 是否為Carousel
     * Carousel限制字數60字
     * */
    Boolean isCarousel = true;

    @Value("${CDC.WORLD.COUNTRYCODE_URL}")
    String worldCovidUrl;

    @Resource
    DiffCountryServiceImpl diffCountryService;

    @Override
    public HandlerEnum getHandler() {
        return HandlerEnum.HANDLE_FOREIGN_COVID;
    }

    /**
     * 有驗證isValidCountryName才會到這裡
     * */
    @Override
    protected List<TextMessage> textMessageReply(TextMessageContent event, String replyToken, String userId) {
        //ex. 看更多日本資訊
        String text = event.getText();
        log.info("{} 使用者:{} 觸發:{}",LOG_PREFIX,userId,text);
        String countryName = CountryEnum.getCountryNameByTextMessageContent(text);
        CountryEnum eachCountry = CountryEnum.getCountryEnumByName(countryName);
        Map<CountryEnum, String> singleReplyText = this.getReplyText(Collections.singletonList(eachCountry),false);
        return Collections.singletonList(new TextMessage(singleReplyText.get(eachCountry)));
    }

    @Override
    protected List<TextMessage> textMessageReply(PostbackEvent event) {
        return null;
    }

    @Override
    protected <T extends MessageContent> List<LocationMessage> handleLocationMessageReply(LocationMessageContent event, String userId) {
        return null;
    }

    /**
     *  QuickReply限制最多13個
     * */
    @Override
    @MultiQuickReply(value = {
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "全球",data = "全球",displayText = "全球"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "美國",data = "美國",displayText = "美國"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "北美洲",data = "北美洲",displayText = "北美洲"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "歐盟",data = "歐洲聯盟",displayText = "歐盟"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "南美洲",data = "南美洲",displayText = "南美洲"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "印度",data = "印度",displayText = "印度"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "巴西",data = "巴西",displayText = "巴西"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "英國",data = "英國",displayText = "英國"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "法國",data = "法國",displayText = "法國"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "德國",data = "德國",displayText = "德國"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "以色列",data = "以色列",displayText = "以色列"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "俄羅斯",data = "俄羅斯",displayText = "俄羅斯"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "中國大陸",data = "中國大陸",displayText = "中國大陸")
    })
    protected List<TemplateMessage> handleImagemapMessageReply(PostbackEvent event) {
        String replyToken = event.getReplyToken();
        String data = event.getPostbackContent().getData();
        switch (data){
            case "國外疫情":
                //先4個 : 全球、美國、中國、日本
                URI globalUri = CountryEnum.GLOBAL.getUri();
                URI usUri     = CountryEnum.US.getUri();
                URI chinaUri  = CountryEnum.CHINA.getUri();
                URI japanUri  = CountryEnum.JAPAN.getUri();
                //建構內容text的Map
                List<CountryEnum> countryEnumList = CountryEnum.getPriorityCountryEnum();
                Map<CountryEnum, String> replyTestMap = getReplyText(countryEnumList,isCarousel);
                //建構CarouselTemplate
                CarouselTemplate carouselTemplate = new CarouselTemplate(
                        Arrays.asList(
                                new CarouselColumn(globalUri, "全球疫情統計", replyTestMap.get(CountryEnum.GLOBAL), Arrays.asList(
                                        new URIAction("線上查看全球疫情數據",
                                                URI.create(CountryEnum.GLOBAL.getActionUri(worldCovidUrl)), null),
                                        new MessageAction("全球詳細資訊","看更多全球資訊")
                                )),
                                new CarouselColumn(usUri, "美國疫情統計", replyTestMap.get(CountryEnum.US), Arrays.asList(
                                        new URIAction("線上查看美國疫情數據",
                                                URI.create(CountryEnum.US.getActionUri(worldCovidUrl)), null),
                                        new MessageAction("美國詳細資訊","看更多美國資訊")
                                )),
                                new CarouselColumn(chinaUri, "中國疫情統計", replyTestMap.get(CountryEnum.CHINA), Arrays.asList(
                                        new URIAction("線上查看中國疫情數據",
                                                URI.create(CountryEnum.CHINA.getActionUri(worldCovidUrl)), null),
                                        new MessageAction("中國詳細資訊","看更多中國資訊")
                                )),
                                new CarouselColumn(japanUri, "日本疫情統計", replyTestMap.get(CountryEnum.JAPAN), Arrays.asList(
                                        new URIAction("線上查看日本疫情數據",
                                                URI.create(CountryEnum.JAPAN.getActionUri(worldCovidUrl)), null),
                                        new MessageAction("日本詳細資訊","看更多日本資訊")
                                ))
                        ));
                TemplateMessage templateMessage = new TemplateMessage("請使用手機觀看", carouselTemplate);
                return Collections.singletonList(templateMessage);
            default:
                //處理各個單一國家
                CountryEnum eachCountry = CountryEnum.getCountryEnumByName(data);
                String countryName = eachCountry.getName();
                URI singleCountryUri = eachCountry.getUri();
                Map<CountryEnum, String> singleReplyText = this.getReplyText(Collections.singletonList(eachCountry),isCarousel);
                CarouselTemplate singleCarouselTemplate = new CarouselTemplate(
                        Arrays.asList(
                                new CarouselColumn(singleCountryUri, countryName+"疫情統計", singleReplyText.get(eachCountry), Arrays.asList(
                                        new URIAction("線上查看"+countryName+"疫情數據",
                                                URI.create(eachCountry.getActionUri(worldCovidUrl)), null),
                                        new MessageAction(countryName+"詳細資訊","看更多"+countryName+"資訊")
                                ))
                        )
                );
                TemplateMessage eachTemplateMessage = new TemplateMessage("請使用手機觀看", singleCarouselTemplate);
                return Collections.singletonList(eachTemplateMessage);
        }
    }



    /**
     * 取得每個國家相對應的回覆內容
     * @param countryEnumList
     * @param isCarousel
     * @return Map
     * */
    public Map<CountryEnum,String> getReplyText(List<CountryEnum> countryEnumList,Boolean isCarousel){
        Map<CountryEnum,String> replyMap = new HashMap<>();
        //找當天資料
        countryEnumList.stream().forEach(x->{
            DiffCountry diffCountry = diffCountryService.findByIsoCodeAndLastUpdate(x.getCountryCode(), TimeUtil.TODAY_DATE);
            if(Objects.isNull(diffCountry)){
                //前天資料
                diffCountry = diffCountryService.findByIsoCodeAndLastUpdate(x.getCountryCode(), TimeUtil.YESTERDAY_DATE);
            }
            if(diffCountry==null){
                log.error("{}", LineBotExceptionEnums.MISSING_COUNTRY_DBINFO);
            }
            replyMap.put(x,CountryEnum.createReplyTemplate(diffCountry,isCarousel));
        });
        return replyMap;
    }



}
