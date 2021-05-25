package com.infotran.springboot.LineBot.service.impl;

import static java.util.Collections.singletonList;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import com.infotran.springboot.LineBot.service.LineMessageInterface;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.DatetimePickerAction;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.TextMessage.Emoji;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.response.BotApiResponse;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReplyMessageHandler implements LineMessageInterface{
	
	private ReplyMessage replyMessage = null;
	
	private static URI createUri(String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().scheme("https").path(path).build().toUri();
    }
	
    private void reply(@NonNull String replyToken, @NonNull Message message) {
        reply(replyToken, singletonList(message));
    }

    private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
        reply(replyToken, messages, false);
    }

    private void reply(@NonNull String replyToken,
                       @NonNull List<Message> messages,
                       boolean notificationDisabled) {
        try {
        	BotApiResponse apiResponse = client
                    .replyMessage(new ReplyMessage(replyToken, messages, notificationDisabled))
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void replyText(@NonNull String replyToken, @NonNull String message) {
        if (replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken must not be empty");
        }
        if (message.length() > 1000) {
            message = message.substring(0, 1000 - 2) + "……";
        }
        this.reply(replyToken, new TextMessage(message));
    }
    
    private LocalDateTime generateLocalDateTime() {
    	return LocalDateTime.now();
    }
    
    private LocalDate genLocalDate() {
    	return LocalDate.now();
    }
    
    private void handleSticker(String replyToken, StickerMessageContent content) {
    	System.out.println("PackageId==>"+content.getPackageId()+"StickerId==>"+content.getStickerId());
        reply(replyToken, new StickerMessage(
                content.getPackageId(), content.getStickerId())
        );
    }
	
	@SuppressWarnings("null")
	public BotApiResponse reply(MessageEvent<TextMessageContent> event)throws IOException{
		
		String receivedMessage = event.getMessage().getText();
	    String replyToken = event.getReplyToken();
	    BotApiResponse botApiResponse = null;
	     
	    switch (receivedMessage) {
		case "測試文字":
			List<Emoji> emojis = new ArrayList<Emoji>();
			Emoji emoji = Emoji.builder().index(4).productId("5ac1bfd5040ab15980c9b435").emojiId("002").build();
			emojis.add(emoji);
			TextMessage textMessage = TextMessage.builder().text("測試成功$").emojis(emojis).build();
			replyMessage = new ReplyMessage(replyToken,textMessage);
			try {
				botApiResponse = client.replyMessage(replyMessage).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			break;
		case "測試carousel":
//			File myfile = new File("D:\\_SpringBoot\\image\\maskon.jpg");
//			URI imageUrl = myfile.toURI();
//			URI imageUrl = createUri("D:\\_SpringBoot\\image\\mask-icon.jpg");
			URI imageUrl = UriComponentsBuilder.fromUriString("https://i.imgur.com/lDZWDH8.jpg").build().toUri();
			CarouselTemplate carouselTemplate = new CarouselTemplate(
                     Arrays.asList(
                             new CarouselColumn(imageUrl, "kitty", "HelloKitty", Arrays.asList(
                                     new URIAction("Go to line.me",
                                                   URI.create("https://line.me"), null),
                                     new URIAction("Go to line.me",
                                                   URI.create("https://line.me"), null),
                                     new PostbackAction("Say hello1",
                                                        "hello こんにちは")
                             )),
                             new CarouselColumn(imageUrl, "hoge", "fuga", Arrays.asList(
                                     new PostbackAction("言 hello2",
                                                        "你好啊",
                                                        "hello こんにちは"),
                                     new PostbackAction("言 hello2",
                                                        "今天如何",
                                                        "hello こんにちは"),
                                     new MessageAction("Say message",
                                                       "Rice=米")
                             )),
                             new CarouselColumn(imageUrl, "Datetime Picker",
                                                "Please select a date, time or datetime", Arrays.asList(
                                     DatetimePickerAction.OfLocalDatetime
                                             .builder()
                                             .label("Datetime")
                                             .data("action=sel")
                                             .initial(this.generateLocalDateTime())
                                             .min(LocalDateTime.parse("1900-01-01T00:00"))
                                             .max(LocalDateTime.parse("2100-12-31T23:59"))
                                             .build(),
                                     DatetimePickerAction.OfLocalDate
                                             .builder()
                                             .label("Date")
                                             .data("action=sel&only=date")
                                             .initial(this.genLocalDate())
                                             .min(LocalDate.parse("1900-01-01"))
                                             .max(LocalDate.parse("2100-12-31"))
                                             .build(),
                                     DatetimePickerAction.OfLocalTime
                                             .builder()
                                             .label("Time")
                                             .data("action=sel&only=time")
                                             .initial(LocalTime.parse("00:00"))
                                             .min(LocalTime.parse("00:00"))
                                             .max(LocalTime.parse("23:59"))
                                             .build()
                             ))
                     ));
             TemplateMessage templateMessage = new TemplateMessage("Carousel alt text", carouselTemplate);
             this.reply(replyToken, templateMessage);
			
			break;
		default:
			this.replyText(
                    replyToken,
                    receivedMessage
            );
			break;
		}
	     
	     return botApiResponse;
	}
	
	
	public void replyPostBack(PostbackEvent event)throws IOException{
		String replyToken = event.getReplyToken();
        this.replyText(replyToken,event.getPostbackContent().getData());
	}
	
	public void stickerHandler(String replyToken, StickerMessageContent content) {
		handleSticker(replyToken,content);
	}
	
}
