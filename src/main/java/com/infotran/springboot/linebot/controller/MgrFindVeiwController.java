package com.infotran.springboot.linebot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MgrFindVeiwController {

    @GetMapping("/")
    public ModelAndView home(){
        return new ModelAndView("/index");
    }
}
