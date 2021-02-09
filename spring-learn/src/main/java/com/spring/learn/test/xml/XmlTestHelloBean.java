package com.spring.learn.test.xml;


import com.spring.learn.config.HelloWorldBean;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class XmlTestHelloBean {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
		HelloWorldBean bean1 = context.getBean(HelloWorldBean.class);
		bean1.hello();
	}

}
