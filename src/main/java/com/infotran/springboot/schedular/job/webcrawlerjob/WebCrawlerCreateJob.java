package com.infotran.springboot.schedular.job.webcrawlerjob;

import com.infotran.springboot.webcrawler.confirmcase.controller.GetCovidNumController;
import com.infotran.springboot.webcrawler.medicinestore.controller.GetMaskJsonController;
import com.infotran.springboot.webcrawler.vaccinesvg.controller.GetVaccineSVGController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class WebCrawlerCreateJob {

    @Resource
    GetCovidNumController getCovidNumController;

    @Resource
    GetMaskJsonController getMaskJsonController;

    @Resource
    GetVaccineSVGController getVaccineSVGController;




}
