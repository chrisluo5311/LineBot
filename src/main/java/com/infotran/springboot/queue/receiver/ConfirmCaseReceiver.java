package com.infotran.springboot.queue.receiver;

import com.infotran.springboot.exception.LineBotException;
import com.infotran.springboot.webcrawler.confirmcase.service.GetCovidNumService;
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
public class ConfirmCaseReceiver {

    @Resource
    GetCovidNumService getCovidNumService;

    @RabbitListener(queues = "${webcrawler.mq.confirmcase}")
    public void receiveMessage(String json) throws LineBotException {
        MDC.put("consumer","Confirm Case Receiver");
        log.info("當日新增確診者接收處理-開始");
        getCovidNumService.getUrlOfNewsDetail(json);
        log.info("當日新增確診者接收處理-結束");
        MDC.remove("consumer");
    }

}
