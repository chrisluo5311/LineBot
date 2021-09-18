package com.infotran.springboot.medicinestore.controller;

import com.infotran.springboot.Exception.Enum.LineBotExceptionCode;
import com.infotran.springboot.Exception.LineBotException;
import com.infotran.springboot.Util.ClientUtil;
import com.infotran.springboot.medicinestore.model.MedicineStore;
import com.infotran.springboot.medicinestore.service.MedicineStoreService;
import com.infotran.springboot.schedular.TimeUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Controller
@Slf4j
public class GetMaskJsonController implements ClientUtil {

    //口罩即時url
    private String Mask_URL = "https://raw.githubusercontent.com/kiang/pharmacies/master/json/points.json";

    private static final String LOG_PREFIX = "GetMaskJsonController";

    //redis key值
    private static final String REDIS_KEY = "medicineStore";

    @Resource
    private MedicineStoreService medicinetoreService;

    @Resource
    RedisTemplate<Object, MedicineStore> medicineStoreRedisTemplate;

    /**
     * 執行異步請求
     * */
    @Scheduled(cron = "0 0 0/1 * * ? *")
    public void run() throws IOException {
        Request request = new Request.Builder().url(Mask_URL).get().build(); // get
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @SneakyThrows
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                log.info("@@@@@@ 爬蟲成功 @@@@@@");
                String jsonBody = response.body().string();
                log.info("{} 即時口罩JSON: {}",LOG_PREFIX,jsonBody);
                parseMaskInfo(jsonBody);
            }
        });
    }

    /**
     * 解析口罩即時資訊的JSON
     * */
    private void parseMaskInfo (String jsonBody) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonBody);
        JSONArray jsonArray = jsonObject.getJSONArray("features");
        List<MedicineStore> medList = new ArrayList<>();
        for (int i = 0 ; i < jsonArray.length() ; i++){
            JSONObject Feature = jsonArray.getJSONObject(i);
            JSONObject property = Feature.getJSONObject("properties");
            JSONObject geometry = Feature.getJSONObject("geometry");
            //藥局表id
            String id = (property.has("id"))? property.getString("id"):"";
            //藥局名稱
            String name = (property.has("name"))? property.getString("name"):"";
            //藥局電話
            String phone = (property.has("phone"))? property.getString("phone"):"";
            //藥局地址
            String address = (property.has("address"))? property.getString("address"):"";
            //藥局成人口罩剩餘
            Integer maskAdult = (property.has("mask_adult"))? Integer.valueOf(property.getString("mask_adult")):0;
            //藥局小孩口罩剩餘
            Integer maskChild = (property.has("mask_child"))? Integer.valueOf(property.getString("mask_child")):0;
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
            log.info("{} 藥局物件 {}",LOG_PREFIX,medicineStore);
            medList.add(medicineStore);
        }
        medicineStoreRedisTemplate.opsForList().leftPushAll(REDIS_KEY,medList);
    }

    /**
     * 定時新增至資料庫(每個小時)<p>
     * 使用自定義hibernate.jdbc.batch_size=1000
     * Batch Size是設定對資料庫進行批量刪除，批量更新和批量插入的時候的批次大小
     * */
    @Scheduled(fixedRate = 1*TimeUnit.HOUR)
    public void scheduledSaving () throws Exception {
        List<MedicineStore> medList = medicineStoreRedisTemplate.opsForList().range(REDIS_KEY, 0, -1);
        List<MedicineStore> response = medicinetoreService.saveAll(medList);
        if (response==null) {
            throw new LineBotException(LineBotExceptionCode.FAIL_ON_SAVING_RESPONSE);
        }
    }


}
