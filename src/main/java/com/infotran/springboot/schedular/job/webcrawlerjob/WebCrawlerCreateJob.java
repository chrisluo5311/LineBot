package com.infotran.springboot.schedular.job.webcrawlerjob;

import com.infotran.springboot.exception.LineBotException;
import com.infotran.springboot.exception.exceptionenum.LineBotExceptionEnums;
import com.infotran.springboot.schedular.TimeUnit;
import com.infotran.springboot.util.ClientUtil;
import com.infotran.springboot.webcrawler.confirmcase.model.ConfirmCase;
import com.infotran.springboot.webcrawler.confirmcase.service.ConfirmCaseService;
import com.infotran.springboot.webcrawler.confirmcase.service.GetCovidNumService;
import com.infotran.springboot.webcrawler.medicinestore.model.MedicineStore;
import com.infotran.springboot.webcrawler.medicinestore.service.GetMaskJsonService;
import com.infotran.springboot.webcrawler.medicinestore.service.MedicineStoreService;
import com.infotran.springboot.webcrawler.vaccinesvg.service.GetVaccinedInfoService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class WebCrawlerCreateJob implements ClientUtil {

    private static final String LOG_PREFIX = "[WebCrawlerCreateJob定時任務]";

    @Resource
    GetCovidNumService getCovidNumService;

    @Resource
    GetMaskJsonService getMaskJsonService;

    @Resource
    GetVaccinedInfoService getVaccinedInfoService;

    @Resource
    RedisTemplate<Object, MedicineStore> medicineStoreRedisTemplate;

    @Autowired
    private ConfirmCaseService confirmCaseService;

    @Resource
    private MedicineStoreService medicineStoreService;

    /**
     * 執行 [當日新增確診數] 爬蟲
     * 每天14:00開始到14:55，每五分鐘執行一次
     *
     * */
    @Scheduled(cron = "0 0/5 14 * * ?")
    public void executeCrawlCovid() throws IOException {
        ConfirmCase confirmCase = confirmCaseService.findByConfirmTime(LocalDate.now());
        if (confirmCase!=null) return;
        log.info("{} CDC_URL 新聞首頁: {} ",LOG_PREFIX,getCovidNumService.CDC_URL);
        Request request = new Request.Builder().url(getCovidNumService.CDC_URL).get().build(); // get post put 等
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.warn("@@@@@@ {} 執行 [當日新增確診數] 爬蟲 失敗!!! @@@@@@");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                log.info("@@@@@@ {} 執行 [當日新增確診數] 爬蟲 @@@@@@",LOG_PREFIX);
                String body = response.body().string();//整頁內容
                String detailUrl = getCovidNumService.getURLOfNewsDetail(body);
                getCovidNumService.parseBody(detailUrl);
            }
        });
    }


    /**
     * 執行 [剩餘口罩數] 爬蟲<br>
     * (每小時執行一次)
     * */
    @Scheduled(fixedRate = 1* TimeUnit.HOUR)
    public void executeMaskCrawl() throws IOException {
        log.info("{} MASK_URL: {}",LOG_PREFIX,getMaskJsonService.MASK_URL);
        Request request = new Request.Builder().url(getMaskJsonService.MASK_URL).get().build(); // get
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.warn("@@@@@@ {} 執行 [剩餘口罩數] 爬蟲 失敗!!! @@@@@@");
                e.printStackTrace();
            }

            @SneakyThrows
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                log.info("@@@@@@ {} 執行 [剩餘口罩數] 爬蟲 @@@@@@",LOG_PREFIX);
                String jsonBody = response.body().string();
                getMaskJsonService.parseMaskInfo(jsonBody);
            }
        });
    }

    /**
     * 定時新增至資料庫(每個小時)<p>
     * 使用自定義hibernate.jdbc.batch_size=1000
     * Batch Size是設定對資料庫進行批量刪除，批量更新和批量插入的時候的批次大小
     * */
    @Scheduled(fixedRate = 1* TimeUnit.HOUR)
    private void scheduledSaving () throws Exception {
        log.info("@@@@@@ {} 執行 [定時新增至資料庫] @@@@@@");
        List<MedicineStore> medList = medicineStoreRedisTemplate.opsForList().range(GetMaskJsonService.REDIS_KEY, 0, -1);
        log.info("{} 從redis 取出所有藥局數量 {}",LOG_PREFIX,medList.size());
        List<MedicineStore> response = medicineStoreService.saveAll(medList);
        log.info("{} 儲存DB後的response物件數量 {}",LOG_PREFIX,response.size());
        if (response==null) {
            log.warn("@@@@@@ {} 執行 [定時新增至資料庫] 失敗!!! @@@@@@");
            throw new LineBotException(LineBotExceptionEnums.FAIL_ON_SAVING_RESPONSE);
        }
    }

    /**
     * 執行 [截图: 累计接踵人次 & 各梯次疫苗涵蓋率 & 取得各疫苗接踵累计人次] 爬蟲<br>
     * (每小時執行一次)
     * */
    @Scheduled(fixedRate = 12* TimeUnit.HOUR)
    public void executeVaccineScreeShot() throws InterruptedException {
        log.info("@@@@@@ {} 執行 [截图: 累计接踵人次 & 各梯次疫苗涵蓋率 & 取得各疫苗接踵累计人次] 爬蟲 @@@@@@",LOG_PREFIX);
        GetVaccinedInfoService getVaccinedInfoService= new GetVaccinedInfoService();

        GetVaccinedInfoService.CumulativeVaccineImg cumulativeVaccineImg = getVaccinedInfoService.new CumulativeVaccineImg();
        GetVaccinedInfoService.EachBatchCoverage eachBatchCoverage = getVaccinedInfoService.new EachBatchCoverage();
//        cumulativeVaccineImg.start();
//        eachBatchCoverage.start();
        /********************************** 取得各疫苗接踵累计人次 ***************************/
        GetVaccinedInfoService.VaccinedTypeAmount vaccinedTypeAmount = getVaccinedInfoService.new VaccinedTypeAmount();
        vaccinedTypeAmount.run();
    }

}