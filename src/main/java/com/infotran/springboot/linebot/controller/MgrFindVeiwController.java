package com.infotran.springboot.linebot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class MgrFindVeiwController {

    private static final String LOG_PREFIX = "[MgrFindVeiwController]";

    @GetMapping(value="/")
    public String home(){
        log.info("{} 進入首頁拉!!!",LOG_PREFIX);
        return "index";
    }
}
