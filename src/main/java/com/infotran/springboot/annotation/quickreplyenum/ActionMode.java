package com.infotran.springboot.annotation.quickreplyenum;

import com.linecorp.bot.model.action.CameraAction;
import com.linecorp.bot.model.action.LocationAction;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.message.quickreply.QuickReplyItem;
import lombok.extern.slf4j.Slf4j;

/**
 * ActionMode enum
 * @author chris
 */
@Slf4j
public enum ActionMode implements ActionModeInterface{

    POSTBACK{
        @Override
        public QuickReplyItem getQuickReplyItem(String label,String data,String displayText,String text) {
            //按順序為:label,data,displayText
            return QuickReplyItem.builder()
                    .action(PostbackAction.builder()
                            .label(label)
                            .data(data)
                            .displayText(displayText)
                            .build())
                    .build();
        }
    },

    MESSAGE {
        @Override
        public QuickReplyItem getQuickReplyItem(String label,String data,String displayText,String text) {

            return QuickReplyItem.builder()
                    .action(new MessageAction(label,text))
                    .build();
        }
    },

    LOCATION {
        @Override
        public QuickReplyItem getQuickReplyItem(String label,String data,String displayText,String text) {
            return QuickReplyItem.builder()
                    .action(LocationAction.withLabel(label))
                    .build();
        }
    },

    CAMERA {
        @Override
        public QuickReplyItem getQuickReplyItem(String label, String data, String displayText, String text) {
            return QuickReplyItem.builder()
                    .action(CameraAction.builder().label(label).build())
                    .build();
        }
    }

}
