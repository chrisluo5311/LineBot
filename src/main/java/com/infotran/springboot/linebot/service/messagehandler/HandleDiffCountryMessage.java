package com.infotran.springboot.linebot.service.messagehandler;

import com.infotran.springboot.annotation.MultiQuickReply;
import com.infotran.springboot.annotation.QuickReplyMode;
import com.infotran.springboot.annotation.quickreplyenum.ActionMode;
import com.infotran.springboot.linebot.service.BaseMessageHandler;
import com.infotran.springboot.linebot.service.messagehandler.enums.HandlerEnum;
import com.infotran.springboot.util.TimeUtil;
import com.infotran.springboot.webcrawler.multicountry.countryenum.CountryEnum;
import com.infotran.springboot.webcrawler.multicountry.model.DiffCountry;
import com.infotran.springboot.webcrawler.multicountry.service.Impl.DiffCountryServiceImpl;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 第四功能 <br>
 * 國內外疫情 <br>
 * 編號: 4 <br>
 * 1.處理國外疫情状况
 *
 * @author chris
 */
public class HandleDiffCountryMessage extends BaseMessageHandler {

    private static final String LOG_PREFIX = "HandleDiffCountryMessage";

    @Value("${CDC.WORLD.COUNTRYCODE_URL}")
    String worldCovidUrl;

    @Resource
    DiffCountryServiceImpl diffCountryService;

    @Override
    public HandlerEnum getHandler() {
        return HandlerEnum.HANDLE_FOREIGN_COVID;
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
        return null;
    }

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
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "俄羅斯",data = "俄羅斯",displayText = "俄羅斯"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "法國",data = "法國",displayText = "法國"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "德國",data = "德國",displayText = "德國"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "泰國",data = "泰國",displayText = "泰國"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "以色列",data = "以色列",displayText = "以色列"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "韓國",data = "韓國",displayText = "韓國"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "香港",data = "香港",displayText = "香港"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "中國大陸",data = "中國大陸",displayText = "中國大陸"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "新加坡",data = "新加坡",displayText = "新加坡"),
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "日本",data = "日本",displayText = "日本"),
    })
    protected List<Message> handleImagemapMessageReply(PostbackEvent event) {
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
                Map<CountryEnum, String> replyTestMap = this.getReplyText(CountryEnum.getPriorityCountryEnum());
                //建構CarouselTemplate
                CarouselTemplate carouselTemplate = new CarouselTemplate(
                        Arrays.asList(
                                new CarouselColumn(globalUri, "全球疫情統計", replyTestMap.get(CountryEnum.GLOBAL), Arrays.asList(
                                        new URIAction("查看全球疫情數據",
                                                URI.create(CountryEnum.GLOBAL.getActionUri(worldCovidUrl)), null)
                                )),
                                new CarouselColumn(usUri, "美國疫情統計", replyTestMap.get(CountryEnum.US), Arrays.asList(
                                        new URIAction("查看美國疫情數據",
                                                URI.create(CountryEnum.US.getActionUri(worldCovidUrl)), null)
                                )),
                                new CarouselColumn(chinaUri, "中國疫情統計", replyTestMap.get(CountryEnum.CHINA), Arrays.asList(
                                        new URIAction("查看中國疫情數據",
                                                URI.create(CountryEnum.CHINA.getActionUri(worldCovidUrl)), null)
                                )),
                                new CarouselColumn(japanUri, "日本疫情統計", replyTestMap.get(CountryEnum.JAPAN), Arrays.asList(
                                        new URIAction("查看日本疫情數據",
                                                URI.create(CountryEnum.JAPAN.getActionUri(worldCovidUrl)), null)
                                ))
                        ));
                TemplateMessage templateMessage = new TemplateMessage("請使用手機觀看", carouselTemplate);
                return Collections.singletonList(templateMessage);
            default:
                CountryEnum eachCountry = CountryEnum.getCountryEnumByName(data);
                String countryName = eachCountry.getName();
                URI singleCountryUri = eachCountry.getUri();
                Map<CountryEnum, String> singleReplyText = this.getReplyText(Collections.singletonList(eachCountry));
                CarouselTemplate singleCarouselTemplate = new CarouselTemplate(
                        Arrays.asList(
                                new CarouselColumn(singleCountryUri, countryName+"疫情統計", singleReplyText.get(eachCountry), Arrays.asList(
                                        new URIAction("查看"+countryName+"疫情數據",
                                                URI.create(eachCountry.getActionUri(worldCovidUrl)), null))
                        ))
                );
                TemplateMessage eachTemplateMessage = new TemplateMessage("請使用手機觀看", singleCarouselTemplate);
                return Collections.singletonList(eachTemplateMessage);
        }
    }



    /**
     * 取得每個國家相對應的回覆內容
     * @return Map
     * */
    private Map<CountryEnum,String> getReplyText(List<CountryEnum> countryEnumList){
        Map<CountryEnum,String> replyMap = new HashMap<>();
        AtomicReference<DiffCountry> diffCountry = null;
        //找當天資料
        countryEnumList.stream().forEach(x->{
            diffCountry.set(diffCountryService.findByIsoCodeAndLastUpdate(x.getCountryCode(), TimeUtil.formCustomDate("YYYY-MM-dd", null)));
            if(Objects.isNull(diffCountry.get())){
                //前天資料
                diffCountry.set(diffCountryService.findByIsoCodeAndLastUpdate(x.getCountryCode(), TimeUtil.formCustomDate("YYYY-MM-dd", 1l)));
            }
            Assert.notNull(diffCountry.get(),"國家不可為null，請手動確認國家資料狀態!!!");
            replyMap.put(x,CountryEnum.createReplyTemplate(diffCountry.get()));
        });
        return replyMap;
    }

}
