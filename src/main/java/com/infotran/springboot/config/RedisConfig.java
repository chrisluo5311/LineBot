package com.infotran.springboot.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.infotran.springboot.webcrawler.confirmcase.model.ConfirmCase;
import com.infotran.springboot.linebot.model.MenuID;
import com.infotran.springboot.webcrawler.medicinestore.model.MedicineStore;
import com.linecorp.bot.model.message.LocationMessage;
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

/**
 * @author chris
 */
@Configuration
public class RedisConfig {

    private StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();


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

    @Bean
    public RedisTemplate<Object, MedicineStore> medicineStoreRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, MedicineStore> template = new RedisTemplate<Object, MedicineStore>();
        template.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer<MedicineStore> ser = new Jackson2JsonRedisSerializer<MedicineStore>(MedicineStore.class);
        template.setDefaultSerializer(ser);
        return template;
    }

    @Bean
    public RedisTemplate<Object, LocationMessage> locationMessageRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, LocationMessage> template = new RedisTemplate<Object, LocationMessage>();
        template.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer<LocationMessage> ser = new Jackson2JsonRedisSerializer<LocationMessage>(LocationMessage.class);
        template.setDefaultSerializer(ser);
        return template;
    }

    @Bean
    public CacheManager confirmCaseRedisCacheManager (RedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        // ???????????????????????????????????????
        ObjectMapper om = new ObjectMapper();
        //POJO???public?????????????????????????????????
        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // null??????????????????
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // ?????????JSON???????????????????????????????????????
        // om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        // ???????????? ?????????enableDefaultTyping
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance ,ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);
        // ??????jackson2??????????????????LocalDateTime?????????
        jackson2JsonRedisSerializer.setObjectMapper(om);
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // ??????????????????????????????????????????
        om.registerModule(new JavaTimeModule());
        RedisCacheConfiguration config = RedisCacheConfiguration
                                        .defaultCacheConfig()
                                        .entryTtl(Duration.ofDays(1))
                                        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringRedisSerializer))
                                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                                        .disableCachingNullValues();
        RedisCacheManager cacheManager = RedisCacheManager.builder(factory).cacheDefaults(config).build();
        return cacheManager;
    }




}
