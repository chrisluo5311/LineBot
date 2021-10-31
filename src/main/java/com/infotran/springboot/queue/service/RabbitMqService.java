package com.infotran.springboot.queue.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class RabbitMqService {

    @Value("${webcrawler.mq.exchange}")
    String TOPIC_WEBCRAWLER_EXCHANGE;

    @Value("${webcrawler.mq.routingkey.confirmcase}")
    String ROUTING_KEY_CONFIRMCASE;

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendConfirmCase(String body){
        rabbitTemplate.convertAndSend(TOPIC_WEBCRAWLER_EXCHANGE,ROUTING_KEY_CONFIRMCASE,body);
        log.info("推送[新增確診數]mq開始");
    }
}
