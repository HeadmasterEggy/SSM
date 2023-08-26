package com.example;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test01 {

    public void createIoC() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-03.xml");
    }

    public void getBeanFromIoC(){

    }
}
