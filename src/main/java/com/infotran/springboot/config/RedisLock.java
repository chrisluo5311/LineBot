package com.infotran.springboot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 僅一主機運行
 * 不做看門狗監聽機制
 *
 * @author chris
 */
@Slf4j
@Component
public class RedisLock {

    @Resource
    RedisTemplate<Object, String> stringRedisTemplate;

    public boolean lock(Integer lockSecondsTime,String key){
        return lock(key,lockSecondsTime,TimeUnit.SECONDS);
    }

    public boolean lock(String key, long expire, TimeUnit timeUnit){
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(key, key, expire, timeUnit);
        log.info("redis lock key: [{}],result: [{}]",key,result);
        return result==null?false:result;
    }

    public boolean isLock(String key){
        Boolean result = stringRedisTemplate.hasKey(key);
        return result==null?false:result;
    }

    public void unlock(String key){
        stringRedisTemplate.delete(key);
    }

}
