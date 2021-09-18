package com.infotran.springboot.linebot.service.messagehandler;

import com.infotran.springboot.linebot.service.BaseMessageHandler;
import com.infotran.springboot.medicinestore.model.MedicineStore;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.quickreply.QuickReply;
import com.linecorp.bot.model.message.quickreply.QuickReplyItem;
import com.linecorp.bot.model.response.BotApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @author chris
 * 處理使用者回傳位置訊息<br>
 * 搜尋最近的藥局並回傳<br>
 * 一次回傳五個最多十個
 *
 * */
@Slf4j
@Service
public class HandleLocationMessageReply extends BaseMessageHandler {

    //前十近藥局
    public ArrayList<Double> topTen = new ArrayList<>();

    //藥局map
    public Map<Double,String> medicineStoreMap = new HashMap<>();

    @Override
    public void handleLocationMessageRely(MessageEvent<LocationMessageContent> event) {
        String replyToken = event.getReplyToken();
        Double  lat1 = event.getMessage().getLatitude();
        Double long1 = event.getMessage().getLongitude();
        Double lat2 ;
        Double long2 ;
        List<MedicineStore> medStoreList = mService.findAll();
        for (int i = 0 ; i < medStoreList.size(); i ++) {
            MedicineStore store = medStoreList.get(i);
            lat2 = store.getLatitude();
            long2 = store.getLongitude();
            Double dist = distance(lat1,long1,lat2,long2);
            medicineStoreMap.put(dist,store.getId());
            topTen.add(dist);
        }
        Collections.sort(topTen);
        List<Message> locationList = new ArrayList<>();

        final List<QuickReplyItem> items = Arrays.<QuickReplyItem>asList(
                QuickReplyItem.builder()
                        .action(PostbackAction.builder()
                                .label("下5間")
                                .data("下5間")
                                .displayText("下5間")
                                .build())
                        .build()
        );

        final QuickReply quickReply = QuickReply.items(items);

        for (int i = 0 ; i < 5 ; i++) {
            String id = medicineStoreMap.get(topTen.get(i));
            MedicineStore eachStore = mService.findById(id);
            String name = eachStore.getName();
            String address = eachStore.getAddress();
            Double latitude = eachStore.getLatitude();
            Double longitude = eachStore.getLongitude();
            LocationMessage locationMessage = LocationMessage.builder()
                                                             .title(name)
                                                             .address(address)
                                                             .latitude(latitude)
                                                             .longitude(longitude)
                                                             .quickReply(quickReply)
                                                             .build();
            locationList.add(locationMessage);
        }
        reply(replyToken,locationList);
    }

    /**
     * 處理QuickReply的"下五間"<br>
     * 回傳下五間最近的藥局資訊
     * @param event PostbackEvent
     * */
    @Override
    public void postBackReply(PostbackEvent event) throws IOException {
        String replyToken = event.getReplyToken();
        String data = event.getPostbackContent().getData();
        switch (data){
            case "下5間" :
                List<Message> locationList = new ArrayList<>();
                for (int i = 5 ; i < 10 ; i++){
                    String id = medicineStoreMap.get(topTen.get(i));
                    MedicineStore eachStore = mService.findById(id);
                    String name = eachStore.getName();
                    String address = eachStore.getAddress();
                    Double latitude = eachStore.getLatitude();
                    Double longitude = eachStore.getLongitude();
                    LocationMessage locationMessage = LocationMessage.builder()
                            .title(name)
                            .address(address)
                            .latitude(latitude)
                            .longitude(longitude)
                            .build();
                    locationList.add(locationMessage);
                }
                this.reply(replyToken,locationList);
        }
    }

    private double distance (double lat1,double lon1,double lat2,double lon2){
        if ((lat1 == lat2) && (lon1 == lon2)){
            return 0;
        }else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515 * 1.609344;
//			dist = dist * 1.609344;
            return (dist);
        }
    }

    @Override
    public BotApiResponse testTextMessageReply(MessageEvent<TextMessageContent> event) {
        //不使用
        return null;
    }

    @Override
    public void handleSticker(String replyToken, StickerMessageContent content) {
        //不使用
    }
}
