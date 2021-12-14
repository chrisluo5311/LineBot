package com.infotran.springboot.linebot.service.messagehandler;

import com.infotran.springboot.linebot.service.BaseMessageHandler;
import com.infotran.springboot.linebot.service.messagehandler.enums.HandlerEnum;
import com.infotran.springboot.util.HandleFileUtil;
import com.infotran.springboot.webcrawler.vaccinesvg.model.VaccineTypePeople;
import com.infotran.springboot.webcrawler.vaccinesvg.service.GetVaccinedInfoService;
import com.infotran.springboot.webcrawler.vaccinesvg.service.VaccinedPeopleService;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.quickreply.QuickReply;
import com.linecorp.bot.model.message.quickreply.QuickReplyItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 第五功能-处理疫苗统计图<br>
 * 編號: 5 <br>
 * @author chris
 *
 * */
@Slf4j
@Component
public class HandleVaccineIMGMessage extends BaseMessageHandler {

    private final String LOG_PREFIX = "[HandleVaccineIMGMessage]";

    @Resource
    VaccinedPeopleService vaccinedPeopleService;

    @Override
    public HandlerEnum getHandler() {
        //第五功能
        return HandlerEnum.HANDLE_STATISTIC_DIAGRAM;
    }

    @Override
    protected List<TextMessage> textMessageReply(PostbackEvent event) {
        return null;
    }

    @Override
    protected List<TextMessage> textMessageReply(TextMessageContent event, String replyToken, String userId) {
        String receivedMessage = event.getText();
        String filePrefix = "/static/";
        switch (receivedMessage){
            case "查看統計圖":
                //混合型回覆 自行生成QuickReply物件
                QuickReply quickReply = createQuickReplyItemList();
                //select出文字訊息pdf
                VaccineTypePeople vaccineTypePeople = vaccinedPeopleService.findOne();
                StringBuilder content = new StringBuilder();
                if(vaccineTypePeople!=null){
                    content.append("根據衛生福利部疾病管制署公佈: \n")
                            .append("COVID-19疫苗接種人次，")
                            .append(vaccineTypePeople.getBody())
                            .append("\n\n資料來源: ")
                            .append(vaccineTypePeople.getResourceUrl())
                            .append("。");
                }
                TextMessage pdfTextMessage = TextMessage.builder().text(content.toString()).quickReply(quickReply).build();
                //圖片訊息
                URI vaccineImgUri1 = HandleFileUtil.createUri(filePrefix.concat(GetVaccinedInfoService.cumuFileName));
                URI vaccineImgUri2 = HandleFileUtil.createUri(filePrefix.concat(GetVaccinedInfoService.eachAgeCoverFileName));
                ImageMessage cumulativeVaccinated = ImageMessage.builder().previewImageUrl(vaccineImgUri1).originalContentUrl(vaccineImgUri1).build();
                ImageMessage eachAgeCoverage = ImageMessage.builder().previewImageUrl(vaccineImgUri2).originalContentUrl(vaccineImgUri2).build();
                //組成List
                List<Message> replyList = new ArrayList<Message>();
                replyList.add(pdfTextMessage);
                replyList.add(cumulativeVaccinated);
                replyList.add(eachAgeCoverage);
                //回覆
                reply(replyToken,replyList);
                break;
            case "查看各縣市COVID-19公費疫苗涵蓋率圖":
                //select出文字訊息pdf
                VaccineTypePeople vaccineTypePeople2 = vaccinedPeopleService.findOne();
                StringBuilder content2 = new StringBuilder();
                if(vaccineTypePeople2!=null){
                    content2.append("資料來源: ")
                            .append(vaccineTypePeople2.getResourceUrl())
                            .append("。");
                }
                TextMessage eachCityTextMessage = TextMessage.builder().text(content2.toString()).build();
                //圖片訊息
                URI vaccineImgUri3 = HandleFileUtil.createUri(filePrefix.concat(GetVaccinedInfoService.eachCityCoverFileName));
                ImageMessage eachCityCoverage = ImageMessage.builder().previewImageUrl(vaccineImgUri3).originalContentUrl(vaccineImgUri3).build();
                //組成List
                List<Message> replyList2 = new ArrayList<Message>();
                replyList2.add(eachCityTextMessage);
                replyList2.add(eachCityCoverage);
                //回覆
                reply(replyToken,replyList2);
            default:
        }
        return null;
    }

    @Override
    protected <T extends MessageContent> List<LocationMessage> handleLocationMessageReply(LocationMessageContent event, String userId) {
        //不使用
        return null;
    }

    @Override
    protected List<Message> handleImagemapMessageReply(PostbackEvent event) {
        return null;
    }

    /**
     * 混合型回復自行生成QuickReply物件
     *
     * @return QuickReply
     * */
    private QuickReply createQuickReplyItemList(){
        List<QuickReplyItem> quickReplyItemList = new ArrayList<>();
        String label = "查看各縣市COVID-19公費疫苗涵蓋率圖";
        String text = "查看各縣市COVID-19公費疫苗涵蓋率圖";
        QuickReplyItem item = QuickReplyItem.builder()
                .action(new MessageAction(label,text))
                .build();
        quickReplyItemList.add(item);
        return QuickReply.builder().items(quickReplyItemList).build();
    }

}
