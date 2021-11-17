package com.infotran.springboot.queue.receiver;

import com.infotran.springboot.webcrawler.multicountry.service.GetDiffCountryStatus;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * CDC world status csv receiver
 * @author chris
 */
@Slf4j
@Component
public class DiffCountryDataReceiver {

    @Resource
    GetDiffCountryStatus diffCountryStatus;

    @RabbitListener(queues = "${webcrawler.mq.JHUCovidData}")
    public void receiveMessage(String json) {
        MDC.put("consumer","JHU_MQ_");
        log.info("各國疫情狀況接收處理-開始");
        diffCountryStatus.parseCsvInfo(json);
        log.info("各國疫情狀況接收處理-結束");
        MDC.remove("consumer");
    }
}
