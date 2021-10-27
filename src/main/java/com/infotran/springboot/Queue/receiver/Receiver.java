package com.infotran.springboot.Queue.receiver;

import com.infotran.springboot.Queue.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

    @RabbitListener(queues = RabbitMqConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        System.out.println("Received message:" + message);
    }

}
