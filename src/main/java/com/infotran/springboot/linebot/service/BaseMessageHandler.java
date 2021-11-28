package com.infotran.springboot.linebot.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.infotran.springboot.annotation.QuickReplyMode;
import com.infotran.springboot.webcrawler.confirmcase.service.ConfirmCaseService;
import com.infotran.springboot.webcrawler.medicinestore.service.MedicineStoreService;
import com.linecorp.bot.model.action.LocationAction;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.quickreply.QuickReply;
import com.linecorp.bot.model.message.quickreply.QuickReplyItem;
import com.linecorp.bot.model.response.BotApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * @author chris
 */
@Slf4j
@Component
public abstract class BaseMessageHandler extends BaseMessageUtil implements BaseMessageInterface {

    private static final String LOG_PREFIX = "BaseMessageHandler";

    @Resource
    public ConfirmCaseService caseService;

    @Resource
    public MedicineStoreService mService;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean canSupport(String className) {
        return getClassName().equals(className);
    }

    /**
     * 處理TextMessageContent文字訊息<br>
     * 可搭配使用的annotation : {@link QuickReplyMode}
     *
     * @param event TextMessageContent
     * @param replyToken replyToken
     * @param userId 使用者id
     * @return List<TextMessage>
     *
     * */
    protected abstract List<TextMessage> textMessageReply(TextMessageContent event,String replyToken,String userId);

    /**
     * 處理PostbackEvent文字訊息<br>
     * 可搭配使用的annotation : {@link QuickReplyMode}
     *
     * @param event PostbackEvent
     * @return List<TextMessage>
     *
     * */
    protected abstract List<TextMessage> textMessageReply(PostbackEvent event);

    /**
     * 處理使用者回傳的現在位置
     *
     * @param event  LocationMessageContent
     * @param userId  使用者id
     * @return  List<LocationMessage>
     */
    protected abstract <T extends MessageContent> List<LocationMessage> handleLocationMessageReply(LocationMessageContent event,String userId);

    /**
     * 處理Imagemap回复
     *
     * @param event  PostbackEvent
     * @return  List<Message>
     */
    protected abstract List<Message> handleImagemapMessageReply(PostbackEvent event);

    @Override
    public BotApiResponse postBackReply(PostbackEvent event) {
        BotApiResponse botApiResponse = null;
        String replyToken = event.getReplyToken();
        String data = event.getPostbackContent().getData();
        log.info("[{}] postBackReply方法的event data: {}",LOG_PREFIX,data);
        switch (data){
            case "國內外疫情" :
                break;
            case "其他" :
                List<Message> textList = new ArrayList<>(textMessageReply(event));
                botApiResponse = reply(replyToken,textList);
                break;
            case "refreshfuntion2":

                break;
            default:
        }
        return botApiResponse;
    }

    /**
     * 回覆順序: 處理文字訊息(可 null) -> 功能訊息(確診數目、藥局地址、統計圖、貼圖...等等)
     *
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
                //使用@MultiQuickReply或@QuickReplyMode自動產生QuickReply，若為混和型回復需自行實作
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
        return null;
    }

    /**
     * 測試用<br>
     * 處理使用者回傳的貼圖
     * (預設回復一模一樣的貼圖)
     * @param replyToken String
     * @param content StickerMessageContent
     * */
    public void handleSticker(String replyToken, StickerMessageContent content) {
        //回傳一模一樣的給用戶
        reply(replyToken, new StickerMessage(
                content.getPackageId(), content.getStickerId())
        );
    }

    /**
     * 打開地圖
     * @return TestMessage 訊息帶打開地圖動作的QuickReply
     *
     * */
    public TextMessage openMap() {
        final List<QuickReplyItem> items = List.of(
                QuickReplyItem.builder()
                        .action(LocationAction.withLabel("打開定位"))
                        .build()
        );

        final QuickReply quickReply = QuickReply.items(items);

        return TextMessage.builder().text("點選下方打開地圖").quickReply(quickReply).build();
    }

    

}
