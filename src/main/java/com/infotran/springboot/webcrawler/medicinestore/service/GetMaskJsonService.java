package com.infotran.springboot.webcrawler.medicinestore.service;

import com.infotran.springboot.exception.LineBotException;
import com.infotran.springboot.exception.exceptionenum.LineBotExceptionEnums;
import com.infotran.springboot.util.ClientUtil;
import com.infotran.springboot.webcrawler.medicinestore.model.MedicineStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 取得店家口罩資訊
 * @author chris
 */
@Service
@Slf4j
public class GetMaskJsonService implements ClientUtil {

    /** 口罩即時url */
    @Value("${Mask.URL}")
    public String MASK_URL;

    private static final String LOG_PREFIX;
    public static final String REDIS_KEY;

    static {
        LOG_PREFIX = "[GetMaskJsonController]";
        REDIS_KEY = "medicineStore";
    }

    @Resource
    RedisTemplate<Object, MedicineStore> medicineStoreRedisTemplate;

    @Resource
    private MedicineStoreService medicineStoreService;

    /**
     * 解析口罩即時資訊的JSON
     * @param jsonBody jsonBody
     * @throws JSONException,LineBotException
     *
     * */
    public void parseMaskInfo (String jsonBody) throws LineBotException {
        List<MedicineStore> medList = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONObject(jsonBody).getJSONArray("features");
            for (int i = 0 ; i < jsonArray.length() ; i++){
                JSONObject feature = jsonArray.getJSONObject(i);
                JSONObject property = feature.getJSONObject("properties");
                JSONObject geometry = feature.getJSONObject("geometry");
                //藥局表id
                String id = (property.has("id"))? property.getString("id"):"";
                //藥局名稱
                String name = (property.has("name"))? property.getString("name"):"";
                //藥局電話
                String phone = (property.has("phone"))? property.getString("phone"):"";
                //藥局地址
                String address = (property.has("address"))? property.getString("address"):"";
                //藥局成人口罩剩餘
                Integer maskAdult = (property.has("mask_adult"))? Integer.parseInt(property.getString("mask_adult")):0;
                //藥局小孩口罩剩餘
                Integer maskChild = (property.has("mask_child"))? Integer.parseInt(property.getString("mask_child")):0;
                //更新時間
                StringBuilder updateTime = new StringBuilder();
                if (property.has("updated")) {
                    updateTime.append(property.getString("updated").substring(0,4));
                    updateTime.append(property.getString("updated").substring(5,8));
                    updateTime.append(property.getString("updated").substring(9));
                }
                //經緯度座標
                Double longitude = null;
                Double latitude = null;
                if (geometry.has("coordinates")) {
                    JSONArray coordinates= geometry.getJSONArray("coordinates");
                    longitude = (Double) coordinates.get(0);
                    latitude = (Double) coordinates.get(1);
                }
                MedicineStore medicineStore= MedicineStore.builder()
                                                          .id(id)
                                                          .name(name)
                                                          .phoneNumber(phone)
                                                          .address(address)
                                                          .maskAdult(maskAdult)
                                                          .maskChild(maskChild)
                                                          .latitude(latitude)
                                                          .longitude(longitude)
                                                          .updateTime(updateTime.toString())
                                                          .build();
                medList.add(medicineStore);
            }
            List<MedicineStore> response = medicineStoreService.saveAll(medList);
            if(Objects.isNull(response)){
                log.warn("{} 口罩即時資訊json解析成功 新增至db失敗",LOG_PREFIX);
                throw new LineBotException(LineBotExceptionEnums.DB_FAILED);
            }
        }catch (JSONException e ) {
            throw new LineBotException(LineBotExceptionEnums.FAIL_ON_GET_JSONOBJECT,e.getMessage());
        }
        medicineStoreRedisTemplate.delete(REDIS_KEY);
        medicineStoreRedisTemplate.opsForList().leftPushAll(REDIS_KEY,medList);
        medicineStoreRedisTemplate.expire(REDIS_KEY,60, java.util.concurrent.TimeUnit.MINUTES);
    }

}
