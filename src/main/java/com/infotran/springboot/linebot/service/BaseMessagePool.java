package com.infotran.springboot.linebot.service;

import com.infotran.springboot.exception.LineBotException;
import com.infotran.springboot.exception.exceptionenum.LineBotExceptionEnums;
import com.infotran.springboot.linebot.service.messagehandler.enums.HandlerEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BaseMessagePool implements InitializingBean, ApplicationContextAware {



    private ApplicationContext applicationContext;

    private List<String> methodList = new ArrayList<>();

    private List<BaseMessageInterface> baseInterfaceList = new ArrayList<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        methodList = HandlerEnum.getAllEnums();
        baseInterfaceList = applicationContext.getBeansOfType(BaseMessageInterface.class).values().stream().collect(Collectors.toList());
    }

    /**
     * 透過enums的名字查詢實現類
     * @param enums HandlerEnum的handlerName
     * @return BaseMessageInterface
     * @throws LineBotException LineBotExceptionEnums.FAIL_ON_IMPLEMENT_GETCLASSNAME
     * */
    public BaseMessageInterface getMethod(String enums) throws Exception{
        for (BaseMessageInterface base : baseInterfaceList){
            if(base.canSupport(enums)){
                return base;
            }
        }
        throw new LineBotException(LineBotExceptionEnums.FAIL_ON_IMPLEMENT_GETCLASSNAME);
    }



}
