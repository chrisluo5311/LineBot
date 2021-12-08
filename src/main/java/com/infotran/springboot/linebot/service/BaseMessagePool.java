package com.infotran.springboot.linebot.service;

import com.infotran.springboot.exception.LineBotException;
import com.infotran.springboot.exception.exceptionenum.LineBotExceptionEnums;
import com.infotran.springboot.linebot.service.messagehandler.enums.HandlerEnum;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chris
 */
@Component
@Slf4j
public class BaseMessagePool implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private List<BaseMessageInterface> baseInterfaceList;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        baseInterfaceList = new ArrayList<>(applicationContext.getBeansOfType(BaseMessageInterface.class).values());
        for(BaseMessageInterface base : baseInterfaceList){
            log.info("載入 BaseMessageInterface 實現類: {}",base);
        }
    }

    /**
     * 透過enums的名字查詢實現類
     * @param enums HandlerEnum
     * @return BaseMessageInterface
     * @throws LineBotException 實現類未實作getHandler()方法
     * */
    public BaseMessageInterface getMethod(HandlerEnum enums) throws Exception{
        for (BaseMessageInterface base : baseInterfaceList){
            if(base.canSupport(enums)){
                return base;
            }
        }
        throw new LineBotException(LineBotExceptionEnums.FAIL_ON_IMPLEMENT_GETCLASSNAME);
    }

}
