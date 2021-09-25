package com.infotran.springboot.linebot.service.messagehandler;

import com.infotran.springboot.annotation.MultiQuickReply;
import com.infotran.springboot.annotation.QuickReplyMode;
import com.infotran.springboot.annotation.quickreplyenum.ActionMode;
import com.infotran.springboot.linebot.service.BaseMessageHandler;
import com.infotran.springboot.linebot.service.messagehandler.enums.HandlerEnum;
import com.infotran.springboot.medicinestore.model.MedicineStore;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chris
 * 第二功能
 * 編號: 2 <br>
 * 1.打開地圖
 * 2.處理使用者回傳位置訊息
 * 3.搜尋最近的藥局並回傳: 第一次回傳五間，按快捷鍵再回傳五個
 *
 * */
@Slf4j
@Component(value = "HandleLocationMessageReply")
public class HandleLocationMessage extends BaseMessageHandler {

    private static final String LOG_PREFIX = "HandleLocationMessageReply";

    private static final String REDIS_KEY = "sortedLocationMessageList";

    @Resource
    RedisTemplate<Object, LocationMessage> locationMessageRedisTemplate;

    @Override
    public String getClassName() {
        return HandlerEnum.getHandlerName(2);
    }

    @Override
    protected  List<TextMessage> textMessageReply(TextMessageContent event,String replyToken) {
        String receivedMessage = event.getText();
        switch (receivedMessage){
            case "下五間":
                List<LocationMessage> locationList = locationMessageRedisTemplate.opsForList().range(REDIS_KEY,0,-1);
                List<Message> messageList = locationList.stream().map(Message.class::cast).collect(Collectors.toList());
                reply(replyToken,messageList);
                break;
        }
        return null;
    }

    @Override
    protected List<TextMessage> textMessageReply(PostbackEvent event) {
        //不使用
        return null;
    }

    /**
     * 處理使用者回傳的現在位置
     *
     * @param event  MessageEvent
     */
    @MultiQuickReply(value = {
            @QuickReplyMode(mode=ActionMode.MESSAGE,label = "下五間",text = "下五間"),
            @QuickReplyMode(mode=ActionMode.POSTBACK,label="重新整理",data="refreshfuntion2",displayText="重新整理")
    })
    @Override
    public List<LocationMessage> handleLocationMessageReply(LocationMessageContent event) {
        Double lat1,long1,lat2,long2;
        lat1 = event.getLatitude();
        long1 = event.getLongitude();
        //藥局map
        Map<Double,MedicineStore> medicineStoreMap = new HashMap<>();
        List<MedicineStore> medStoreList = mService.findAll();
        for (int i = 0 ; i < medStoreList.size(); i ++) {
            MedicineStore store = medStoreList.get(i);
            lat2 = store.getLatitude();
            long2 = store.getLongitude();
            Double dist = distance(lat1,long1,lat2,long2);
            medicineStoreMap.put(dist,store);
        }

        log.info("{} medStoreList 排序前 : {}",LOG_PREFIX,medStoreList);
        TreeMap<Double,MedicineStore> medStoreTreeMap = new TreeMap<>(medicineStoreMap);
        log.info("{} TreeMap medStoreTreeMap 排序後 : {}",LOG_PREFIX,medStoreTreeMap);

        LinkedList<LocationMessage> locationlinkedList = new LinkedList<>();
        LocationMessage locationMessage = null;
        for (Map.Entry entry : medStoreTreeMap.entrySet()){
            MedicineStore medicineStore = (MedicineStore) entry.getValue();
            String name = medicineStore.getName();
            String address = medicineStore.getAddress();
            Double latitude = medicineStore.getLatitude();
            Double longitude = medicineStore.getLongitude();
            locationMessage = LocationMessage.builder()
                                             .title(name)
                                             .address(address)
                                             .latitude(latitude)
                                             .longitude(longitude)
                                             .build();
            locationlinkedList.add(locationMessage);
        }
        //save to redis
        List<LocationMessage> listToRedis = locationlinkedList.stream().skip(5).limit(5).collect(Collectors.toList());
        locationMessageRedisTemplate.opsForList().leftPushAll(REDIS_KEY,listToRedis);
        List<LocationMessage> locationList = locationlinkedList.stream().limit(5).collect(Collectors.toList());
        return locationList;
    }


    /**
     * 計算兩點距離
     * @param lat1 緯度1
     * @param lon1 經度1
     * @param lat2 緯度2
     * @param lon2 經度2
     * @return double 距離
     * */
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


}