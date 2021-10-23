package com.infotran.springboot.schedular.job.webcrawlerjob;

import com.infotran.springboot.schedular.TimeUnit;
import com.infotran.springboot.util.ClientUtil;
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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

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

    private static ExecutorService crawImgExecutor;

    @PostConstruct
    public void init(){
        crawImgExecutor = new ThreadPoolExecutor(2,4,180,
                                                java.util.concurrent.TimeUnit.SECONDS,new ArrayBlockingQueue<>(5),new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 執行 [當日新增確診數] 爬蟲
     * 每天14:00開始到14:55，每五分鐘執行一次
     *
     * */
    @Scheduled(fixedRate = 1* TimeUnit.HOUR)
    public void executeCrawlCovid() throws IOException {
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
                log.info("@@@@@@@@@@@@@@@ {} 執行 [當日新增確診數] 爬蟲 @@@@@@@@@@@@@@@",LOG_PREFIX);
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
                log.info("@@@@@@@@@@@@@@@ {} 執行 [剩餘口罩數] 爬蟲 @@@@@@@@@@@@@@@",LOG_PREFIX);
                String jsonBody = response.body().string();
                getMaskJsonService.parseMaskInfo(jsonBody);
            }
        });
    }



    /**
     * 執行 [pdf 取得各疫苗接踵累计人次] 爬蟲<br>
     * (每小時執行一次)
     * */
    @Scheduled(fixedRate = 12* TimeUnit.HOUR)
    public void executeParsingPDF() throws InterruptedException {
        log.info("@@@@@@@@@@@@@@@ {} 執行 [pdf 取得各疫苗接踵累计人次] 爬蟲 @@@@@@@@@@@@@@@",LOG_PREFIX);
        Request request = new Request.Builder().url(getVaccinedInfoService.getPdfUrl()).get().build(); // get post put 等
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.warn("@@@@@@ {} 執行 [當日新增確診數] 爬蟲 失敗!!! @@@@@@");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();//整頁內容
                getVaccinedInfoService.crawlVaccinedAmount(body);
            }
        });
    }

    /**
     * 執行 [截图: 累计接踵人次 & 各梯次疫苗涵蓋率] 爬蟲<br>
     * (每小時執行一次)
     * */
    @Scheduled(fixedRate = 12* TimeUnit.HOUR)
    public void executeVaccineScreeShot() throws InterruptedException {
        log.info("@@@@@@@@@@@@@@@ {} 執行 [截图: 累计接踵人次 & 各梯次疫苗涵蓋率] 爬蟲 @@@@@@@@@@@@@@@",LOG_PREFIX);
        String LOG_PREFIX = "executeVaccineScreeShot";
        for(int i = 0; i < 2 ; i++){
            int finalI = i;
            crawImgExecutor.execute(() -> {
                try {
                    if(finalI ==0){
                        getVaccinedInfoService.crawlCumulativeVaccineImg();
                    }else {
                        getVaccinedInfoService.crawlEachBatchCoverage();
                    }
                } catch (RejectedExecutionException | InterruptedException e){
                    log.info("{} 截图: 累计接踵人次 & 各梯次疫苗涵蓋率 失敗",LOG_PREFIX);
                } finally {

                }
            });
        }


    }

}