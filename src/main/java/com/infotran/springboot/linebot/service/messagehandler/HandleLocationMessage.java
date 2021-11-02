package com.infotran.springboot.linebot.service.messagehandler;

import com.infotran.springboot.annotation.LogExecutionTime;
import com.infotran.springboot.annotation.MultiQuickReply;
import com.infotran.springboot.annotation.QuickReplyMode;
import com.infotran.springboot.annotation.quickreplyenum.ActionMode;
import com.infotran.springboot.config.RedisLock;
import com.infotran.springboot.linebot.service.BaseMessageHandler;
import com.infotran.springboot.linebot.service.messagehandler.enums.HandlerEnum;
import com.infotran.springboot.webcrawler.medicinestore.model.MedicineStore;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author chris
 * 第二功能
 * 編號: 2 <br>
 * 1.處理使用者回傳位置訊息
 * 2.搜尋最近的藥局並回傳: 第一次回傳五間，按快捷鍵再回傳五個
 *
 * */
@Slf4j
@Component
public class HandleLocationMessage extends BaseMessageHandler {

    private static final String LOG_PREFIX;
    private static String REDIS_KEY_PREFIX;
    private static Integer medicineStoreAmount;

    static {
        LOG_PREFIX = "HandleLocationMessageReply";
        REDIS_KEY_PREFIX = "sortedLocationMessageList";//須加上使用者id
        medicineStoreAmount = 10;
    }

    @Resource
    private RedisLock redisLock;

    //Reids Timeout 1分鐘
    private static Integer TIMEOUT = 1;
    
    @Resource
    RedisTemplate<Object, LocationMessage> locationMessageRedisTemplate;

    @Resource
    RedisTemplate<Object, MedicineStore> medicineStoreRedisTemplate;

    @Override
    public String getClassName() {
        return HandlerEnum.getHandlerName(2);
    }

    @Override
    protected  List<TextMessage> textMessageReply(TextMessageContent event,String replyToken,String userId) {
        //redis key 對應使用者
        StringBuilder keySB = new StringBuilder();
        keySB.append(REDIS_KEY_PREFIX).append(userId);
        String receivedMessage = event.getText();
        switch (receivedMessage){
            case "下五間":
                if(locationMessageRedisTemplate.hasKey(keySB)){
                    List<LocationMessage> locationList = locationMessageRedisTemplate.opsForList().range(keySB,0,-1);
                    //確認是5家不然會抱錯 line不傳超過5家
                    List<Message> messageList = (locationList.size()==5) ?
                            locationList.stream().map(Message.class::cast).collect(Collectors.toList()) :
                            locationList.subList(0, 5).stream().map(Message.class::cast).collect(Collectors.toList());
                    reply(replyToken, messageList);
                    //刪除redis key
                    redisLock.unlock(keySB.toString());
                }else {
                    //redis key 1分鐘Timeout才點會启动重新定位的方法
                    TextMessage textMessage = openMap();
                    reply(replyToken,textMessage);
                }
                break;
            case "查看所在位置口罩剩餘狀態":
            case "重新定位":
                TextMessage textMessage = openMap();
                reply(replyToken,textMessage);
                break;
        }
        return null;
    }

    @Override
    protected List<TextMessage> textMessageReply(PostbackEvent event) {
        //不使用
        return null;
    }

    @Override
    protected List<Message> handleImagemapMessageReply(PostbackEvent event) {
        //不使用
        return null;
    }

    /**
     * 處理使用者回傳的現在位置
     *
     * @param event  MessageEvent
     */
    @LogExecutionTime
    @MultiQuickReply(value = {
            @QuickReplyMode(mode=ActionMode.MESSAGE,label = "下五間",text = "下五間"),
            @QuickReplyMode(mode=ActionMode.MESSAGE,label="重新定位",text = "重新定位")
    })
    @Override
    public List<LocationMessage> handleLocationMessageReply(LocationMessageContent event,String userId) {
        //從redis取出所有藥局
        List<MedicineStore> medStoreList = medicineStoreRedisTemplate.opsForList().range("medicineStore",0,-1);
        if(Objects.isNull(medStoreList)){
            medStoreList = mService.findAll();
        }
        Double lat1 = event.getLatitude();
        Double long1 = event.getLongitude();
        //藥局map 儲存距離與店家
        TreeMap<Double,MedicineStore> medicineStoreMap = new TreeMap<>();
        if(medStoreList!=null) {
            for(MedicineStore medicineStore : medStoreList){
                //两点公式算距离
                Double distance = getDistance(lat1, long1, medicineStore.getLatitude(), medicineStore.getLongitude());
                medicineStoreMap.put(distance, medicineStore);
            }
        }

        //取出店家存進LocationMessage的LinkedList
        LinkedList<LocationMessage> locationLinkedList = new LinkedList<>();
        LocationMessage locationMessage = null;
        Integer i = 0;
        for (Map.Entry entry : medicineStoreMap.entrySet()){
            while (i < medicineStoreAmount){
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
                locationLinkedList.add(locationMessage);
                i++;
            }
            break;
        }
        //redis key
        String key = REDIS_KEY_PREFIX.concat(userId);
        //存後5家進redis
        List<LocationMessage> listToRedis = locationLinkedList.stream().skip(5).limit(5).collect(Collectors.toList());
        locationMessageRedisTemplate.opsForList().leftPushAll(key,listToRedis);
        redisLock.lock(TIMEOUT,key);
        //設定Timeout:1分鐘
        locationMessageRedisTemplate.expire(key,TIMEOUT, TimeUnit.MINUTES);
        //回覆前5家
        return locationLinkedList.stream().limit(5).collect(Collectors.toList());
    }


    /**
     * 計算兩點距離
     * @param lat1 緯度1
     * @param lon1 經度1
     * @param lat2 緯度2
     * @param lon2 經度2
     * @return double 距離
     * */
    private double getDistance(double lat1, double lon1, double lat2, double lon2){
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
