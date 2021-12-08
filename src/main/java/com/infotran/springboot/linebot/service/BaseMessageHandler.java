package com.infotran.springboot.linebot.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.infotran.springboot.exception.LineBotException;
import com.infotran.springboot.exception.exceptionenum.LineBotExceptionEnums;
import com.infotran.springboot.linebot.service.messagehandler.enums.HandlerEnum;
import com.infotran.springboot.webcrawler.confirmcase.service.ConfirmCaseService;
import com.infotran.springboot.webcrawler.medicinestore.service.MedicineStoreService;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * BaseMessageHandler
 * @author chris
 */
@Slf4j
@Component
public abstract class BaseMessageHandler extends BaseMessageTemplate implements BaseMessageInterface {

    private static final String LOG_PREFIX = "BaseMessageHandler";

    @Resource
    public ConfirmCaseService confirmCaseService;

    @Resource
    public MedicineStoreService medicineStoreService;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean canSupport(HandlerEnum handlerEnum) {
        return getHandler().equals(handlerEnum);
    }

    @Override
    public BotApiResponse postBackReply(PostbackEvent event,String data) {
        BotApiResponse botApiResponse = null;
        String replyToken = event.getReplyToken();
        log.info("[{}] postBackReply方法的event data: {}",LOG_PREFIX,data);
        switch (data){
            case "國內外疫情" :
                break;
            case "其他" :
                List<Message> textList = new ArrayList<>(textMessageReply(event));
                botApiResponse = reply(replyToken,textList);
                break;
            default:
        }
        return botApiResponse;
    }

    /**
     * 回覆順序 : <br>
     * 1. 處理文字訊息(可 null) <br>
     * 2. 功能訊息(確診數目、藥局地址、統計圖、貼圖...等等)
     * */
    @Override
    public final <T extends MessageContent> BotApiResponse handleMessageEvent(MessageEvent<T> event) throws Exception{
        //replyToken
        String replyToken = event.getReplyToken();
        //source
        Source source = event.getSource();
        String userId = source.getUserId();
        log.info("[{}] 使用者id: {}",LOG_PREFIX,userId);

        if(event.getMessage() instanceof TextMessageContent){
            //1. 處理文字(可null)
            List<TextMessage> textMessageList = textMessageReply((TextMessageContent)event.getMessage(),replyToken,userId);
            if (Objects.nonNull(textMessageList)){
                Method textMethod = this.getClass().getDeclaredMethod("textMessageReply",TextMessageContent.class,String.class,String.class);
                //使用@MultiQuickReply或@QuickReplyMode自動產生QuickReply，若為混合型回復需自行實作
                return executeReply(textMethod,textMessageList,replyToken);
            }
        } else if (event.getMessage() instanceof LocationMessageContent) {
            //2. 功能訊息-(處理使用者地址並回傳藥局資訊/可null)
            List<LocationMessage> locationMessageList = handleLocationMessageReply((LocationMessageContent) event.getMessage(),userId);
            if(Objects.nonNull(locationMessageList)){
                Method locationMethod = this.getClass().getDeclaredMethod("handleLocationMessageReply",LocationMessageContent.class,String.class);
                return executeReply(locationMethod,locationMessageList,replyToken);
            }
        } else if (event.getMessage() instanceof  StickerMessageContent) {
            //處理貼圖(如果是官方貼圖，預設回覆一樣)
            handleSticker(replyToken, (StickerMessageContent) event.getMessage());
        }
        throw new LineBotException(LineBotExceptionEnums.NO_SUCH_MESSAGE_EVENT);
    }

}
