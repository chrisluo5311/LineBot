package com.infotran.springboot.queue.receiver;


import com.infotran.springboot.webcrawler.vaccinesvg.service.GetVaccinedInfoService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author chris
 */
@Slf4j
@Component
public class PDFVaccinedAmountReceiver {

    @Resource
    GetVaccinedInfoService getVaccinedInfoService;

    @RabbitListener(queues = "${webcrawler.mq.PDFVaccinedAmount}")
    public void receiveMessage(String json) {
        MDC.put("consumer","PDF Receiver");
        log.info("pdf取得各疫苗接踵累计人次接收處理-開始");
        getVaccinedInfoService.crawlPDFVaccinedAmount(json);
        log.info("pdf取得各疫苗接踵累计人次接收處理-結束");
        MDC.remove("consumer");
    }
}
