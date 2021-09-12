package com.infotran.springboot.linebot.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.linecorp.bot.model.action.LocationAction;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.quickreply.QuickReply;
import com.linecorp.bot.model.message.quickreply.QuickReplyItem;
import org.springframework.stereotype.Service;

@Service
public class MessageQuickReplySupplier implements Supplier<Message> {

    @Override
    public Message get() {
        final List<QuickReplyItem> items = Arrays.<QuickReplyItem>asList(
                QuickReplyItem.builder()
                        .action(LocationAction.withLabel("打開地圖"))
                        .build()
        );

        final QuickReply quickReply = QuickReply.items(items);

        return TextMessage
                .builder()
                .text("請點選下方快捷鍵")
                .quickReply(quickReply)
                .build();
    }


}
