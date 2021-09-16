package com.infotran.springboot.medicinestore.controller;

import com.infotran.springboot.Util.DownloadFileUtil;
import com.infotran.springboot.medicinestore.model.MedicineStore;
import com.infotran.springboot.medicinestore.service.MedicineStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@Slf4j
public class Parse2WGS84Controlller {

    //政府公開口罩剩餘資訊csv檔URL
    private static final String GOV_URL = "https://data.nhi.gov.tw/Datasets/Download.ashx?rid=A21030000I-D50001-001&l=https://data.nhi.gov.tw/resource/mask/maskdata.csv";

    @Resource
    MedicineStore medicineStore;

    @Resource
    MedicineStoreService medicineStoreService;

    @Resource
    RedisTemplate<Object, MedicineStore> medicineStoreRedisTemplate;

    @Scheduled(cron = "0 0 0/1 * * ? *")
    public void runParseAddess() throws Exception {
        List<MedicineStore> storeList = new ArrayList<>();
        String[] args = DownloadFileUtil.downlaodWithHttpClientAsync(GOV_URL).split(",");
        for (int i = 7; i < args.length; i++) {
            switch (i % 7) {
                case 0:
                    medicineStore.setId(args[i]);
                    break;
                case 1:
                    medicineStore.setName(args[i]);
                    break;
                case 2:
                    medicineStore.setAddress(args[i]);
                    break;
                case 3:
                    medicineStore.setPhoneNumber(args[i]);
                    break;
                case 4:
                    medicineStore.setMaskAdult(Integer.valueOf(args[i]));
                    break;
                case 5:
                    medicineStore.setMaskChild(Integer.valueOf(args[i]));
                    break;
                case 6:
                    medicineStore.setUpdateTime(args[i]);
                    storeList.add(medicineStore);
                    break;
            }
        }
        medicineStoreRedisTemplate.opsForList().leftPushAll("medicineStore", storeList);
    }

}
