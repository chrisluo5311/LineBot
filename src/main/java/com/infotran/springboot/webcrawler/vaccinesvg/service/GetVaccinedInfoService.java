package com.infotran.springboot.webcrawler.vaccinesvg.service;

import com.infotran.springboot.exception.LineBotException;
import com.infotran.springboot.exception.exceptionenum.LineBotExceptionEnums;
import com.infotran.springboot.util.ClientUtil;
import com.infotran.springboot.util.DownloadFileUtil;
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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
public class GetVaccinedInfoService implements ClientUtil {

    private static final String LOG_PREFIX = "[GetVaccinedInfoService 截圖]";

    //疫苗施打統計(infogram)標題:誰打了疫苗
    @Value("${VACCINE.IMG.URL}")
    private String VACCINE_IMG_URL;

    //全球疫情地圖之疫苗接種統計圖
    @Value("${VACCINE.URL}")
    private String VACCINE_URL;

    //CDC疫苗統計資料pdf
    @Value("${PDF.URL}")
    private String PDF_URL;

    //疫苗統計資料PDF網址前綴
    @Value("${CDC_URL_PREFIX}")
    private String CDC_URL_PREFIX;

    //selenium使用的driver
    private static final String SYSTEM_DRIVER = "webdriver.chrome.driver";

    //chromedirver系统路径
    private static final String SYSTEM_PATH = "E:\\javalib\\selenium\\webdrivers\\chromedriver.exe";

    //累计接踵人次截圖FileName
    private static final String cumuFileName = "cumulativeVaccined.jpg";

    //各梯次疫苗涵蓋率图FileName
    private static final String coverFileName = "eachBatchCoverage.jpg";

    @Resource
    CheckPDFRecordServiceImpl checkPDFRecordService;

    @Resource
    VaccinedPeopleServiceImpl vaccinedPeopleService;

    public String getPdfUrl(){
        return this.PDF_URL;
    }

    /**
     * 前往疫苗施打統計(infogram 標題:誰打了疫苗)<br>
     * 取得累计接踵人次截圖
     *
     * */
    public void crawlCumulativeVaccineImg() throws InterruptedException, LineBotException {
        System.setProperty(SYSTEM_DRIVER,SYSTEM_PATH);
        WebDriver driver = new ChromeDriver();
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
            throw new LineBotException(LineBotExceptionEnums.FAIL_ON_OUTPUT_FILE,e.getMessage());
        } finally {
            driver.quit();
        }
    }

    /**
     * 全球疫情地圖之疫苗接種統計圖<br>
     * 取得各梯次疫苗涵蓋率
     *
     * */
    public void crawlEachBatchCoverage() throws InterruptedException, LineBotException {
        System.setProperty(SYSTEM_DRIVER,SYSTEM_PATH);
        WebDriver driver = new ChromeDriver();
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
            throw new LineBotException(LineBotExceptionEnums.FAIL_ON_OUTPUT_FILE,e.getMessage());
        } finally {
            driver.quit();
        }
    }

    /**
     * 解析pdf並取得各疫苗接踵累计人次
     * @param body
     */
    public void crawlVaccinedAmount(String body) throws IOException {
        StringBuilder fullUrl = new StringBuilder();
        Document doc = Jsoup.parse(body);
        //只取得第一個<p>
        Element firstP = doc.getElementsByClass("download").get(0).child(1);
        Element ancherPdf = firstP.child(1);
        String title = ancherPdf.attr("title");
        log.info("{} 第一個pdf的標題: {}",LOG_PREFIX,title);
        //判別日期並回傳日期
        String dateNum = verifyDate(title);
        log.info("title的日期: {}",dateNum);
        String isNew = checkPDFRecordService.findByUploadTime(dateNum);
        log.info(isNew);
        if(Objects.nonNull(dateNum) && CheckPDFRecordServiceImpl.ISNEWPDF.equals(isNew)){
            //紀錄新的一筆
            VaccinedPDFRecord vaccinedPDFRecord = VaccinedPDFRecord.builder()
                    .uploadTime(dateNum)
                    .build();
            checkPDFRecordService.save(vaccinedPDFRecord);
            //提取url
            String suffixUrl = ancherPdf.attr("href");
            fullUrl.append(CDC_URL_PREFIX).append(suffixUrl);
            log.info("{} pdf下載連結url: {}",LOG_PREFIX,fullUrl.toString());
            //pdf轉換成文字
            String content = PDFBoxUtil.readPDF(fullUrl.toString());
            //解析內容
            String result = parseContent(content);//結果(累計接踵疫苗人次字串)
            String todayDate =TimeUtil.formTodayDate();//當日日期YYYY-MM-DD
            VaccineTypePeople vPeople= VaccineTypePeople.builder()
                    .resourceUrl(fullUrl.toString())
                    .body(result)
                    .createTime(todayDate)
                    .build();
            vaccinedPeopleService.save(vPeople);
            //儲存至static
            title += ".pdf";
            DownloadFileUtil.downloadWithFilesCopy(fullUrl.toString(),title);
        }else{ //為null代表非統計資料的pdf
            log.info("[{} pdf檔尚未更新 或 此檔案非統計資料表 或 此檔案已新增過 ]",LOG_PREFIX);
        }
    }

    /**
     * 提取數字並返回日期
     * 若非日期返回null
     * @param title 下載標題
     * */
    private String verifyDate(String title){
        Integer index = 0;
        Integer sum = 0;
        while (Character.isDigit(title.charAt(index))) {
            sum = sum*10 + Character.getNumericValue(title.charAt(index));
            index++;
        }
        return sum==0?null:String.valueOf(sum);
    }

    /**
     * 透過句號分割字串
     * 只取第2個和第3個字串
     * @param content pdf內文
     * @return String 擷取(累計接踵疫苗人次字串)
     * */
    private String parseContent(String content){
        String[] strings = content.split("。");
        StringBuilder result = new StringBuilder();
        result.append(strings[1]).append(strings[2]);
        log.info("{} 字串解析結果: {}",LOG_PREFIX,result.toString());
        return result.toString();
    }



}
