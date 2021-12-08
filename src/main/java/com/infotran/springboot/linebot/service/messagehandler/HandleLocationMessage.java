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
 * 第二功能
 * 編號: 2 <br>
 * 1.處理使用者回傳位置訊息
 * 2.搜尋最近的藥局並回傳: 第一次回傳五間，按快捷鍵再回傳五個
 * @author chris
 *
 * */
@Slf4j
@Component
public class HandleLocationMessage extends BaseMessageHandler {

    private static final String LOG_PREFIX;
    private static final String REDIS_KEY_PREFIX;
    private static final Integer MEDICINE_STORE_AMOUNT;
    private static final Integer REPLY_STORE_LIMIT;

    static {
        LOG_PREFIX = "HandleLocationMessageReply";
        REDIS_KEY_PREFIX = "sortedLocationMessageList";
        MEDICINE_STORE_AMOUNT = 10;
        REPLY_STORE_LIMIT = 5;
    }

    @Resource
    private RedisLock redisLock;

    /**
     * Redis Timeout 1分鐘
     * */
    private static final Integer TIMEOUT = 1;
    
    @Resource
    RedisTemplate<Object, LocationMessage> locationMessageRedisTemplate;

    @Resource
    RedisTemplate<Object, MedicineStore> medicineStoreRedisTemplate;

    @Override
    public HandlerEnum getHandler() {
        return HandlerEnum.HANDLE_LOCATION_MESSAGE;
    }

    @Override
    protected  List<TextMessage> textMessageReply(TextMessageContent event,String replyToken,String userId) {
        //redis key 對應使用者
        StringBuilder keyString = new StringBuilder();
        keyString.append(REDIS_KEY_PREFIX).append(userId);
        String receivedMessage = event.getText();
        switch (receivedMessage){
            case "下五間":
                if(Boolean.TRUE.equals(locationMessageRedisTemplate.hasKey(keyString))){
                    List<LocationMessage> locationList = locationMessageRedisTemplate.opsForList().range(keyString,0,-1);
                    //確認是5家 line不傳超過5家會抱錯
                    if(locationList!=null){
                        List<Message> messageList = (REPLY_STORE_LIMIT ==locationList.size())
                                ? locationList.stream().map(Message.class::cast).collect(Collectors.toList())
                                : locationList.subList(0, 5).stream().map(Message.class::cast).collect(Collectors.toList());
                        reply(replyToken, messageList);
                        redisLock.unlock(keyString.toString());
                    }
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
            default:
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
        List<MedicineStore> medStoreList = medicineStoreRedisTemplate.opsForList().range("medicineStore",0,-1);
        if(Objects.isNull(medStoreList)){
            medStoreList = medicineStoreService.findAll();
        }
        Double lat1 = event.getLatitude();
        Double long1 = event.getLongitude();
        TreeMap<Double,MedicineStore> medicineStoreMap = new TreeMap<>();
        if(medStoreList!=null) {
            for(MedicineStore medicineStore : medStoreList){
                //两点公式算距离
                Double distance = getDistance(lat1, long1, medicineStore.getLatitude(), medicineStore.getLongitude());
                medicineStoreMap.put(distance, medicineStore);
            }
        } else {
            //TODO throw exception
            log.error("{} redis 或 資料庫無藥局資料 請手動確認",LOG_PREFIX);
        }

        LinkedList<LocationMessage> locationLinkedList = new LinkedList<>();
        medicineStoreMap.entrySet().stream().limit(MEDICINE_STORE_AMOUNT)
                .map(Map.Entry::getValue)
                .map(store -> LocationMessage.builder()
                        .title(store.getName())
                        .address(store.getAddress())
                        .latitude(store.getLatitude())
                        .longitude(store.getLongitude()).build())
                .forEach(locationLinkedList::add);

        //redis key + userid
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
    private double getDistance(Double lat1, Double lon1, Double lat2, Double lon2){
        if ((lat1.equals(lat2)) && (lon1.equals(lon2))){
            return 0;
        }else {
            Double theta = lon1 - lon2;
            Double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515 * 1.609344;
//			dist = dist * 1.609344;
            return (dist);
        }
    }


}
