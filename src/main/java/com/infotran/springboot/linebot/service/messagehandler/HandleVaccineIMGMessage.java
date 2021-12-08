package com.infotran.springboot.linebot.service.messagehandler;

import com.infotran.springboot.linebot.service.BaseMessageHandler;
import com.infotran.springboot.linebot.service.messagehandler.enums.HandlerEnum;
import com.infotran.springboot.util.HandleFileUtil;
import com.infotran.springboot.webcrawler.vaccinesvg.model.VaccineTypePeople;
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
                            .append(vaccineTypePeople.getBody()).append("\n\n資料來源: ")
                            .append(vaccineTypePeople.getResourceUrl()).append("。");
                }
                TextMessage pdfTextMessage = TextMessage.builder().text(content.toString()).quickReply(quickReply).build();
                //圖片訊息
                URI vaccineImgUri1 = HandleFileUtil.createUri("/static/cumulativeVaccined.jpg");
                URI vaccineImgUri2 = HandleFileUtil.createUri("/static/eachBatchCoverage.jpg");
                ImageMessage cumulativeVaccined = ImageMessage.builder().previewImageUrl(vaccineImgUri1).originalContentUrl(vaccineImgUri1).build();
                ImageMessage eachBatchCoverage = ImageMessage.builder().previewImageUrl(vaccineImgUri2).originalContentUrl(vaccineImgUri2).build();
                //組成List
                List<Message> replyList = new ArrayList<Message>();
                replyList.add(pdfTextMessage);
                replyList.add(cumulativeVaccined);
                replyList.add(eachBatchCoverage);
                //回覆
                reply(replyToken,replyList);
                break;
            case "查看各縣市COVID-19疫苗涵蓋率":
                //todo 查看各縣市COVID-19疫苗涵蓋率
                log.warn("尚未製作 查看各縣市COVID-19疫苗涵蓋率");
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
        String label = "查看各縣市COVID-19疫苗涵蓋率";
        String text = "查看各縣市COVID-19疫苗涵蓋率";
        QuickReplyItem item = QuickReplyItem.builder()
                .action(new MessageAction(label,text))
                .build();
        quickReplyItemList.add(item);
        return QuickReply.builder().items(quickReplyItemList).build();
    }

}
