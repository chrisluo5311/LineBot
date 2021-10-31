package com.infotran.springboot.Queue.receiver;

import com.infotran.springboot.exception.LineBotException;
import com.infotran.springboot.webcrawler.confirmcase.service.GetCovidNumService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@RabbitListener(queues = "${webcrawler.mq.confirmcase}")
public class Receiver {

    @Resource
    GetCovidNumService getCovidNumService;

    @RabbitHandler
    public void receiveMessage(@Payload String json) throws LineBotException {
        System.out.println("Received message:" + json);
        String detailedUrl = getCovidNumService.getURLOfNewsDetail(json);
        getCovidNumService.parseBody(detailedUrl);
    }

}
