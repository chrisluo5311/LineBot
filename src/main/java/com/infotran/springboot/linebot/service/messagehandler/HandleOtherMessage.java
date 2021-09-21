package com.infotran.springboot.linebot.service.messagehandler;

import com.infotran.springboot.linebot.service.BaseMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author chris
 * 第六功能
 * 處理其他功能
 *
 * */
@Slf4j
@Component(value = "HandleOtherMessage")
public class HandleOtherMessage extends BaseMessageHandler {

    /**
     * 處理其他
     * @param replyToken String
     * */
    public void handleOtherMessageReply(StringBuilder message,String replyToken){
        message.append("⊂_ヽ\n" +
                "　 ＼＼ ＿\n" +
                "　　 ＼(　•_•) F\n" +
                "　　　 <　⌒ヽ A\n" +
                "　　　/ 　 へ＼ B\n" +
                "　　 /　　/　＼＼ U\n" +
                "　　 ﾚ　ノ　　 ヽ_つ L\n" +
                "　　/　/ O\n" +
                "　 /　/| U\n" +
                "　(　(ヽ S\n" +
                "　|　|、＼\n" +
                "　| 丿 ＼ ⌒)\n" +
                "　| |　　) /\n" +
                "`ノ )　　Lﾉ\n" +
                "(_／");
        replyText(replyToken,message.toString());
    }
}
