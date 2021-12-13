package com.infotran.springboot.schedular.job.webcrawlerjob;

import com.infotran.springboot.exception.LineBotException;
import com.infotran.springboot.exception.exceptionenum.LineBotExceptionEnums;
import com.infotran.springboot.queue.service.RabbitMqService;
import com.infotran.springboot.util.ClientUtil;
import com.infotran.springboot.util.HandleFileUtil;
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
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 爬蟲類排程
 * @author chris
 */
@Slf4j
@Component
public class WebCrawlerCreateJob implements ClientUtil {

    @Resource
    GetMaskJsonService getMaskJsonService;

    @Resource
    GetVaccinedInfoService getVaccinedInfoService;

    @Resource
    GetDiffCountryStatus countryStatus;

    @Resource
    RabbitMqService rabbitMqService;

    private static ExecutorService crawImgExecutor;

    @PostConstruct
    public void init(){
        crawImgExecutor = new ThreadPoolExecutor(2,4,180,
                                                java.util.concurrent.TimeUnit.SECONDS,new ArrayBlockingQueue<>(5),new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 執行 [當日新增確診數] 爬蟲 <br>
     * 每三十分鐘執行一次
     *
     * */
//    @Scheduled(fixedRate = 30 * TimeUnit.MINUTE)
    public void executeCrawlCovid() {
        Request request = new Request.Builder().url(GetCovidNumService.properties.URL).get().build();
        Call call = CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @SneakyThrows
            @Override
            @EverythingIsNonNull
            public void onFailure(Call call, IOException e) {
                log.error("執行 [當日新增確診數] 爬蟲 失敗");
                throw new LineBotException(LineBotExceptionEnums.FAIL_ON_WEBCRAWLING,e.getMessage());
            }

            @SneakyThrows
            @Override
            @EverythingIsNonNull
            public void onResponse(Call call, Response response) {
                MDC.put("job","Confirm Case");
                String jsonBody = null;
                try{
                    if(response.body()!=null){
                        jsonBody = response.body().string();
                        rabbitMqService.sendConfirmCase(jsonBody);
                    }else {
                        throw new LineBotException(LineBotExceptionEnums.FAIL_ON_BODY_RESPONSE);
                    }
                } catch (LineBotException e){
                    log.error("執行 [當日新增確診數] 爬蟲成功但響應失敗:{}",e.getMessage());
                }finally {
                    MDC.remove("job");
                }
            }
        });
    }

    /**
     * 執行 [剩餘口罩數] 爬蟲 <br>
     * (每小時執行一次)
     *
     * */
//    @Scheduled(fixedRate = TimeUnit.HOUR)
    public void executeMaskCrawl() {
        Request request = new Request.Builder().url(getMaskJsonService.MASK_URL).get().build();
        Call call = CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @SneakyThrows
            @Override
            @EverythingIsNonNull
            public void onFailure(Call call, IOException e) {
                log.error("執行 [查詢剩餘口罩數] 爬蟲 失敗");
                throw new LineBotException(LineBotExceptionEnums.FAIL_ON_WEBCRAWLING,e.getMessage());
            }

            @SneakyThrows
            @Override
            @EverythingIsNonNull
            public void onResponse( Call call, Response response) {
                MDC.put("job","Mask Info");
                String jsonBody = null;
                try{
                    if(response.body()!=null){
                        jsonBody = response.body().string();
                        rabbitMqService.sendMaskInfo(jsonBody);
                    }else {
                        throw new LineBotException(LineBotExceptionEnums.FAIL_ON_BODY_RESPONSE);
                    }
                } catch (LineBotException e){
                    log.error("執行 [查詢剩餘口罩數] 爬蟲成功但響應失敗:{}",e.getMessage());
                }finally {
                    MDC.remove("job");
                }
            }
        });
    }

    /**
     * 執行 [pdf 取得各疫苗接踵累计人次] 爬蟲<br>
     * (每12小時執行一次)
     * */
//    @Scheduled(fixedRate = 12* TimeUnit.HOUR)
    public void executeParsingPDF() {
        Request request = new Request.Builder().url(GetVaccinedInfoService.properties.PDF_URL).get().build();
        Call call = CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @SneakyThrows
            @Override
            @EverythingIsNonNull
            public void onFailure(Call call, IOException e) {
                log.error("執行 [pdf 取得各疫苗接踵累计人次] 爬蟲 失敗");
                throw new LineBotException(LineBotExceptionEnums.FAIL_ON_WEBCRAWLING,e.getMessage());
            }

            @SneakyThrows
            @Override
            @EverythingIsNonNull
            public void onResponse(Call call, Response response) {
                MDC.put("job","Vaccine PDF");
                String jsonBody = null;
                try{
                    if(Objects.nonNull(response.body())){
                        jsonBody = response.body().string();
                        rabbitMqService.sendPDFVaccinedAmount(jsonBody);
                    }else {
                        throw new LineBotException(LineBotExceptionEnums.FAIL_ON_BODY_RESPONSE);
                    }
                }finally {
                    MDC.remove("job");
                }
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
        // Pair.of(擷取的高度(長度),頁面捲動至(2205或2650)的地方)
        Pair<Integer,Integer> eachAgeCoverage = Pair.of(500,2205);
        Pair<Integer,Integer> eachCityCoverage = Pair.of(700,2650);
        try {
            FutureTask crawlCumulativeVaccineImg = new FutureTask(() -> {
                //取得累计接踵人次截圖
                getVaccinedInfoService.crawlCumulativeVaccineImg();
            }, null);
            FutureTask crawlEachAgeCoverage = new FutureTask(() -> {
                //取得各年齡疫苗涵蓋率
                getVaccinedInfoService.crawlEachBatchCoverage(eachAgeCoverage.getFirst(),eachAgeCoverage.getSecond(),GetVaccinedInfoService.eachAgeCoverFileName);
            }, null);
            FutureTask crawlEachCityCoverage = new FutureTask(() -> {
                //取得各縣市疫苗涵蓋率
                getVaccinedInfoService.crawlEachBatchCoverage(eachCityCoverage.getFirst(),eachCityCoverage.getSecond(),GetVaccinedInfoService.eachCityCoverFileName);
            }, null);
            List<FutureTask> futureTaskList = new ArrayList<>();
            futureTaskList.add(crawlCumulativeVaccineImg);
//            futureTaskList.add(crawlEachAgeCoverage);
//            futureTaskList.add(crawlEachCityCoverage);
            futureTaskList.stream().forEach(crawImgExecutor::submit);
        } finally {
            MDC.remove("job");
        }
    }

    /**
     * 執行 取得 [CDC_World COVID-19 Data] <br>
     * (每 6 小時執行一次)
     * */
//    @Scheduled(fixedRate = 6 * TimeUnit.HOUR)
    public void executeTodayWorldCovidData()  {
        MDC.put("job","CDC_World_Today_CovidData");
        log.info("url :{} ", countryStatus.CDC_WORLD_URL);
        try {
            HandleFileUtil.downloadWithFilesCopy(countryStatus.CDC_WORLD_URL,GetDiffCountryStatus.FILENAME);
            URI uri = new URI(HandleFileUtil.filePath.concat(GetDiffCountryStatus.FILENAME));
            log.info("CSV檔完整存儲路徑:{}",uri);
            Path path = Paths.get("src/main/resources/static/world");
            //取出並解壓縮成Bytes
            byte[] bytes = HandleFileUtil.decomposeGzipToBytes(path);
            //轉utf-8
            String body = new String(bytes, StandardCharsets.UTF_8);
            if(Objects.nonNull(body)){
                rabbitMqService.sendWorldCovid19Data(body);
            }
        } catch (IOException e) {
            log.error("下載CDC各國疫情csv檔失敗:{}",e.getMessage());
        } catch (URISyntaxException e) {
            log.error("URI創建失敗:{}",e.getMessage());
        } finally {
            MDC.remove("job");
        }
    }

}
