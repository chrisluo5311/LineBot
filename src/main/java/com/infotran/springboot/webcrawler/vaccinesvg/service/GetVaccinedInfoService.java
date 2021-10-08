package com.infotran.springboot.webcrawler.vaccinesvg.service;

import com.infotran.springboot.util.ClientUtil;
import com.infotran.springboot.util.DownloadFileUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
public class GetVaccinedInfoService implements ClientUtil {

    private static final String LOG_PREFIX = "[GetVaccineSVG]";

    //全球疫情地圖之疫苗接種統計圖
    @Value("${VACCINE.URL}")
    private static String VACCINE_URL;

    //疫苗施打統計(infogram)標題:誰打了疫苗
    @Value("${VACCINE.IMG.URL}")
    private static String VACCINE_IMG_URL;

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
    public static class CumulativeVaccineImg extends Thread{

        @SneakyThrows
        public void run(){
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
    public static class EachBatchCoverage extends Thread{

        @SneakyThrows
        public void run(){
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
                e.printStackTrace();
            }
            driver.quit();
        }
    }

    /**
     * 取得各疫苗接踵累计人次
     * todo 取得各疫苗接踵累计人次(解析pdf)
     */
    public static class getVaccinedTypeAmount implements Runnable{

        @Override
        public void run() {

        }


    }

}
