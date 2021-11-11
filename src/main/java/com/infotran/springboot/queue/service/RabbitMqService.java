package com.infotran.springboot.queue.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author chris
 */
@Slf4j
@Service
public class RabbitMqService {

    @Value("${webcrawler.mq.exchange}")
    String TOPIC_WEBCRAWLER_EXCHANGE;

    @Value("${webcrawler.mq.routingkey.confirmcase}")
    String ROUTING_KEY_CONFIRMCASE;

    @Value("${webcrawler.mq.routingkey.maskinfo}")
    String ROUTING_KEY_MASKINFO;

    @Value("${webcrawler.mq.routingkey.PDFVaccinedAmount}")
    String ROUTING_KEY_PDFVaccinedAmount;

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendConfirmCase(String body){
        rabbitTemplate.convertAndSend(TOPIC_WEBCRAWLER_EXCHANGE,ROUTING_KEY_CONFIRMCASE,body);
        log.info("推送[新增確診數]mq開始");
    }

    public void sendMaskInfo(String body){
        rabbitTemplate.convertAndSend(TOPIC_WEBCRAWLER_EXCHANGE,ROUTING_KEY_MASKINFO,body);
        log.info("推送[查詢剩餘口罩數]mq開始");
    }

    public void sendPDFVaccinedAmount(String body){
        rabbitTemplate.convertAndSend(TOPIC_WEBCRAWLER_EXCHANGE,ROUTING_KEY_PDFVaccinedAmount,body);
        log.info("推送[pdf取得各疫苗接踵累计人次]mq開始");
    }
}
