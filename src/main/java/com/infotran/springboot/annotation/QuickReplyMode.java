package com.infotran.springboot.annotation;

import com.infotran.springboot.annotation.quickreplyenum.ActionMode;
import org.springframework.scheduling.annotation.Schedules;

import java.lang.annotation.*;

/**
 *  QuickReply模式目前僅三種:
 *  POSTBACK,MESSAGE,LOCATION
 *  TODO URI,DATETIMEPICKER,CAMERA,CAMERA_ROLL
 *
 * */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(MultiQuickReply.class)
public @interface QuickReplyMode {

    /**
     *  QuickReply模式
     *  @return
     * */
    ActionMode mode();

    /**
     * 快捷鍵上顯示的文字
     * @return
     * */
    String label();


    /**
     *  回傳值
     *  @return
     * */
    String data() default "";

    /**
     *  使用者按下後傳送的字
     *  @return
     * */
    String displayText() default "";

    /**
     *  傳給使用者的文字訊息
     *  @return
     * */
    String text() default "";


}
