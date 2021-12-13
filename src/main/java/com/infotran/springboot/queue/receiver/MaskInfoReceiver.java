package com.infotran.springboot.queue.receiver;

import com.infotran.springboot.exception.LineBotException;
import com.infotran.springboot.webcrawler.medicinestore.service.GetMaskJsonService;
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
public class MaskInfoReceiver {

    @Resource
    GetMaskJsonService getMaskJsonService;

    @RabbitListener(queues = "${webcrawler.mq.maskinfo}")
    public void receiveMessage(String json) throws LineBotException {
        MDC.put("consumer","執行查詢剩餘口罩:");
        log.info("查詢剩餘口罩數接收處理-開始");
        getMaskJsonService.parseMaskInfo(json);
        log.info("查詢剩餘口罩數接收處理-結束");
        MDC.remove("consumer");
    }
}
