package com.infotran.springboot.schedular.job.webcrawlerjob;

import com.infotran.springboot.exception.LineBotException;
import com.infotran.springboot.exception.exceptionenum.LineBotExceptionEnums;
import com.infotran.springboot.queue.service.RabbitMqService;
import com.infotran.springboot.schedular.TimeUnit;
import com.infotran.springboot.util.ClientUtil;
import com.infotran.springboot.webcrawler.confirmcase.service.GetCovidNumService;
import com.infotran.springboot.webcrawler.medicinestore.service.GetMaskJsonService;
import com.infotran.springboot.webcrawler.vaccinesvg.service.GetVaccinedInfoService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

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
    RabbitMqService rabbitMqService;

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
            @SneakyThrows
            @Override
            public void onFailure(Call call, IOException e) {
                log.warn("執行 [當日新增確診數] 爬蟲 失敗");
                throw new LineBotException(LineBotExceptionEnums.FAIL_ON_WEBCRAWLING,e.getMessage());
            }

            @SneakyThrows
            @Override
            public void onResponse(Call call, Response response) {
                MDC.put("job","Confirm Case");
                //整頁內容
                String body = response.body().string();
                if(Objects.nonNull(body)){
                    rabbitMqService.sendConfirmCase(body);
                }else {
                    throw new LineBotException(LineBotExceptionEnums.FAIL_ON_BODY_RESPONSE);
                }
                MDC.remove("job");
            }
        });
    }


    /**
     * 執行 [剩餘口罩數] 爬蟲<br>
     * (每小時執行一次)
     *
     * */
    @Scheduled(fixedRate = 1* TimeUnit.HOUR)
    public void executeMaskCrawl() throws IOException {
        Request request = new Request.Builder().url(getMaskJsonService.MASK_URL).get().build(); // get
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @SneakyThrows
            @Override
            public void onFailure(Call call, IOException e) {
                log.warn("執行 [查詢剩餘口罩數] 爬蟲 失敗");
                throw new LineBotException(LineBotExceptionEnums.FAIL_ON_WEBCRAWLING,e.getMessage());
            }

            @SneakyThrows
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                MDC.put("job","Mask Info");
                String jsonBody = response.body().string();
                if(Objects.nonNull(jsonBody)){
                    rabbitMqService.sendMaskInfo(jsonBody);
                }else {
                    throw new LineBotException(LineBotExceptionEnums.FAIL_ON_BODY_RESPONSE);
                }
                MDC.remove("job");
            }
        });
    }



    /**
     * 執行 [pdf 取得各疫苗接踵累计人次] 爬蟲<br>
     * (每小時執行一次)
     * */
    @Scheduled(fixedRate = 12* TimeUnit.HOUR)
    public void executeParsingPDF() throws InterruptedException {
        Request request = new Request.Builder().url(getVaccinedInfoService.getPdfUrl()).get().build(); // get post put 等
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @SneakyThrows
            @Override
            public void onFailure(Call call, IOException e) {
                log.warn("執行 [pdf 取得各疫苗接踵累计人次] 爬蟲 失敗");
                throw new LineBotException(LineBotExceptionEnums.FAIL_ON_WEBCRAWLING,e.getMessage());
            }

            @SneakyThrows
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                MDC.put("job","Vaccined PDF");
                String jsonBody = response.body().string();
                if(Objects.nonNull(jsonBody)){
                    rabbitMqService.sendPDFVaccinedAmount(jsonBody);
                }else {
                    throw new LineBotException(LineBotExceptionEnums.FAIL_ON_BODY_RESPONSE);
                }
                MDC.remove("job");
            }
        });
    }

    /**
     * 執行 [截图: 累计接踵人次 & 各梯次疫苗涵蓋率] 爬蟲<br>
     * (每小時執行一次)
     * */
    @Scheduled(fixedRate = 12* TimeUnit.HOUR)
    public void executeVaccineScreeShot() throws InterruptedException {
        MDC.put("job","Selenium Snapshot");
        Integer i = 0;
        while(i<2){
            if(i==0){
                crawImgExecutor.execute(() -> {
                    try {
                        getVaccinedInfoService.crawlCumulativeVaccineImg();
                    } catch (InterruptedException e) {
                        log.error("[累计接踵人次]截图輸出失敗 或 其他原因");
                    }
                });
                i++;
            }else {
                crawImgExecutor.execute(() -> {
                    try {
                        getVaccinedInfoService.crawlEachBatchCoverage();
                    } catch (InterruptedException e) {
                        log.error("[各梯次疫苗涵蓋率]截图輸出失敗 或 其他原因");
                    }
                });
                i++;
            }
        }
        MDC.remove("job");
    }


    }
