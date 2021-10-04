package com.infotran.springboot.webcrawler.vaccinesvg.controller;

import com.infotran.springboot.schedular.TimeUnit;
import com.infotran.springboot.util.ClientUtil;
import com.infotran.springboot.util.DownloadFileUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;

@Slf4j
@Controller
public class GetVaccineSVG implements ClientUtil,CommandLineRunner {

    private static final String LOG_PREFIX = "[GetVaccineSVG]";

    //全球疫情地圖之疫苗接種統計圖
    @Value("${VACCINE.URL}")
    private String VACCINE_URL;

    //疫苗施打統計(infogram)標題:誰打了疫苗
    @Value("${VACCINE.IMG.URL}")
    private String VACCINE_IMG_URL;

    private static final String SYSTEM_DRIVER = "webdriver.chrome.driver";

    private static final String SYSTEM_PATH = "E:\\javalib\\selenium\\webdrivers\\chromedriver.exe";

    private static final String cumuFileName = "cumulativeVaccined.jpg";

    private static final String coverFileName = "eachBatchCoverage.jpg";

    @Override
    public void run(String... args) throws Exception {
        executeVaccineScreeShot();
    }

    @Scheduled(fixedRate = 12* TimeUnit.HOUR)
    public void executeVaccineScreeShot() throws InterruptedException {
        CumulativeVaccineImg cumulativeVaccineImg = new CumulativeVaccineImg();
        EachBatchCoverage eachBatchCoverage = new EachBatchCoverage();
        cumulativeVaccineImg.start();
        eachBatchCoverage.start();
    }

    /**
     * 前往疫苗施打統計(infogram 標題:誰打了疫苗)<br>
     * 取得累计接踵人次截圖
     *
     * */
    public class CumulativeVaccineImg extends Thread{

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
    public class EachBatchCoverage extends Thread{

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

}
