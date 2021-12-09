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
            @QuickReplyMode(mode= ActionMode.POSTBACK,label = "",data = "",displayText = "")
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
                Map<CountryEnum, String> replyTestMap = this.getReplyText();
                //建構CarouselTemplate
                CarouselTemplate carouselTemplate = new CarouselTemplate(
                        Arrays.asList(
                                new CarouselColumn(globalUri, "全球疫情統計", replyTestMap.get(CountryEnum.GLOBAL), Arrays.asList(
                                        new URIAction("查看全球疫情數據",
                                                URI.create(CountryEnum.GLOBAL.getActionUri()), null)
                                )),
                                new CarouselColumn(usUri, "美國疫情統計", replyTestMap.get(CountryEnum.US), Arrays.asList(
                                        new URIAction("查看美國疫情數據",
                                                URI.create(CountryEnum.US.getActionUri()), null)
                                )),
                                new CarouselColumn(chinaUri, "中國疫情統計", replyTestMap.get(CountryEnum.CHINA), Arrays.asList(
                                        new URIAction("查看中國疫情數據",
                                                URI.create(CountryEnum.CHINA.getActionUri()), null)
                                )),
                                new CarouselColumn(japanUri, "日本疫情統計", replyTestMap.get(CountryEnum.JAPAN), Arrays.asList(
                                        new URIAction("查看日本疫情數據",
                                                URI.create(CountryEnum.JAPAN.getActionUri()), null)
                                ))
                        ));
                TemplateMessage templateMessage = new TemplateMessage("請使用手機觀看", carouselTemplate);
                reply(replyToken,templateMessage);
                break;
            default:
        }
        return null;
    }

    /**
     * 取得每個國家相對應的回覆內容
     * @return Map
     * */
    private Map<CountryEnum,String> getReplyText(){
        Map<CountryEnum,String> replyMap = new HashMap<>();
        AtomicReference<DiffCountry> diffCountry = null;
        //找當天資料
        CountryEnum.getPriorityCountryEnum().stream().forEach(x->{
            diffCountry.set(diffCountryService.findByIsoCodeAndLastUpdate(x.getCountryCode(), TimeUtil.formCustomDate("YYYY-MM-dd", null)));
            if(Objects.isNull(diffCountry.get())){
                //前天資料
                diffCountry.set(diffCountryService.findByIsoCodeAndLastUpdate(x.getCountryCode(), TimeUtil.formCustomDate("YYYY-MM-dd", 1l)));
            }
            replyMap.put(x,CountryEnum.createReplyTemplate(diffCountry.get()));
        });
        return replyMap;
    }

}
