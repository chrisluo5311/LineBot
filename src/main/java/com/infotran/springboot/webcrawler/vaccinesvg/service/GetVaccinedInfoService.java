package com.infotran.springboot.webcrawler.vaccinesvg.service;

import com.infotran.springboot.util.ClientUtil;
import com.infotran.springboot.util.HandleFileUtil;
import com.infotran.springboot.util.PDFBoxUtil;
import com.infotran.springboot.util.TimeUtil;
import com.infotran.springboot.webcrawler.vaccinesvg.model.VaccineTypePeople;
import com.infotran.springboot.webcrawler.vaccinesvg.model.VaccinedPDFRecord;
import com.infotran.springboot.webcrawler.vaccinesvg.service.Impl.CheckPDFRecordServiceImpl;
import com.infotran.springboot.webcrawler.vaccinesvg.service.Impl.VaccinedPeopleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * 取得 <br>
 * 1. 累计接踵人次截圖 <br>
 * 2. 各梯次疫苗涵蓋率圖 <br>
 * 3. 解析pdf並取得各疫苗接踵累计人次 <br>
 *
 * @author chris
 */
@Slf4j
@Service
public class GetVaccinedInfoService implements ClientUtil {

    @Resource
    CheckPDFRecordServiceImpl checkPDFRecordService;

    @Resource
    VaccinedPeopleServiceImpl vaccinedPeopleService;

    private static String LOG_PREFIX= "[GetVaccinedInfoService 截圖]";
    /** selenium使用的driver */
    private static String SYSTEM_DRIVER = "webdriver.chrome.driver";
    /** chromedirver系统路径 */
    private static String SYSTEM_PATH = "E:\\javalib\\selenium\\webdrivers\\chromedriver.exe";
    /** 累计接踵人次截圖FileName */
    private static String cumuFileName = "cumulativeVaccined.jpg" ;
    /** 各梯次疫苗涵蓋率图FileName */
    private static String coverFileName = "eachBatchCoverage.jpg";


    /**
     * 前往疫苗施打統計(infogram 標題:誰打了疫苗)<br>
     * 取得累计接踵人次截圖
     *
     * */
    public void crawlCumulativeVaccineImg()  {
        System.setProperty(SYSTEM_DRIVER,SYSTEM_PATH);
        WebDriver driver = new ChromeDriver();
        try {
            driver.get(properties.VACCINE_IMG_URL);
            driver.manage().window().setSize(new Dimension(886,550));
            Thread.sleep(1000);
            //截图: 累计接踵人次
            ((JavascriptExecutor)driver).executeScript("window.scrollTo(0,845);");
            ((JavascriptExecutor)driver).executeScript("return document.body.style.overflow = 'hidden';");
            Thread.sleep(1000);
            File cumulativeVaccinedFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            //累计接踵人次截圖
            StringBuilder fullPath = new StringBuilder().append(HandleFileUtil.filePath).append(cumuFileName);
            FileUtils.copyFile(cumulativeVaccinedFile,new File(fullPath.toString()));
        } catch (IOException | InterruptedException e) {
            log.error("累计接踵人次截圖 圖片輸出失敗:{}",e.getMessage());
        } finally {
            driver.quit();
        }
    }

    /**
     * 全球疫情地圖之疫苗接種統計圖<br>
     * 取得各梯次疫苗涵蓋率
     *
     * */
    public void crawlEachBatchCoverage() {
        System.setProperty(SYSTEM_DRIVER,SYSTEM_PATH);
        WebDriver driver = new ChromeDriver();
        try {
            driver.get(properties.VACCINE_URL);
            driver.manage().window().setSize(new Dimension(1100,700));
            Thread.sleep(1000);
            //截图: 各梯次疫苗涵蓋率
            ((JavascriptExecutor)driver).executeScript("window.scrollTo(0,1960);");
            ((JavascriptExecutor)driver).executeScript("return document.body.style.overflow = 'hidden';");
            Thread.sleep(1000);
            File cumulativeVaccinedFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            //各梯次疫苗涵蓋率
            StringBuilder fullPath = new StringBuilder().append(HandleFileUtil.filePath).append(coverFileName);
            FileUtils.copyFile(cumulativeVaccinedFile,new File(fullPath.toString()));
        } catch (IOException | InterruptedException e) {
            log.error("取得各梯次疫苗涵蓋率 圖片輸出失敗:{}",e.getMessage());
        } finally {
            driver.quit();
        }
    }

    /**
     * 解析pdf並取得各疫苗接踵累计人次
     * @param body
     */
    public void crawlPdfVaccinedAmount(String body) {
        StringBuilder fullUrl = new StringBuilder();
        try {
            Document doc = Jsoup.parse(body);
            //只取得第一個<p>
            Element ancherPdf = doc.getElementsByClass("download").get(0).child(1).child(1);
            String title = ancherPdf.attr("title");
            //判別日期並回傳日期
            String dateNum = TimeUtil.verifyDate(title);
            String isNew = checkPDFRecordService.findByUploadTime(dateNum);
            if(Objects.nonNull(dateNum) && CheckPDFRecordServiceImpl.ISNEWPDF.equals(isNew)){
                //提取url
                fullUrl.append(properties.PREFIX).append(ancherPdf.attr("href"));
                //pdf轉換成文字
                String content = PDFBoxUtil.readPDF(fullUrl.toString());
                //解析內容 :累計接踵疫苗人次字串
                String result = parseContent(content,new StringBuilder()).trim();
                //记录内容
                VaccineTypePeople vPeople= VaccineTypePeople.builder()
                                                            .resourceUrl(fullUrl.toString())
                                                            .body(result)
                                                            .createTime(TimeUtil.formTodayDate())
                                                            .build();
                VaccineTypePeople vaccineTypePeople = vaccinedPeopleService.save(vPeople);
                if(Objects.isNull(vaccineTypePeople)){
                    log.error("pdf解析成功 pdf内文新增至db失败");
                }
                //记录新的一筆
                VaccinedPDFRecord vaccinedPDFRecord = VaccinedPDFRecord.builder()
                                                                       .uploadTime(dateNum)
                                                                       .build();
                VaccinedPDFRecord pdfRecord = checkPDFRecordService.save(vaccinedPDFRecord);
                if(Objects.isNull(pdfRecord)){
                    log.error("pdf解析成功、pdf内文新增成功  pdf记录至db失败");
                }
                //儲存至static resource
                HandleFileUtil.downloadWithFilesCopy(fullUrl.toString(),title.concat(".pdf"));
            }else{
                log.warn("{} pdf檔尚未更新 或 pdf檔非統計資料表 或 pdf檔已新增過",LOG_PREFIX);
            }
        } catch (IOException e) {
            log.error("pdf輸出失敗或解析失敗:{}", e.getMessage());
        }
    }

    /**
     * 透過句號分割字串 <br>
     * 只取第2個和第3個字串
     * @param content pdf內文
     * @return String 擷取(累計接踵疫苗人次字串)
     * */
    private String parseContent(String content,StringBuilder result){
        String[] strings = content.split("。");
        return result.append(strings[1]).append(strings[2]).toString();
    }


    /**
     * VaccinedInfo相關參數配置<br>
     * 對應 webcrawl.properties(prefix = vaccineinfo)<br>
     * @author chris
     * */
    @ConstructorBinding
    @ConfigurationProperties(prefix = "vaccineinfo")
    public static class properties {

        /** 疫苗施打統計(infogram)標題:誰打了疫苗 */
        public static String VACCINE_IMG_URL;

        /** 全球疫情地圖之疫苗接種統計圖 */
        public static String VACCINE_URL;

        /** CDC疫苗統計資料pdf */
        public static String PDF_URL;

        /** 疫苗統計資料PDF網址前綴 */
        @Value("${CDC_URL_PREFIX}")
        public static String PREFIX;

        public properties(String VACCINE_IMG_URL,String VACCINE_URL,String PDF_URL,String PREFIX) {
            this.VACCINE_IMG_URL = VACCINE_IMG_URL;
            this.VACCINE_URL = VACCINE_URL;
            this.PDF_URL = PDF_URL;
            this.PREFIX = PREFIX;
        }
    }

}
