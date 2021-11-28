package com.infotran.springboot.annotation.quickreplyenum;

import com.linecorp.bot.model.message.quickreply.QuickReplyItem;

/**
 * ActionMode自訂義方法interface
 *
 * @author chris
 *
 * */
public interface ActionModeInterface {

    /**
     * 依據不同ActionMode
     * 執行不同方法並取得QuickReplyItem
     * @param label
     * @param data
     * @param displayText
     * @param text
     *
     * */
    QuickReplyItem getQuickReplyItem(String label,String data,String displayText,String text);
}
