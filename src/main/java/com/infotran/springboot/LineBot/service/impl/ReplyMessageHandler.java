package com.infotran.springboot.LineBot.service.impl;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;

import com.infotran.springboot.ConfirmCase.model.ConfirmCase;
import com.infotran.springboot.ConfirmCase.service.ConfirmCaseService;
import com.infotran.springboot.MedicineStore.Model.MedicineStore;
import com.infotran.springboot.MedicineStore.Service.MedicineStoreService;
import com.linecorp.bot.model.action.*;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.message.*;
import com.linecorp.bot.model.message.quickreply.QuickReply;
import com.linecorp.bot.model.message.quickreply.QuickReplyItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.infotran.springboot.LineBot.service.LineMessaging;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage.Emoji;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.response.BotApiResponse;

import lombok.NonNull;

@Service
@Slf4j
public class ReplyMessageHandler implements LineMessaging {

	@Autowired
	private ConfirmCaseService caseService;

	@Autowired
	private MedicineStoreService mService;
	
	private ReplyMessage replyMessage = null;

	private Double[] topTenCloestMedicineStore = new Double[]{};

	private Map<Double,Long> medicineStoreMap = new HashMap<>();
	
	private static URI createUri(String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                                          .scheme("https")
                                          .path(path).build()
                                          .toUri();
    }
	
	
	private void reply(@NonNull String replyToken, @NonNull Message message) {
        reply(replyToken, Collections.singletonList(message));
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

    private double distance (double lat1,double lon1,double lat2,double lon2){
		if ((lat1 == lat2) && (lon1 == lon2)){
			return 0;
		}else {
			double theta = lon1 - lon2;
			double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
			dist = Math.acos(dist);
			dist = Math.toDegrees(dist);
			dist = dist * 60 * 1.1515 * 1.609344;
//			dist = dist * 1.609344;
			return (dist);
		}
	}
	
    
    public void handleSticker(String replyToken, StickerMessageContent content) {
        reply(replyToken, new StickerMessage(
                content.getPackageId(), content.getStickerId())
        );
    }

    public void handleLocationMessageRely(MessageEvent<LocationMessageContent> event) {
		String replyToken = event.getReplyToken();
		Double  lat1 = event.getMessage().getLatitude();
		Double long1 = event.getMessage().getLongitude();
		Double lat2 ;
		Double long2 ;
		List<MedicineStore> medStoreList = mService.findAll();
		for (int i = 0 ; i < medStoreList.size(); i ++) {
			MedicineStore store = medStoreList.get(i);
			lat2 = store.getLatitude();
			long2 = store.getLongitude();
			Double dist = distance(lat1,long1,lat2,long2);
			medicineStoreMap.put(dist,store.getId());
			topTenCloestMedicineStore[i] = dist;
		}
		Arrays.sort(topTenCloestMedicineStore);
		List<Message> locationList = new ArrayList<>();

		final List<QuickReplyItem> items = Arrays.<QuickReplyItem>asList(
				QuickReplyItem.builder()
						.action(PostbackAction.builder()
								.label("下5間")
								.data("下5間")
								.displayText("下5間")
								.build())
						.build()
		);

		final QuickReply quickReply = QuickReply.items(items);

		for (int i = 0 ; i < 5 ; i++) {
			Long id = medicineStoreMap.get(topTenCloestMedicineStore[i]);
			MedicineStore eachStore = mService.findById(id);
			String name = eachStore.getName();
			String address = eachStore.getAddress();
			Double latitude = eachStore.getLatitude();
			Double longitude = eachStore.getLongitude();
			LocationMessage locationMessage = LocationMessage.builder()
					                                         .title(name)
					                                         .address(address)
					                                         .latitude(latitude)
					                                         .longitude(longitude)
					 									     .quickReply(quickReply)
					                                         .build();
			locationList.add(locationMessage);
		}

		this.reply(replyToken,locationList);
	}


	
	public void postBackReply(PostbackEvent event)throws IOException{
		String replyToken = event.getReplyToken();
		String data = event.getPostbackContent().getData();
		StringBuilder message = new StringBuilder();
		switch (data){
			case "1" :
				ConfirmCase confirmCase = caseService.findByConfirmTime(LocalDate.now());
				if (confirmCase!=null){
					message.append("指揮中心快訊：今日新增"+ confirmCase.getTodayAmount() + "例COVID-19確定病例。\n");
					message.append("校正回歸數"+confirmCase.getReturnAmount()+"例。\n");
					message.append("死亡人數"+confirmCase.getDeathAmount()+"例。");
				}else {
					message.append("本日確診數量尚未公布。");
				}
				this.replyText(replyToken,message.toString());
				break;
			case "2" :
				this.reply(replyToken,new MessageQuickReplySupplier().get());
				break;
			case "3" :
				break;
			case "4" :
				break;
			case "5" :
				break;
			case "6" :
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
				this.replyText(replyToken,message.toString());
				break;
			case "下5間" :
				List<Message> locationList = new ArrayList<>();
				for (int i = 5 ; i < 10 ; i++){
					Long id = medicineStoreMap.get(topTenCloestMedicineStore[i]);
					MedicineStore eachStore = mService.findById(id);
					String name = eachStore.getName();
					String address = eachStore.getAddress();
					Double latitude = eachStore.getLatitude();
					Double longitude = eachStore.getLongitude();
					LocationMessage locationMessage = LocationMessage.builder()
							.title(name)
							.address(address)
							.latitude(latitude)
							.longitude(longitude)
							.build();
					locationList.add(locationMessage);
				}
				this.reply(replyToken,locationList);
		}
	}

	/*
	 * 測試用
	 * */
	@SuppressWarnings("null")
	public BotApiResponse textMessageReply(MessageEvent<TextMessageContent> event)throws IOException{

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
	
}
