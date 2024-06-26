package com.infotran.springboot.queue.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * RabbitMqConfig
 * @author chris
 */
@Configuration
public class RabbitMqConfig {

    @Value("${webcrawler.mq.exchange}")
    String TOPIC_WEBCRAWLER_EXCHANGE;

    @Value("${webcrawler.mq.confirmcase}")
    String QUEUE_CONFIRMCASE;

    @Value("${webcrawler.mq.maskinfo}")
    String QUEUE_MASKINFO;

    @Value("${webcrawler.mq.PDFVaccinedAmount}")
    String QUEUE_PDFVaccinedAmount;

    @Value("${webcrawler.mq.WorldCovidData}")
    String QUEUE_WORLDCOVIDDATA;

    @Value("${webcrawler.mq.routingkey.confirmcase}")
    String ROUTING_KEY_CONFIRMCASE;

    @Value("${webcrawler.mq.routingkey.maskinfo}")
    String ROUTING_KEY_MASKINFO;

    @Value("${webcrawler.mq.routingkey.PDFVaccinedAmount}")
    String ROUTING_KEY_PDFVaccinedAmount;

    @Value("${webcrawler.mq.routingkey.WorldCovidData}")
    String ROUTING_KEY_WorldCovidData;

    @Value("${spring.rabbitmq.host:127.0.0.1}")
    String address;
    @Value("${spring.rabbitmq.username:chris}")
    String username;
    @Value("${spring.rabbitmq.password:11111111}")
    String password;
    @Value("${spring.rabbitmq.port:5672}")
    Integer port;

    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(address);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setPort(port);
        return connectionFactory;
    }

    /**
     * 將自定義的消息類序列化成json格式，再轉成byte構造 Message，在接收消息時，會將接收到的 Message 再反序列化成自定義的類。
     * @param
     * @return
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    Queue queueConfirmCase() {
        return new Queue(QUEUE_CONFIRMCASE, false);
    }

    @Bean
    Queue queueMaskInfo() {
        return new Queue(QUEUE_MASKINFO, false);
    }

    @Bean
    Queue queuePDFVaccinedAmount() {
        return new Queue(QUEUE_PDFVaccinedAmount, false);
    }

    @Bean
    Queue queueWorldCovidData() {
        return new Queue(QUEUE_WORLDCOVIDDATA, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(TOPIC_WEBCRAWLER_EXCHANGE);
    }

    @Bean
    @DependsOn(value = {"queueConfirmCase","exchange"})
    Binding bindingConfirmCase(Queue queueConfirmCase, TopicExchange exchange) {
        return BindingBuilder.bind(queueConfirmCase).to(exchange).with(ROUTING_KEY_CONFIRMCASE);
    }

    @Bean
    @DependsOn(value = {"queueMaskInfo","exchange"})
    Binding bindingMaskInfo(Queue queueMaskInfo, TopicExchange exchange) {
        return BindingBuilder.bind(queueMaskInfo).to(exchange).with(ROUTING_KEY_MASKINFO);
    }
    @Bean
    @DependsOn(value = {"queuePDFVaccinedAmount","exchange"})
    Binding bindingPDFVaccinedAmount(Queue queuePDFVaccinedAmount, TopicExchange exchange) {
        return BindingBuilder.bind(queuePDFVaccinedAmount).to(exchange).with(ROUTING_KEY_PDFVaccinedAmount);
    }

    @Bean
    @DependsOn(value = {"queueWorldCovidData","exchange"})
    Binding bindingWorldCovidData(Queue queueWorldCovidData, TopicExchange exchange) {
        return BindingBuilder.bind(queueWorldCovidData).to(exchange).with(ROUTING_KEY_WorldCovidData);
    }
}
