package com.infotran.springboot.linebot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author chris
 */
@Slf4j
@Controller
public class MgrFindViewController {

    @GetMapping(value="/")
    public String home(){
        return "index";
    }
}
