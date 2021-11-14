package com.infotran.springboot.linebot.service.messagehandler;

import com.infotran.springboot.linebot.service.BaseMessageHandler;
import com.infotran.springboot.linebot.service.messagehandler.enums.HandlerEnum;
import com.infotran.springboot.util.DownloadFileUtil;
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
 * @author chris
 * 第五功能-处理疫苗统计图
 * 編號: 5 <br>
 *
 *
 * */
@Slf4j
@Component
public class HandleVaccineIMGMessage extends BaseMessageHandler {

    private final String LOG_PREFIX = "[HandleVaccineIMGMessage]";

    @Resource
    VaccinedPeopleService vaccinedPeopleService;

    @Override
    public String getClassName() {
        //第五功能
        return HandlerEnum.getHandlerName(5);
    }

    @Override
    protected List<TextMessage> textMessageReply(PostbackEvent event) {
        return null;
    }

    @Override
    protected List<TextMessage> textMessageReply(TextMessageContent event, String replyToken, String userId) {
        String receivedMessage = event.getText();
        switch (receivedMessage){
            case "查看疫苗施打人數累計統計圖":
                QuickReply quickReply = createQuickReplyItemList();
                //文字訊息
                VaccineTypePeople vaccineTypePeople = vaccinedPeopleService.findOne();
                StringBuilder content = new StringBuilder();
                if(vaccineTypePeople!=null){
                    content.append("根據衛生福利部疾病管制署公佈: \n")
                            .append("COVID-19疫苗接種人次，")
                            .append(vaccineTypePeople.getBody()).append("\n\n資料來源: ")
                            .append(vaccineTypePeople.getResourceUrl()).append("。");
                }
                TextMessage textMessage = TextMessage.builder().text(content.toString()).quickReply(quickReply).build();
                //圖片訊息
                URI vaccineImgUri1 = DownloadFileUtil.createUri("/static/cumulativeVaccined.jpg");
                URI vaccineImgUri2 = DownloadFileUtil.createUri("/static/eachBatchCoverage.jpg");
                ImageMessage imgMessage1 = ImageMessage.builder().previewImageUrl(vaccineImgUri1).originalContentUrl(vaccineImgUri1).build();
                ImageMessage imgMessage2 = ImageMessage.builder().previewImageUrl(vaccineImgUri2).originalContentUrl(vaccineImgUri2).build();
                List<Message> replyList = new ArrayList<Message>();
                replyList.add(textMessage);
                replyList.add(imgMessage1);
                replyList.add(imgMessage2);
                reply(replyToken,replyList);
                break;
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
     * 混和行回復需自行生成QuickReply物件
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
