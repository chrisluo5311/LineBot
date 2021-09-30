package com.infotran.springboot.linebot.service.messagehandler;

import com.infotran.springboot.linebot.service.BaseMessageHandler;
import com.infotran.springboot.linebot.service.messagehandler.enums.HandlerEnum;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.DatetimePickerAction;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImagemapMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.TextMessage.Emoji;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.response.BotApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author chris
 * 測試及做筆記用
 * 編號: 0 <br>
 *
 * */
@Slf4j
@Component
public class HandleTextMessage extends BaseMessageHandler {

	private ReplyMessage replyMessage = null;

	private static String LOG_PREFIX = "TestReplyMessageHandler";

	@Override
	public String getClassName() {
		return HandlerEnum.getHandlerName(0);
	}

	@Override
	public List<TextMessage> textMessageReply(TextMessageContent event,String replyToken,String userId){
			BotApiResponse botApiResponse = null;
			String receivedMessage = event.getText();
			switch (receivedMessage) {
				case "測試文字":
					List<Emoji> emojis = new ArrayList<Emoji>();
					Emoji emoji = Emoji.builder().index(4).productId("5ac1bfd5040ab15980c9b435").emojiId("002").build();
					emojis.add(emoji);
					TextMessage textMessage = TextMessage.builder().text("測試成功$").emojis(emojis).build();
					return Collections.singletonList(textMessage);
	//				botApiResponse = reply(replyToken,textMessage);
				case "測試carousel":
					URI imageuri = URI.create("https://www.ballarataquaticcentre.com/wp-content/uploads/2020/07/mask-icon.jpg");
					CarouselTemplate carouselTemplate = new CarouselTemplate(
							Arrays.asList(
									new CarouselColumn(imageuri, "hoge", "fuga", Arrays.asList(
											new URIAction("Go to line.me",
													imageuri, null),
											new URIAction("Go to line.me",
													URI.create("https://line.me"), null),
											new PostbackAction("Say hello1",
													"???")
									)),
									new CarouselColumn(imageuri, "hoge", "fuga", Arrays.asList(
											new PostbackAction("言 hello2",
													"??",
													"??"),
											new PostbackAction("言 hello2",
													"??",
													"??"),
											new MessageAction("Say message",
													"Rice=米")
									)),
									new CarouselColumn(imageuri, "Datetime Picker",
											"Please select a date, time or datetime", Arrays.asList(
											DatetimePickerAction.OfLocalDatetime
													.builder()
													.label("Datetime")
													.data("action=sel")
													.initial(LocalDateTime.parse("2017-06-18T06:15"))
													.min(LocalDateTime.parse("1900-01-01T00:00"))
													.max(LocalDateTime.parse("2100-12-31T23:59"))
													.build(),
											DatetimePickerAction.OfLocalDate
													.builder()
													.label("Date")
													.data("action=sel&only=date")
													.initial(LocalDate.parse("2017-06-18"))
													.min(LocalDate.parse("1900-01-01"))
													.max(LocalDate.parse("2100-12-31"))
													.build(),
											DatetimePickerAction.OfLocalTime
													.builder()
													.label("Time")
													.data("action=sel&only=time")
													.initial(LocalTime.parse("06:15"))
													.min(LocalTime.parse("00:00"))
													.max(LocalTime.parse("23:59"))
													.build()
									))
							));
					TemplateMessage templateMessage = new TemplateMessage("Carousel alt text", carouselTemplate);
					break;
				default:
					break;
			}
		return null;
	}

	@Override
	protected List<TextMessage> textMessageReply(PostbackEvent event) {
		return null;
	}

	@Override
	protected List<ImagemapMessage> handleImagemapMessageReply(PostbackEvent event) {
		//不使用
		return null;
	}

	@Override
	protected List<LocationMessage> handleLocationMessageReply(LocationMessageContent event,String userId) {
		//不使用
		return null;
	}


}
