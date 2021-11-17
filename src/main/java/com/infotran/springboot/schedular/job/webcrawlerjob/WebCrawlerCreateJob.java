package com.infotran.springboot.schedular.job.webcrawlerjob;

import com.infotran.springboot.exception.LineBotException;
import com.infotran.springboot.exception.exceptionenum.LineBotExceptionEnums;
import com.infotran.springboot.queue.service.RabbitMqService;
import com.infotran.springboot.util.ClientUtil;
import com.infotran.springboot.webcrawler.confirmcase.service.GetCovidNumService;
import com.infotran.springboot.webcrawler.medicinestore.service.GetMaskJsonService;
import com.infotran.springboot.webcrawler.multicountry.service.GetDiffCountryStatus;
import com.infotran.springboot.webcrawler.vaccinesvg.service.GetVaccinedInfoService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.annotations.EverythingIsNonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 爬蟲類排程
 * @author chris
 */
@Slf4j
@Component
public class WebCrawlerCreateJob implements ClientUtil {

    @Resource
    GetCovidNumService getCovidNumService;

    @Resource
    GetMaskJsonService getMaskJsonService;

    @Resource
    GetVaccinedInfoService getVaccinedInfoService;

    @Resource
    GetDiffCountryStatus countryStatus;

    @Resource
    RabbitMqService rabbitMqService;

    private static ExecutorService crawImgExecutor;

    /** 目前截圖方法總數 */
    private static final Integer PICTURE_METHOD_AMOUNT = 2;


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
//    @Scheduled(fixedRate = TimeUnit.HOUR)
    public void executeCrawlCovid() {
        // get post put 等
        Request request = new Request.Builder().url(getCovidNumService.CDC_URL).get().build();
        Call call = CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @SneakyThrows
            @Override
            @EverythingIsNonNull
            public void onFailure(Call call, IOException e) {
                log.warn("執行 [當日新增確診數] 爬蟲 失敗");
                throw new LineBotException(LineBotExceptionEnums.FAIL_ON_WEBCRAWLING,e.getMessage());
            }

            @SneakyThrows
            @Override
            @EverythingIsNonNull
            public void onResponse(Call call, Response response) {
                MDC.put("job","Confirm Case");
                String jsonBody = null;
                if(response.body()!=null){
                    jsonBody = response.body().string();
                }else {
                    throw new LineBotException(LineBotExceptionEnums.FAIL_ON_BODY_RESPONSE);
                }
                rabbitMqService.sendConfirmCase(jsonBody);
                MDC.remove("job");
            }
        });
    }


    /**
     * 執行 [剩餘口罩數] 爬蟲<br>
     * (每小時執行一次)
     *
     * */
//    @Scheduled(fixedRate = TimeUnit.HOUR)
    public void executeMaskCrawl() {
        // get
        Request request = new Request.Builder().url(getMaskJsonService.MASK_URL).get().build();
        Call call = CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @SneakyThrows
            @Override
            @EverythingIsNonNull
            public void onFailure(Call call, IOException e) {
                log.warn("執行 [查詢剩餘口罩數] 爬蟲 失敗");
                throw new LineBotException(LineBotExceptionEnums.FAIL_ON_WEBCRAWLING,e.getMessage());
            }

            @SneakyThrows
            @Override
            @EverythingIsNonNull
            public void onResponse( Call call, Response response) {
                MDC.put("job","Mask Info");
                String jsonBody = null;
                if(response.body()!=null){
                    jsonBody = response.body().string();
                }else {
                    throw new LineBotException(LineBotExceptionEnums.FAIL_ON_BODY_RESPONSE);
                }
                rabbitMqService.sendMaskInfo(jsonBody);
                MDC.remove("job");
            }
        });
    }



    /**
     * 執行 [pdf 取得各疫苗接踵累计人次] 爬蟲<br>
     * (每小時執行一次)
     * */
//    @Scheduled(fixedRate = 12* TimeUnit.HOUR)
    public void executeParsingPDF() {
        // get post put 等
        Request request = new Request.Builder().url(getVaccinedInfoService.getPdfUrl()).get().build();
        Call call = CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @SneakyThrows
            @Override
            @EverythingIsNonNull
            public void onFailure(Call call, IOException e) {
                log.warn("執行 [pdf 取得各疫苗接踵累计人次] 爬蟲 失敗");
                throw new LineBotException(LineBotExceptionEnums.FAIL_ON_WEBCRAWLING,e.getMessage());
            }

            @SneakyThrows
            @Override
            @EverythingIsNonNull
            public void onResponse(Call call, Response response) {
                MDC.put("job","Vaccined PDF");
                String jsonBody = null;
                if(response.body()!=null){
                    jsonBody = response.body().string();
                }else {
                    throw new LineBotException(LineBotExceptionEnums.FAIL_ON_BODY_RESPONSE);
                }
                rabbitMqService.sendPDFVaccinedAmount(jsonBody);
                MDC.remove("job");
            }
        });
    }

    /**
     * 執行 [截图: 累计接踵人次 & 各梯次疫苗涵蓋率] 爬蟲<br>
     * (每小時執行一次)
     * */
//    @Scheduled(fixedRate = TimeUnit.HOUR)
    public void executeVaccineScreeShot() {
        MDC.put("job","Selenium Snapshot");
        int i = 0;
        while(i < PICTURE_METHOD_AMOUNT){
            if(i==0){
                crawImgExecutor.execute(() -> {
                    getVaccinedInfoService.crawlCumulativeVaccineImg();
                });
            }else {
                crawImgExecutor.execute(() -> {
                    getVaccinedInfoService.crawlEachBatchCoverage();
                });
            }
            i++;
        }
        MDC.remove("job");
    }

    /**
     * 執行 每日 取得 [CDC_World COVID-19 Data] <br>
     * (每 6 小時執行一次)
     * */
//    @Scheduled(fixedRate = 6 * TimeUnit.HOUR)
    public void executeTodayWorldCovidData(){
        log.info("CDC World url :{} ", countryStatus.CDC_WORLD_URL);
        Request request = new Request.Builder().url(countryStatus.CDC_WORLD_URL).get().build();
        Call call = CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @SneakyThrows
            @Override
            @EverythingIsNonNull
            public void onFailure(Call call, IOException e) {
                log.warn("執行 [TODAY CDC_World COVID-19 Data] 失敗");
                throw new LineBotException(LineBotExceptionEnums.FAIL_ON_WEBCRAWLING,e.getMessage());
            }

            @SneakyThrows
            @Override
            @EverythingIsNonNull
            public void onResponse(Call call, Response response) {
                MDC.put("job","CDC_World_Today_CovidData");
                if(response.code()==404){
                    log.error("CDC World url連線有問題");
                    MDC.remove("job");
                    executeTodayWorldCovidData();
                }
                String jsonBody = null;
                if(response.body()!=null){
                    jsonBody = response.body().string();
                }else {
                    throw new LineBotException(LineBotExceptionEnums.FAIL_ON_BODY_RESPONSE);
                }
                rabbitMqService.sendWorldCovid19Data(jsonBody);
                MDC.remove("job");
            }
        });
    }



}
