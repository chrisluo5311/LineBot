package com.infotran.springboot.LineBot.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import com.infotran.springboot.LineBot.service.LineMessageInterface;
import com.linecorp.bot.client.LineBlobClient;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.richmenu.RichMenu;
import com.linecorp.bot.model.richmenu.RichMenuArea;
import com.linecorp.bot.model.richmenu.RichMenuBounds;
import com.linecorp.bot.model.richmenu.RichMenuIdResponse;
import com.linecorp.bot.model.richmenu.RichMenuSize;

public class RichMenuHandler implements LineMessageInterface{
	
	
	public static void main(String[] args) throws FileNotFoundException, InterruptedException, ExecutionException {
		List<RichMenuArea> areas = new ArrayList<>();
		
		RichMenuArea area1 = new RichMenuArea(
								new RichMenuBounds(0,0,833,843),
								new URIAction("Go to line.me",URI.create("https://line.me"), null));
		RichMenuArea area2 = new RichMenuArea(
								new RichMenuBounds(833,0,833,843),
								new URIAction("Go to line.me",URI.create("https://line.me"), null));
		RichMenuArea area3 = new RichMenuArea(
								new RichMenuBounds(1666,0,833,843),
								new URIAction("Go to line.me",URI.create("https://line.me"), null));
		areas.add(area1);
		areas.add(area2);
		areas.add(area3);
		RichMenu richmenu = RichMenu.builder().size(new RichMenuSize(2500,843))
											  .selected(true)
											  .name("firstRichMenu")
											  .chatBarText("功能選單")
											  .areas(areas)
											  .build();
		
		RichMenuIdResponse idresponse = client.createRichMenu(richmenu).get();
		String menuId = idresponse.getRichMenuId();
		FileInputStream fis = new FileInputStream(new File("/static/richmenu.png"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = null;
		byte[] b = new byte[81920];
		int length;
		try {
			while ((length = fis.read(b))!= -1) {
				baos.write(b, 0, length);
			}
			fis.close();
			baos.close();
			buffer = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		LineBlobClient linebotclient = LineBlobClient
										.builder("ww5P0UIpl9jxB+a+dkO44euiFLfNhbUW+uqNjUue+dk/evwcYs/7ySV6iwwatmQwZExhR3polWKVDIJHxXMSehCExemJ4RRny0uFpYdWgwsp+Mi+643shY6fzzH/Ttqbn9iHPB8xm7GAlg7UH/klIwdB04t89/1O/w1cDnyilFU=")
								        .build();
		linebotclient.setRichMenuImage(menuId, "application/json", buffer).get();
		client.linkRichMenuIdToUser("all", menuId);
		client.setDefaultRichMenu(menuId);
	}

}
