package com.example;

import com.example.ioc03.Main;
import com.example.ioc04.JavaBean2;
import com.example.ioc05.JavaBean;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test01 {

    public void createIoC() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-03.xml");
    }

    @Test
    public void getBeanFromIoC() {
        //创建IOC容器对象
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext();
        applicationContext.setConfigLocations("spring-03.xml");
        applicationContext.refresh();

        //读取ioc容器的组件
        //根据beanId获取 返回Object 需要强转（不推荐）
        Main main = (Main) applicationContext.getBean("main");

        //根据beanId 指定bean的类型class
        Main main1 = applicationContext.getBean("main", Main.class);

        //根据类型获取
        Main main2 = applicationContext.getBean(Main.class);

        main2.print();

        System.out.println(main == main1);
        System.out.println(main2 == main1);

    }

    @Test
    public void test04() {
        //创建ioc容器进行实例化 -> init
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-04.xml");

        JavaBean2 bean = applicationContext.getBean(JavaBean2.class);
        JavaBean2 bean1 = applicationContext.getBean(JavaBean2.class);
        System.out.println(bean1 == bean);
        //正常结束ioc容器 -> destroy
        applicationContext.close();
    }

}
