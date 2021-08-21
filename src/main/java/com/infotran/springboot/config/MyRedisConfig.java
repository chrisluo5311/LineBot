package com.infotran.springboot.config;

import com.infotran.springboot.ConfirmCase.model.ConfirmCase;
import com.infotran.springboot.LineBot.Model.MenuID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class MyRedisConfig {

    @Primary
    @Bean
    public RedisTemplate<Object, MenuID> menuidRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, MenuID> template = new RedisTemplate<Object, MenuID>();
        template.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer<MenuID> ser = new Jackson2JsonRedisSerializer<MenuID>(MenuID.class);
        template.setDefaultSerializer(ser);
        return template;
    }

    @Bean
    public RedisTemplate<Object, ConfirmCase> confirmCaseRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, ConfirmCase> template = new RedisTemplate<Object, ConfirmCase>();
        template.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer<ConfirmCase> ser = new Jackson2JsonRedisSerializer<ConfirmCase>(ConfirmCase.class);
        template.setDefaultSerializer(ser);
        return template;
    }




}
