package com.example;

import com.example.ioc05.JavaBean;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test02 {
    @Test
    public void test05() {
        //创建ioc容器进行实例化 -> init
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-05.xml");

        JavaBean javaBean = applicationContext.getBean("javaBean", JavaBean.class);
        //Factory工厂也会加入到ioc容器 &id
        Object bean = applicationContext.getBean("&javaBean");
        System.out.println(bean);
        System.out.println(javaBean);
        //正常结束ioc容器 -> destroy
        applicationContext.close();
    }
}
