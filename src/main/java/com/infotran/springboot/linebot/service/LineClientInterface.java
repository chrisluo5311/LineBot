package com.infotran.springboot.linebot.service;


import com.linecorp.bot.client.LineBlobClient;
import com.linecorp.bot.client.LineMessagingClient;

/**
 * LineMessagingClient 回覆訊息所需token
 * @author chris
 */
public interface LineClientInterface {

    LineMessagingClient CLIENT = LineMessagingClient
            .builder("ww5P0UIpl9jxB+a+dkO44euiFLfNhbUW+uqNjUue+dk/evwcYs/7ySV6iwwatmQwZExhR3polWKVDIJHxXMSehCExemJ4RRny0uFpYdWgwsp+Mi+643shY6fzzH/Ttqbn9iHPB8xm7GAlg7UH/klIwdB04t89/1O/w1cDnyilFU=")
            .build();

    LineBlobClient BLOB_CLIENT = LineBlobClient
            .builder("ww5P0UIpl9jxB+a+dkO44euiFLfNhbUW+uqNjUue+dk/evwcYs/7ySV6iwwatmQwZExhR3polWKVDIJHxXMSehCExemJ4RRny0uFpYdWgwsp+Mi+643shY6fzzH/Ttqbn9iHPB8xm7GAlg7UH/klIwdB04t89/1O/w1cDnyilFU=")
            .build();
}
