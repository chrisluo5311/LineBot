package com.infotran.springboot.webcrawler.vaccinesvg.controller;

import com.infotran.springboot.annotation.ChromeWebDriver;
import com.infotran.springboot.schedular.TimeUnit;
import com.infotran.springboot.util.ClientUtil;
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

    private String LOG_PREFIX = "[GetVaccineSVG]";

    @Value("${VACCINE.URL}")
    private String VACCINE_URL;

    private String VACCINE_IMG_URL = "https://infogram.com/f25f5a66-bd5e-4272-b4b4-be1258a276a8";

    @Override
    public void run(String... args) throws Exception {
    }

    @Scheduled(fixedRate = 12* TimeUnit.HOUR)
    public void executeVaccineScreeShot() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver","E:\\javalib\\selenium\\webdrivers\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get(VACCINE_IMG_URL);
        driver.manage().window().setSize(new Dimension(886,500));
        Thread.sleep(2000);
        //截图: 累计接踵人次
        ((JavascriptExecutor)driver).executeScript("window.scrollTo(0,1068);");
        ((JavascriptExecutor)driver).executeScript("return document.body.style.overflow = 'hidden';");
        Thread.sleep(2000);
        File srcFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(srcFile,new File("D:\\IdeaProject\\LineBot\\src\\main\\resources\\static\\screenshot.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        driver.quit();
    }


}
