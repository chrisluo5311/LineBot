package com.infotran.springboot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class BaseLineBotTest implements TestInterface{

    @Override
    public String execute(String arg){
        System.out.println("father: " + arg);
        switch (arg){
            case "a":
                break;
            default:
                System.out.println("hi");
                return "hello";
        }
        return null;
    }
}
