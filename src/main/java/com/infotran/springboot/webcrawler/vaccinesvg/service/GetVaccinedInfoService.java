package com.infotran.springboot.webcrawler.vaccinesvg.service;

import com.infotran.springboot.util.ClientUtil;
import com.infotran.springboot.util.DownloadFileUtil;
import com.infotran.springboot.util.PDFBoxUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
public class GetVaccinedInfoService implements ClientUtil {

    private static final String LOG_PREFIX = "[GetVaccineSVG 截圖]";

    //selenium使用的driver
    private static final String SYSTEM_DRIVER = "webdriver.chrome.driver";

    //chromedirver系统路径
    private static final String SYSTEM_PATH = "E:\\javalib\\selenium\\webdrivers\\chromedriver.exe";

    //累计接踵人次截圖FileName
    private static final String cumuFileName = "cumulativeVaccined.jpg";

    //各梯次疫苗涵蓋率图FileName
    private static final String coverFileName = "eachBatchCoverage.jpg";

    /**
     * 前往疫苗施打統計(infogram 標題:誰打了疫苗)<br>
     * 取得累计接踵人次截圖
     *
     * */
    @Component
    public class CumulativeVaccineImg extends Thread{

        //疫苗施打統計(infogram)標題:誰打了疫苗
        @Value("${VACCINE.IMG.URL}")
        private String VACCINE_IMG_URL;

        @SneakyThrows
        @PostConstruct
        public void run(){
            System.setProperty(SYSTEM_DRIVER,SYSTEM_PATH);
            WebDriver driver = new ChromeDriver();
            log.info("{} 累计接踵人次截圖VACCINE_IMG_URL: {} ",LOG_PREFIX,VACCINE_IMG_URL);
            driver.get(VACCINE_IMG_URL);
            driver.manage().window().setSize(new Dimension(886,500));
            Thread.sleep(2000);
            //截图: 累计接踵人次
            ((JavascriptExecutor)driver).executeScript("window.scrollTo(0,1068);");
            ((JavascriptExecutor)driver).executeScript("return document.body.style.overflow = 'hidden';");
            Thread.sleep(2000);
            File cumulativeVaccinedFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            try {
                //累计接踵人次截圖
                StringBuilder fullPath = new StringBuilder();
                fullPath.append(DownloadFileUtil.filePath).append(cumuFileName);
                FileUtils.copyFile(cumulativeVaccinedFile,new File(fullPath.toString()));
            } catch (IOException e) {
                log.info("{} 截取[累计接踵人次截圖] 失败! ",LOG_PREFIX);
                e.printStackTrace();
            }
            driver.quit();
        }
    }

    /**
     * 全球疫情地圖之疫苗接種統計圖<br>
     * 取得各梯次疫苗涵蓋率
     *
     * */
    @Component
    public class EachBatchCoverage extends Thread{

        //全球疫情地圖之疫苗接種統計圖
        @Value("${VACCINE.URL}")
        private String VACCINE_URL;

        @SneakyThrows
        @PostConstruct
        public void run(){
            System.setProperty(SYSTEM_DRIVER,SYSTEM_PATH);
            WebDriver driver = new ChromeDriver();
            log.info("{} 各梯次疫苗涵蓋率VACCINE_URL: {} ",LOG_PREFIX,VACCINE_URL);
            driver.get(VACCINE_URL);
            driver.manage().window().setSize(new Dimension(1100,700));
            Thread.sleep(2000);
            //截图: 各梯次疫苗涵蓋率
            ((JavascriptExecutor)driver).executeScript("window.scrollTo(0,1960);");
            ((JavascriptExecutor)driver).executeScript("return document.body.style.overflow = 'hidden';");
            Thread.sleep(2000);
            File cumulativeVaccinedFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            try {
                //各梯次疫苗涵蓋率
                StringBuilder fullPath = new StringBuilder();
                fullPath.append(DownloadFileUtil.filePath).append(coverFileName);
                FileUtils.copyFile(cumulativeVaccinedFile,new File(fullPath.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            driver.quit();
        }
    }

    /**
     * 取得各疫苗接踵累计人次
     * todo 取得各疫苗接踵累计人次(解析pdf)
     */
    @Component
    public class VaccinedTypeAmount {

        //衛福部COVID-19疫苗統計資料PDF網址
        @Value("${PDF.URL}")
        private String VACCINED_STATISTICS_PDF_URL;

        //衛福部COVID-19疫苗統計資料PDF的fileName
        private static final String pdfFileName = "statisticPdfFileName.pdf";

        //疫苗統計資料PDF網址前綴
        private static final String CDC_URL_PREFIX = "https://www.cdc.gov.tw";

        @PostConstruct
        public void crawlVaccinedAmount() {
            log.info("疫苗統計資料PDF網址 VACCINED_STATISTICS_PDF_URL: {} ",VACCINED_STATISTICS_PDF_URL);
            Request request = new Request.Builder().url(VACCINED_STATISTICS_PDF_URL).get().build(); // get post put 等
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    log.warn("@@@@@@ {} 執行 [當日新增確診數] 爬蟲 失敗!!! @@@@@@");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    log.info("@@@@@@ {} 執行 [COVID-19疫苗統計資料PDF] 爬蟲 @@@@@@",LOG_PREFIX);
                    StringBuilder fullUrl = new StringBuilder();
                    String body = response.body().string();//整頁內容
                    Document doc = Jsoup.parse(body);
                    //只取得第一個<p>
                    Element firstP = doc.getElementsByClass("download").get(0).child(1);
                    log.info("{} 第一個下載連結源碼: {}",LOG_PREFIX,firstP.toString());
                    Element ancherPdf = firstP.child(1);
                    //判別是否為日期
                    String title = ancherPdf.attr("title");
                    String dateNum = verifyDate(title);//只判別是否為數字因為非日更

                    if(Objects.nonNull(dateNum)){
                        String suffixUrl = ancherPdf.attr("href");
                        log.info("{} pdf下載連結url: {}",LOG_PREFIX,ancherPdf.toString(),suffixUrl);
                        fullUrl.append(CDC_URL_PREFIX).append(suffixUrl);
                        String content = PDFBoxUtil.readPDF(fullUrl.toString());
                        //todo 解析pdf內容
                    }else{
                        log.info("pdf檔尚未更新或非統計資料表");
                    }


                }
            });
        }

        /**
         * 返回日期
         * @param title 下載標題
         * */
        private String verifyDate(String title){
            Integer index = 0;
            while (Character.isDigit(title.charAt(index))) {
                index = index*10 + Character.getNumericValue(title.charAt(index));
                index++;
            }
            return index==0?null:String.valueOf(index);
        }

    }

}
