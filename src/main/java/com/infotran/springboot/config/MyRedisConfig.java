package com.infotran.springboot.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.infotran.springboot.ConfirmCase.model.ConfirmCase;
import com.infotran.springboot.LineBot.Model.MenuID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class MyRedisConfig {

    private StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

    private Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

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


    public CacheManager confirmCaseRedisCacheManager (RedisConnectionFactory factory) {
        // 解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        //POJO无public的属性或方法时，不报错
        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // null值字段不显示
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 序列化JSON串时，在值上打印出对象类型
        // om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        // 替换上方 过期的enableDefaultTyping
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance ,ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);
        jackson2JsonRedisSerializer.setObjectMapper(om); // 解决jackson2无法反序列化LocalDateTime的问题
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.registerModule(new JavaTimeModule()); // 配置序列化（解决乱码的问题）
        RedisCacheConfiguration config = RedisCacheConfiguration
                                        .defaultCacheConfig()
                                        .entryTtl(Duration.ofDays(30))
                                        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringRedisSerializer))
                                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                                        .disableCachingNullValues();
        RedisCacheManager cacheManager = RedisCacheManager.builder(factory).cacheDefaults(config).build();
        return cacheManager;
    }




}
