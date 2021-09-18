package com.infotran.springboot;

public abstract class BaseLineBotTest {


    public String execute(String arg){
        System.out.println("father: " + arg);
        return arg;
    }
}
